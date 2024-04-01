package com.dsbie.rearend.manager.datasource.jdbc;


import com.dsbie.rearend.KToolsContext;
import com.dsbie.rearend.config.ConfigParamUtil;
import com.dsbie.rearend.common.utils.DataSourceUtil;
import com.dsbie.rearend.common.utils.StreamUtil;
import com.dsbie.rearend.exception.KToolException;
import com.dsbie.rearend.manager.datasource.KDataSourceHandler;
import com.dsbie.rearend.manager.datasource.jdbc.model.TableColumn;
import com.dsbie.rearend.manager.datasource.jdbc.model.TableMetadata;
import com.dsbie.rearend.mybatis.MybatisContext;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * JDBC 处理器
 *
 * @author WCG
 */
@Slf4j
public abstract class AbstractJdbcHandler implements KDataSourceHandler {

    protected static final String SQL_EXEC_RESULT = "result";

    protected final Properties properties;

    protected final JdbcConfig jdbcConfig;

    protected AbstractJdbcHandler(Properties properties) throws KToolException {
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

    protected abstract String getDriverClass();

    protected abstract SQLType getSqlTypeByJdbcType(String typeName);

}
