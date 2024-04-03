package com.dsbie.rearend.manager.datasource.type.jdbc;


import com.dsbie.rearend.KToolsContext;
import com.dsbie.rearend.common.utils.DataSourceUtil;
import com.dsbie.rearend.common.utils.StreamUtil;
import com.dsbie.rearend.config.ConfigParamUtil;
import com.dsbie.rearend.exception.KToolException;
import com.dsbie.rearend.job.element.BaseColumn;
import com.dsbie.rearend.job.element.BaseRow;
import com.dsbie.rearend.job.element.DataType;
import com.dsbie.rearend.job.model.JobModel;
import com.dsbie.rearend.job.model.SinkConfig;
import com.dsbie.rearend.job.model.SourceConfig;
import com.dsbie.rearend.manager.datasource.KDataSourceHandler;
import com.dsbie.rearend.manager.datasource.type.jdbc.model.TableColumn;
import com.dsbie.rearend.manager.datasource.type.jdbc.model.TableMetadata;
import com.dsbie.rearend.mybatis.MybatisContext;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JDBC 处理器
 *
 * @author WCG
 */
@Slf4j
public abstract class AbstractJdbcHandler implements KDataSourceHandler {

    protected final Properties properties;

    protected final JdbcConfig jdbcConfig;

    public AbstractJdbcHandler(Properties properties) throws KToolException {
        this.properties = properties;
        this.jdbcConfig = ConfigParamUtil.buildConfig(properties, JdbcConfig.class);
        this.jdbcConfig.setKey(UUID.randomUUID().toString());
        this.jdbcConfig.setDriver(getDriverClass());
    }

    @Override
    public void close() {
        disConn();
    }

    @Override
    public void testConn() throws KToolException {
        try (Connection ignored = DriverManager.getConnection(jdbcConfig.getJdbcUrl(), jdbcConfig.getUsername(), jdbcConfig.getPassword())) {
            log.info("数据源连接测试成功！");
        } catch (SQLException e) {
            throw new KToolException("测试连接失败", e);
        }
    }

    @Override
    public void conn() {
        MybatisContext mybatisContext = KToolsContext.getInstance().getMybatisContext();
        if (!mybatisContext.existDataSource(jdbcConfig.getKey())) {
            synchronized (this) {
                if (!mybatisContext.existDataSource(jdbcConfig.getKey())) {
                    DataSource dataSource = DataSourceUtil.createDataSource(jdbcConfig);
                    mybatisContext.addDataSource(jdbcConfig.getKey(), dataSource);
                }
            }
        }
    }

    @Override
    public void disConn() {
        MybatisContext mybatisContext = KToolsContext.getInstance().getMybatisContext();
        if (mybatisContext.existDataSource(jdbcConfig.getKey())) {
            synchronized (this) {
                if (mybatisContext.existDataSource(jdbcConfig.getKey())) {
                    mybatisContext.removeDataSource(jdbcConfig.getKey());
                }
            }
        }
    }

