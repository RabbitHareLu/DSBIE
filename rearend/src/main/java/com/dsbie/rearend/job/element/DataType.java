package com.dsbie.rearend.job.element;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * 数据类型
 *
 * @author WCG
 */
public enum DataType implements Serializable {
    /**
     * 未知类型
     */
    UNKNOWN_DATA,
    NULL,
    BYTE,
    SHORT,
    INT,
    LONG,
    STRING,
    BOOLEAN,
    FLOAT,
    DOUBLE,
    BINARY,
    DECIMAL,
    DATE,
    TIMESTAMP;

    public Object convertData(Object o) {
        if (o == null) {
            return null;
        }
        return switch (this) {
            case NULL -> null;
            case BYTE -> Byte.parseByte(String.valueOf(o));
            case SHORT -> Short.parseShort(String.valueOf(o));
            case INT -> Integer.parseInt(String.valueOf(o).replace(",", ""));
            case LONG -> Long.parseLong(String.valueOf(o).replace(",", ""));
            case DOUBLE -> Double.parseDouble(String.valueOf(o).replace(",", ""));
            case DECIMAL -> new BigDecimal(String.valueOf(o).replace(",", ""));
            case TIMESTAMP -> Timestamp.valueOf(String.valueOf(o));
            case STRING -> String.valueOf(o);
            case DATE -> Date.valueOf(String.valueOf(o));
            case BOOLEAN -> Boolean.parseBoolean(String.valueOf(o));
            case FLOAT -> Float.parseFloat(String.valueOf(o));
            case BINARY -> String.valueOf(o).getBytes();
            case UNKNOWN_DATA -> throw new RuntimeException("未知数据类型！");
        };
    }

}