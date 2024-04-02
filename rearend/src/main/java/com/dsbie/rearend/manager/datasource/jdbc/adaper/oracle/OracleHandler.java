package com.dsbie.rearend.manager.datasource.jdbc.adaper.oracle;

import com.dsbie.rearend.exception.KToolException;
import com.dsbie.rearend.manager.datasource.jdbc.AbstractJdbcHandler;
import oracle.jdbc.OracleType;
import oracle.jdbc.driver.OracleDriver;

import java.sql.SQLType;
import java.util.Properties;

/**
 * oracle处理器
 *
 * @author WCG
 */
public class OracleHandler extends AbstractJdbcHandler {

    public OracleHandler(Properties properties) throws KToolException {
        super(properties);
    }

    @Override
    protected String getDriverClass() {
        return OracleDriver.class.getName();
    }

    @Override
    protected SQLType getSqlTypeByJdbcType(String typeName) {
        return OracleType.valueOf(typeName);
    }

}
