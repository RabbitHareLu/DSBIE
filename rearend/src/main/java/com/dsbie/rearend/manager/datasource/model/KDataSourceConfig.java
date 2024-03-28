package com.dsbie.rearend.manager.datasource.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 配置信息
 *
 * @author WCG
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KDataSourceConfig implements Serializable {

    /**
     * 配置名称
     */
    private String name;

    /**
     * 配置key
     */
    private String key;

    /**
     * 是否必须配置
     */
    private boolean must;

    /**
     * 默认值
     */
    private String defaultValue;

}
