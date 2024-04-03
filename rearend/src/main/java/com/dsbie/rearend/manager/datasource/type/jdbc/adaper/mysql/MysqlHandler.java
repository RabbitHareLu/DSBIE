package com.dsbie.rearend.manager.datasource.type.jdbc.adaper.mysql;

import com.dsbie.rearend.common.utils.StreamUtil;
import com.dsbie.rearend.exception.KToolException;
import com.dsbie.rearend.job.element.DataType;
import com.dsbie.rearend.manager.datasource.type.jdbc.AbstractJdbcHandler;
import com.dsbie.rearend.manager.datasource.type.jdbc.model.TableColumn;
import com.dsbie.rearend.manager.datasource.type.jdbc.model.TableMetadata;
import com.mysql.cj.MysqlType;
import com.mysql.cj.jdbc.Driver;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * mysql数据源处理器
 *
 * @author WCG
 */
public class MysqlHandler extends AbstractJdbcHandler {

    public MysqlHandler(Properties properties) throws KToolException {
        super(properties);
    }

    @Override
    public List<String> selectAllSchema() throws KToolException {
        try (Connection connection = getDataSource().getConnection();
             ResultSet schemas = connection.getMetaData().getCatalogs()
        ) {
            return StreamUtil.buildStream(schemas)
                    .map(map -> String.valueOf(map.get("TABLE_CAT")))
                    .filter(schemaName -> !"information_schema".equalsIgnoreCase(schemaName) && !"performance_schema".equalsIgnoreCase(schemaName))
                    .toList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> selectAllTable(String schema) throws KToolException {
        try (Connection connection = getDataSource().getConnection();
             ResultSet tables = connection.getMetaData().getTables(schema, null, "%", new String[]{"TABLE"})
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
             ResultSet primaryKeys = connection.getMetaData().getPrimaryKeys(schema, null, tableName);
             ResultSet columns = connection.getMetaData().getColumns(schema, null, tableName, "%")
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

    @Override
    protected String getDriverClass() {
        return Driver.class.getName();
    }

    @Override
    protected SQLType getSqlTypeByJdbcType(String typeName) {
        return MysqlType.getByName(typeName);
    }

    @Override
    protected DataType getDataTypeByJdbcType(String typeName) {
        MysqlType mysqlType = MysqlType.getByName(typeName);
        return switch (mysqlType) {
            case BINARY -> DataType.BINARY;
            case FLOAT -> DataType.FLOAT;
            case DOUBLE -> DataType.DOUBLE;
            case TIMESTAMP -> DataType.TIMESTAMP;
            case DECIMAL -> DataType.DECIMAL;
            case INT, BIGINT, SMALLINT, TINYINT -> DataType.INT;
            case DATE -> DataType.DATE;
            case BOOLEAN -> DataType.BOOLEAN;
            case CHAR, VARCHAR, BLOB, TEXT, LONGBLOB, LONGTEXT, TINYBLOB, TINYTEXT -> DataType.STRING;
            default -> throw new RuntimeException("暂不支持的数据类型:" + mysqlType.getName());
        };
    }

}
