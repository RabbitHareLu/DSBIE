package com.dsbie.rearend.manager.datasource;

import com.dsbie.rearend.exception.KToolException;
import com.dsbie.rearend.manager.datasource.model.KDataSourceMetadata;

import java.util.Properties;

/**
 * 数据源工厂
 *
 * @author WCG
 */
public interface KDataSourceFactory {

    /**
     * 数据源元数据
     */
    KDataSourceMetadata getMetadata();

    /**
     * 创建数据源处理器
     *
     * @param properties 配置消息
     * @return 数据源处理器
     */
    KDataSourceHandler createDataSourceHandler(Properties properties) throws KToolException;

}
