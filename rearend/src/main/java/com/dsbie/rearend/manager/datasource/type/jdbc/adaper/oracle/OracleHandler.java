package com.dsbie.rearend.manager.datasource.type.jdbc.adaper.oracle;

import com.dsbie.rearend.exception.KToolException;
import com.dsbie.rearend.job.element.DataType;
import com.dsbie.rearend.manager.datasource.type.jdbc.AbstractJdbcHandler;
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

    @Override
    protected DataType getDataTypeByJdbcType(String typeName) {
        OracleType oracleType = OracleType.valueOf(typeName);
        return switch (oracleType) {
            case FLOAT -> DataType.FLOAT;
            case TIMESTAMP -> DataType.TIMESTAMP;
            case DATE -> DataType.DATE;
            case CHAR, BLOB, CLOB, NCHAR, NCLOB, NVARCHAR, VARCHAR2 -> DataType.STRING;
            case LONG -> DataType.LONG;
            case NUMBER -> DataType.DECIMAL;
            default -> throw new RuntimeException("暂不支持的数据类型:" + oracleType.getName());
        };
    }

}
