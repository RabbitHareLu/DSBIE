package com.dsbie.rearend.manager.datasource.type.jdbc.model;

import lombok.Data;

import java.sql.SQLType;

/**
 * 列信息
 *
 * @author WCG
 */
@Data
public class TableColumn {

    /**
     * 列名
     */
    private String name;

    /**
     * 列类型
     */
    private SQLType dataType;

    /**
     * 列长度
     */
    private Integer length;

    /**
     * 列精度
     */
    private Integer precision;

    /**
     * 是否主键
     */
    private boolean primaryKey;

    /**
     * 是否可为空
     */
    private boolean nullable;

}