package com.dsbie.rearend.manager.datasource.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 数据源元数据
 *
 * @author WCG
 */
@Data
public class KDataSourceMetadata implements Serializable {

    /**
     * 名称
     */
    private String name;

    /**
     * 未连接logo
     */
    private String logo;

    /**
     * 已连接logo
     */
    private String connLogo;

    /**
     * 可配置项
     */
    private List<KDataSourceConfig> config;

}
