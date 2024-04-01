package com.dsbie.rearend.manager.datasource;

import com.dsbie.rearend.manager.datasource.model.KDataSourceMetadata;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * 数据源工厂
 *
 * @author WCG
 */
public class DataSourceFactory {

    private static final Map<String, Type> CACHE_MAP = new HashMap<>();

    private static final Map<String, KDataSourceHandler> HANDLER_CACHE = new HashMap<>();

    static {
        for (DataSourceType dataSourceType : DataSourceType.values()) {
            for (Type type : dataSourceType.getSupportedTypes()) {
                CACHE_MAP.put(type.name(), type);
            }
        }
    }

    /**
     * 查询指定类型的数据源元数据
     */
    public static KDataSourceMetadata getMetadata(String name) {
        return CACHE_MAP.get(name).getMetadata();
    }

    /**
     * 创建数据源处理器
     *
     * @param properties 配置消息
     * @return 数据源处理器
     */
    public static KDataSourceHandler createDataSourceHandler(String type, Properties properties) {
        return CACHE_MAP.get(type).createDataSourceHandler(properties);
    }

    /**
     * 获取数据源处理器
     *
     * @param id         节点id
     * @param type       节点类型
     * @param properties 节点配置
     * @return 处理器
     */
    public static KDataSourceHandler getDataSourceHandler(String id, String type, Properties properties) {
        if (!HANDLER_CACHE.containsKey(id)) {
            synchronized (DataSourceFactory.class) {
                if (!HANDLER_CACHE.containsKey(id)) {
                    HANDLER_CACHE.put(id, createDataSourceHandler(type, properties));
                }
            }
        }
        return HANDLER_CACHE.get(id);
    }

    /**
     * 获取数据源处理器
     *
     * @param id         节点id
     * @return 处理器
     */
    public static KDataSourceHandler getDataSourceHandler(String id) {
        return HANDLER_CACHE.get(id);
    }

    /**
     * 获取数据源处理器
     *
     * @param id         节点id
     */
    public static void removeDataSourceHandler(String id) {
        Optional.ofNullable(HANDLER_CACHE.get(id)).ifPresent(kDataSourceHandler -> {
            kDataSourceHandler.close();
            HANDLER_CACHE.remove(id);
        });
    }

}
