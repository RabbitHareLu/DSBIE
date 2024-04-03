package com.dsbie.rearend.service;

import com.dsbie.rearend.api.DataSourceApi;
import com.dsbie.rearend.manager.datasource.DataSourceFactory;
import com.dsbie.rearend.manager.datasource.DataSourceType;
import com.dsbie.rearend.manager.datasource.KDataSourceHandler;
import com.dsbie.rearend.manager.datasource.type.jdbc.model.TableMetadata;
import com.dsbie.rearend.manager.datasource.model.KDataSourceMetadata;
import com.dsbie.rearend.mybatis.entity.TreeEntity;
import com.mybatisflex.core.query.QueryChain;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Override
    public Map<String, TreeEntity> selectAllDataSource() {
        List<TreeEntity> treeEntities = QueryChain.of(TreeEntity.class)
                .eq(TreeEntity::getNodeType, "CONNECTION")
                .list();

        Set<String> idList = treeEntities.stream()
                .flatMap((Function<TreeEntity, Stream<String>>) treeEntity -> Arrays.stream(treeEntity.getNodePath().split("/")))
                .collect(Collectors.toSet());

        Map<Integer, String> pathNodeMap = QueryChain.of(TreeEntity.class)
                .select(TreeEntity::getId, TreeEntity::getNodeName)
                .in(TreeEntity::getId, idList)
                .list()
                .stream()
                .collect(Collectors.toMap(TreeEntity::getId, TreeEntity::getNodeName));

        return treeEntities.stream().collect(Collectors.toMap(treeEntity -> {
            String[] pathNode = treeEntity.getNodePath().split("/");
            StringJoiner joiner = new StringJoiner("/");
            for (int i = 1; i < pathNode.length; i++) {
                joiner.add(pathNodeMap.get(Integer.valueOf(pathNode[i])));
            }
            return joiner.toString().concat("/").concat(treeEntity.getNodeName());
        }, Function.identity()));


    }

}
