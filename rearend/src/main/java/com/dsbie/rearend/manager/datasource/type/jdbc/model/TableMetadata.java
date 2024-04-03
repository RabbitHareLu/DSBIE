package com.dsbie.rearend.manager.datasource.type.jdbc.model;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author WCG
 */
@Data
public class TableMetadata {

    /**
     * schema
     */
    private String schema;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 列信息
     */
    private Map<String, TableColumn> columns = new HashMap<>();

    /**
     * 返回主键字段
     */
    public List<String> getPrimaryKey() {
        return this.getColumns().values()
                .stream()
                .filter(TableColumn::isPrimaryKey)
                .map(TableColumn::getName)
                .collect(Collectors.toList());
    }

}