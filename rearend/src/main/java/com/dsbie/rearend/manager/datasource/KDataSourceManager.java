package com.dsbie.rearend.manager.datasource;

import com.dsbie.rearend.exception.KToolException;
import com.dsbie.rearend.manager.datasource.model.KDataSourceMetadata;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 数据源管理器
 *
 * @author WCG
 */
public class KDataSourceManager {

    /**
     * 数据源工厂
     */
    private final ServiceLoader<KDataSourceFactory> factories;

    private final Map<String, KDataSourceHandler> handlerMap = new ConcurrentHashMap<>();

    public KDataSourceManager() {
        this.factories = ServiceLoader.load(KDataSourceFactory.class);
    }

    /**
     * 查询加载
     */
    public void reload() {
        this.factories.reload();
    }

    /**
     * 查询支持的数据源类型
     */
    public List<KDataSourceMetadata> supportDataSource() {
        this.reload();
        return this.factories.stream().map(provider -> provider.get().getMetadata()).collect(Collectors.toList());
    }

    /**
     * 获取数据源工厂
     *
     * @param name 数据源名称
     */
    public KDataSourceFactory getFactory(String name) throws KToolException {
        for (KDataSourceFactory factory : this.factories) {
            if (Objects.equals(factory.getMetadata().getName(), name)) {
                return factory;
            }
        }
        throw new KToolException("不支持的元数据类型");
    }

    public KDataSourceHandler getHandler(String key) throws KToolException {
        return Optional.ofNullable(this.handlerMap.get(key)).orElseThrow(() -> new KToolException("未找到数据源！"));
    }

    public void addHandler(String key, KDataSourceHandler handler) {
        Optional.ofNullable(handler).ifPresent(h -> this.handlerMap.put(key, h));
    }

    public void removeHandler(String key) {
        this.handlerMap.remove(key);
    }

    public boolean existHandler(String key) {
        return this.handlerMap.containsKey(key);
    }

}
