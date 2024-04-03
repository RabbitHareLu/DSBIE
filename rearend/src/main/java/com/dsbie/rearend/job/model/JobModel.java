package com.dsbie.rearend.job.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 任务模型
 *
 * @author WCG
 */
@Getter
@Setter
public class JobModel {

    /**
     * 任务id
     */
    private String jobId;

    /**
     * 全局任务配置
     */
    private SettingConfig settingConfig;

    /**
     * 读取器配置
     */
    private SourceConfig sourceConfig;

    /**
     * 写入器配置
     */
    private SinkConfig sinkConfig;

}
