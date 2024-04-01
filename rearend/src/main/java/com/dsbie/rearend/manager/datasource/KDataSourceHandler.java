package com.dsbie.rearend.manager.datasource;

import com.dsbie.rearend.manager.datasource.jdbc.model.TableMetadata;

import java.util.List;

/**
 * 数据源接口
 *
 * @author WCG
 */
public interface KDataSourceHandler {

    /**
     * 关闭
     */
    void close();

    /**
     * 测试连接
     */
    void testConn();

    /**
     * 连接数据源
     */
    void conn();

    /**
     * 断开数据源
     */
    void disConn();

    /**
     * 查询所有schema
     */
    List<String> selectAllSchema();

    /**
     * 查询所有表名
     *
     * @param schema schema
     */
    List<String> selectAllTable(String schema);

    /**
     * 查询表元数据
     */
    TableMetadata selectTableMetadata(String schema, String tableName);

}
