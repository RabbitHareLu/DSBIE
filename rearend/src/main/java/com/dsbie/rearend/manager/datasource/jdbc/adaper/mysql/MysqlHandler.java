package com.dsbie.rearend.manager.datasource.jdbc.adaper.mysql;

import com.dsbie.rearend.common.utils.StreamUtil;
import com.dsbie.rearend.exception.KToolException;
import com.dsbie.rearend.manager.datasource.jdbc.AbstractJdbcHandler;
import com.mysql.cj.MysqlType;
import com.mysql.cj.jdbc.Driver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.List;
import java.util.Properties;

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
                    .toList();
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

}
