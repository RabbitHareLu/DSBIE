package com.dsbie.rearend.job;

import com.dsbie.rearend.job.model.JobModel;
import com.dsbie.rearend.job.model.JobResult;
import com.dsbie.rearend.job.runnable.JobRunner;
import com.dsbie.rearend.manager.task.TaskManager;

import javax.swing.*;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * 任务管理器
 *
 * @author WCG
 */
public class JobContext {

    private final TaskManager taskManager;

    private final Map<String, JTextArea> J_TEXT_AREA_CACHE = new ConcurrentHashMap<>();

    private final Map<String, JobRunner> RUNNING_JOB = new ConcurrentHashMap<>();

    public JobContext(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public Future<JobResult> submit(JobModel jobModel, JTextArea jTextArea) {
        JobRunner jobRunner = new JobRunner(jobModel, this);
        RUNNING_JOB.put(jobModel.getJobId(), jobRunner);
        J_TEXT_AREA_CACHE.put(jobModel.getJobId(), jTextArea);
        return this.taskManager.submitTask(jobRunner);
    }

    public void log(String jobId, String msg) {
        Optional.ofNullable(J_TEXT_AREA_CACHE.get(jobId))
                .ifPresent(jTextArea -> jTextArea.append(msg + "\n"));
    }

    public void stopJob(String jobId) {
        Optional.ofNullable(RUNNING_JOB.get(jobId)).ifPresent(JobRunner::close);
    }

    public void jobCompleted(String jobId) {
        RUNNING_JOB.remove(jobId);
        J_TEXT_AREA_CACHE.remove(jobId);
    }

}