    @Override
    public List<String> selectAllSchema() throws KToolException {
        try (Connection connection = getDataSource().getConnection();
             ResultSet schemas = connection.getMetaData().getSchemas()
        ) {
            return StreamUtil.buildStream(schemas)
                    .map(map -> String.valueOf(map.get("TABLE_SCHEM")))
                    .toList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> selectAllTable(String schema) throws KToolException {
        try (Connection connection = getDataSource().getConnection();
             ResultSet tables = connection.getMetaData().getTables(null, schema, "%", new String[]{"TABLE"})
        ) {
            return StreamUtil.buildStream(tables)
                    .map(map -> String.valueOf(map.get("TABLE_NAME")))
                    .toList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TableMetadata selectTableMetadata(String schema, String tableName) throws KToolException {
        TableMetadata metadata = new TableMetadata();
        metadata.setSchema(schema);
        metadata.setTableName(tableName);

        try (Connection connection = getDataSource().getConnection();
             ResultSet primaryKeys = connection.getMetaData().getPrimaryKeys(null, schema, tableName);
             ResultSet columns = connection.getMetaData().getColumns(null, schema, tableName, "%")
        ) {
            // 获取主键信息的结果集
            List<String> primaryKeyList = StreamUtil.buildStream(primaryKeys)
                    .map(map -> String.valueOf(map.get("COLUMN_NAME")))
                    .toList();
            // 处理字段
            Map<String, TableColumn> columnMap = StreamUtil.buildStream(columns).map(column -> {
                TableColumn columnTemp = new TableColumn();
                columnTemp.setName(String.valueOf(column.get("COLUMN_NAME")));
                columnTemp.setDataType(getSqlTypeByJdbcType(String.valueOf(column.get("TYPE_NAME"))));
                columnTemp.setPrimaryKey(primaryKeyList.contains(columnTemp.getName()));
                columnTemp.setNullable(ResultSetMetaData.columnNullable == Integer.parseInt(String.valueOf(column.get("NULLABLE"))));
                columnTemp.setLength(Integer.parseInt(String.valueOf(column.get("COLUMN_SIZE"))));
                columnTemp.setPrecision(Integer.parseInt(String.valueOf(column.get("DECIMAL_DIGITS"))));
                return columnTemp;
            }).collect(Collectors.toMap(TableColumn::getName, Function.identity(), (tableColumn, tableColumn2) -> {
                throw new RuntimeException("列名冲突！");
            }, LinkedHashMap::new));

            // 赋值给元数据
            metadata.setColumns(columnMap);
            // 返回结果
            return metadata;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected DataSource getDataSource() throws KToolException {
        MybatisContext mybatisContext = KToolsContext.getInstance().getMybatisContext();
        return mybatisContext.getDataSource(jdbcConfig.getKey());
    }

    @Override
    public void selectData(JobModel jobModel, Consumer<Stream<BaseRow>> consumer) {
        SourceConfig sourceConfig = jobModel.getSourceConfig();
        String finalSql = sourceConfig.getConfigValue();
        try (Connection connection = getDataSource().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(finalSql)
        ) {
            // 查询字段类型
            Map<String, DataType> fieldType = new HashMap<>();
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columnCount = resultSetMetaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                fieldType.put(resultSetMetaData.getColumnName(i), getDataTypeByJdbcType(resultSetMetaData.getColumnTypeName(i)));
            }

            // 构建流
            Stream<Map<String, Object>> mapStream = StreamUtil.buildStream(resultSet);
            // 转换数据
            Stream<BaseRow> rowStream = mapStream.map(map -> {
                BaseRow baseRow = new BaseRow(map.size());
                map.forEach((fieldName, fieldValue) -> {
                    BaseColumn baseColumn = BaseColumn.create(fieldName, fieldValue, fieldType.get(fieldName));
                    baseRow.addField(baseColumn);
                });
                return baseRow;
            });
            // 回调
            consumer.accept(rowStream);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void syncData(JobModel jobModel, Stream<BaseRow> stream) {
        SinkConfig sinkConfig = jobModel.getSinkConfig();
        if (sinkConfig.getBatchSize() == null) {
            sinkConfig.setBatchSize(1000);
        }
        final TableMetadata tableMetadata = selectTableMetadata(sinkConfig.getSchema(), sinkConfig.getTableName());
        final List<String> primaryKey = tableMetadata.getPrimaryKey();
        final List<Row> baseRows = new ArrayList<>();
        stream.filter(baseRow -> {
            // 检查主键数据完整性
            boolean whole = true;
            for (String key : primaryKey) {
                BaseColumn field = baseRow.getField(key);
                if (field == null || field.getData() == null) {
                    whole = false;
                }
            }
            return whole;
        }).map(baseRow -> {
            // 转换为mybatis认识的row
            Row row = new Row();
            tableMetadata.getColumns().values().forEach(tableColumn -> {
                BaseColumn field = baseRow.getField(tableColumn.getName());
                Optional.ofNullable(field)
                        .ifPresent(baseColumn -> {
                            Object value = getDataTypeByJdbcType(tableColumn.getDataType().getName()).convertData(baseColumn.getData());
                            row.set(tableColumn.getName(), value);
                        });
            });
            return row;
        }).forEach(row -> {
            // 开始落库
            baseRows.add(row);
            // 判断缓存大小
            if (baseRows.size() >= sinkConfig.getBatchSize()) {
                // 批量存库
                batchSaveData(tableMetadata.getSchema(), tableMetadata.getTableName(), new ArrayList<>(baseRows));
                KToolsContext.getInstance().getJobContext().log(jobModel.getJobId(), "成功存储数据：" + sinkConfig.getBatchSize());
                baseRows.clear();
            }
        });
        // 批量存库
        if (!baseRows.isEmpty()) {
            batchSaveData(tableMetadata.getSchema(), tableMetadata.getTableName(), new ArrayList<>(baseRows));
            KToolsContext.getInstance().getJobContext().log(jobModel.getJobId(), "成功存储数据：" + sinkConfig.getBatchSize());
        }
        baseRows.clear();
    }

    private void batchSaveData(String schema, String tableName, ArrayList<Row> rows) {
        if (!supportBatch()) {
            DataSourceKey.use(jdbcConfig.getKey(), () -> {
                Db.insertBatchWithFirstRowColumns(schema, tableName, rows);
            });
        } else {
            try (Connection connection = getDataSource().getConnection();
                 Statement statement = connection.createStatement();
            ) {
                TransactionManager transactionManager = TransactionManager.createTransactionManager(connection, supportTransaction());
                try {
                    transactionManager.setAutoCommit(false);

                    for (Row row : rows) {
                        String sql = DialectFactory.getDialect().forInsertRow(schema, tableName, row);
                        statement.addBatch(sql);
                    }

                    statement.executeBatch();
                    statement.clearBatch();
                    transactionManager.commit();
                } catch (Exception e) {
                    transactionManager.rollback();
                    throw new RuntimeException(e);
                } finally {
                    transactionManager.setAutoCommit(true);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected boolean supportTransaction() {
        return true;
    }

    protected boolean supportBatch() {
        return true;
    }

    protected abstract String getDriverClass();

    protected abstract SQLType getSqlTypeByJdbcType(String typeName);

    protected abstract DataType getDataTypeByJdbcType(String typeName);

}
