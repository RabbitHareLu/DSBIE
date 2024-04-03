package com.dsbie.rearend.job.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Properties;

/**
 * 写入器配置
 *
 * @author WCG
 */
@Getter
@Setter
public class SinkConfig {

    /**
     * 数据源id
     */
    private String sinkId;

    /**
     * 数据源类型
     */
    private String sinkType;

    /**
     * 数据源配置
     */
    private Properties sinkProperties;

    /**
     * schema
     */
    private String schema;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 批次大小
     */
    private Integer batchSize;

    /**
     * 文件夹路径（文件类型数据源使用）
     */
    private String dirPath;

    /**
     * 文件类型（文件类型数据源使用）
     */
    private String fileType;

    /**
     * 分割符（文件类型数据源使用）
     */
    private String separator;
}
