package com.dsbie.rearend.job.runnable;

import com.dsbie.rearend.KToolsContext;
import com.dsbie.rearend.job.JobContext;
import com.dsbie.rearend.job.model.JobModel;
import com.dsbie.rearend.job.model.JobResult;
import com.dsbie.rearend.job.model.JobResultState;
import lombok.AllArgsConstructor;

import java.util.concurrent.Callable;

/**
 * @author WCG
 */
@AllArgsConstructor
public abstract class AbstractJobRunner implements Callable<JobResult> {

    protected final JobModel jobModel;

    protected final JobContext jobContext;

    @Override
    public JobResult call() {
        long startTime = System.currentTimeMillis();

        // 任务执行结果
        JobResult taskResult = new JobResult();

        try {
            // 执行任务
            doTask();
            // 封装执行结果
            taskResult.setMessage("任务执行成功！");
            taskResult.setState(JobResultState.SUCCESS);
        } catch (Exception e) {
            // 处理异常
            String errorMsg = handleException(e);
            taskResult.setState(JobResultState.FAIL);
            taskResult.setMessage(errorMsg);
        } finally {
            // 通知任务执行结束
            this.jobContext.jobCompleted(jobModel.getJobId());
            KToolsContext.getInstance().getJobContext().log(jobModel.getJobId(), taskResult.getMessage());
        }

        taskResult.setTaskExecTime(System.currentTimeMillis() - startTime);
        return taskResult;
    }

    private String handleException(Exception e) {
        // 构建错误信息
        StringBuilder logContent = new StringBuilder("数据任务执行失败！异常信息:" + e.getMessage());
        Throwable cause = e.getCause();
        while (cause != null) {
            logContent.append(" cause: ").append(cause.getMessage());
            Throwable nextCause = cause.getCause();
            // 解决异常循环问题
            if (nextCause == cause) {
                break;
            }
            cause = nextCause;
        }

        // 返回错误信息
        return logContent.toString();
    }

    protected abstract void doTask();

    public abstract void close();

}
