package com.dsbie.rearend.manager.datasource;

import com.dsbie.rearend.exception.KToolException;
import com.dsbie.rearend.manager.datasource.jdbc.model.TableMetadata;

import java.util.List;

/**
 * 数据源接口
 *
 * @author WCG
 */
public interface KDataSourceHandler {

    /**
     * 测试连接
     */
    void testConn() throws KToolException;

    /**
     * 连接数据源
     */
    void conn();

    /**
     * 断开连接数据源
     */
    void disConn();

    /**
     * 查询所有schema
     */
    List<String> selectAllSchema() throws KToolException;

    /**
     * 查询所有表名
     *
     * @param schema schema
     */
    List<String> selectAllTable(String schema) throws KToolException;

    /**
     * 查询表元数据
     */
    TableMetadata selectTableMetadata(String schema, String tableName) throws KToolException;

}
