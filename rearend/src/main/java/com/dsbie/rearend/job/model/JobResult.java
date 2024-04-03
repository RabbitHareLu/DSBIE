package com.dsbie.rearend.job.model;

import lombok.Data;

/**
 * 任务执行结果
 *
 * @author WCG
 */
@Data
public class JobResult {

    /**
     * 任务状态
     */
    private JobResultState state;

    /**
     * 任务消息
     */
    private String message;

    /**
     * 任务执行时间
     */
    private Long taskExecTime;

}
