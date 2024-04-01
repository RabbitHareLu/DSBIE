package com.dsbie.rearend.service;

import com.dsbie.rearend.api.DataSourceApi;
import com.dsbie.rearend.manager.datasource.DataSourceFactory;
import com.dsbie.rearend.manager.datasource.DataSourceType;
import com.dsbie.rearend.manager.datasource.KDataSourceHandler;
import com.dsbie.rearend.manager.datasource.jdbc.model.TableMetadata;
import com.dsbie.rearend.manager.datasource.model.KDataSourceMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author WCG
 */
public class DataSourceService extends BaseService implements DataSourceApi {

    @Override
    public Map<String, KDataSourceMetadata> getAllMetadata(String name) {
        return DataSourceType.valueOf(name).getAllMetadata();
    }

    @Override
    public KDataSourceMetadata getMetadata(String name) {
        return DataSourceFactory.getMetadata(name);
    }

    @Override
    public void testDataSource(String type, Map<String, String> properties) {
        // 构建Properties
        Properties datasourceProperties = new Properties();
        datasourceProperties.putAll(properties);
        // 获取数据源处理器
        KDataSourceHandler dataSourceHandler = DataSourceFactory.createDataSourceHandler(type, datasourceProperties);
        // 测试数据源连接
        dataSourceHandler.testConn();
    }

    @Override
    public void conn(String id, String type, Map<String, String> properties) {
        // 构建Properties
        Properties datasourceProperties = new Properties();
        datasourceProperties.putAll(properties);
        // 获取数据源处理器
        KDataSourceHandler dataSourceHandler = DataSourceFactory.getDataSourceHandler(id, type, datasourceProperties);
        // 连接数据源
        dataSourceHandler.conn();
    }

    @Override
    public void disConn(String id) {
        DataSourceFactory.removeDataSourceHandler(id);
    }

    @Override
    public List<String> selectAllSchema(String id) {
        // 获取数据源处理器
        KDataSourceHandler dataSourceHandler = DataSourceFactory.getDataSourceHandler(id);
        if (dataSourceHandler != null) {
            return dataSourceHandler.selectAllSchema();
        }
        return new ArrayList<>();
    }

    @Override
    public List<String> selectAllTable(String id, String schema) {
        // 获取数据源处理器
        KDataSourceHandler dataSourceHandler = DataSourceFactory.getDataSourceHandler(id);
        if (dataSourceHandler != null) {
            return dataSourceHandler.selectAllTable(schema);
        }
        return new ArrayList<>();
    }

    @Override
    public TableMetadata selectTableMetadata(String id, String schema, String tableName) {
        // 获取数据源处理器
        KDataSourceHandler dataSourceHandler = DataSourceFactory.getDataSourceHandler(id);
        if (dataSourceHandler != null) {
            return dataSourceHandler.selectTableMetadata(schema, tableName);
        }
        return null;
    }

}
