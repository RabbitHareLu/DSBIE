package com.dsbie.rearend.manager.datasource.jdbc.adaper.mysql;

import com.dsbie.rearend.exception.KToolException;
import com.dsbie.rearend.manager.datasource.jdbc.AbstractJdbcHandler;
import com.mysql.cj.jdbc.Driver;

import java.sql.SQLType;
import java.util.Properties;

/**
 * mysql数据源处理器
 *
 * @author WCG
 */
public class MysqlHandler extends AbstractJdbcHandler {

    protected MysqlHandler(Properties properties) throws KToolException {
        super(properties);
    }

    @Override
    protected String getDriverClass() {
        return Driver.class.getName();
    }

    @Override
    protected SQLType getSqlTypeByJdbcType(String typeName) {
        return null;
    }

}
