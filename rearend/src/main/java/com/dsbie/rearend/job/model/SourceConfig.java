package com.dsbie.rearend.job.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Properties;

/**
 * 读取器配置
 *
 * @author WCG
 */
@Getter
@Setter
public class SourceConfig {

    /**
     * 数据源id
     */
    private String sourceId;

    /**
     * 数据源类型
     */
    private String sourceType;

    /**
     * 数据源配置信息
     */
    private Properties sourceProperties;

    /**
     * 文件类型填路径、JDBC类型填sql
     */
    private String configValue;

    /**
     * 文件类型（文件类型数据源使用）
     */
    private String fileType;

    /**
     * 分割符（文件类型数据源使用）
     */
    private String separator;
}
