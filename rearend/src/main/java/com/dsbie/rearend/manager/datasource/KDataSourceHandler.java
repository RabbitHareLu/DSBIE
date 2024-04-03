package com.dsbie.rearend.manager.datasource;

import com.dsbie.rearend.job.element.BaseRow;
import com.dsbie.rearend.job.model.JobModel;
import com.dsbie.rearend.manager.datasource.type.jdbc.model.TableMetadata;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

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
    
    /**
     * 查询数据
     */
    void selectData(JobModel jobModel, Consumer<Stream<BaseRow>> consumer);

    /**
     * 同步数据
     */
    void syncData(JobModel jobModel, Stream<BaseRow> baseRow);
}
