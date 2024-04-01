package com.dsbie.rearend.manager.datasource.model;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 数据源元数据
 *
 * @author WCG
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KDataSourceMetadata implements Serializable {

    /**
     * 类别
     */
    private String type;

    /**
     * 名称
     */
    private String name;

    /**
     * 可配置项
     */
    private List<KDataSourceConfig> config;

    public static KDataSourceMetadata of(String type, String name, List<KDataSourceConfig> config) {
        return new KDataSourceMetadata(type, name, config);
    }

}
