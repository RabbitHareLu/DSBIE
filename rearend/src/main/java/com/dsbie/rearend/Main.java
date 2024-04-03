package com.dsbie.rearend;

import com.dsbie.rearend.job.JobContext;
import com.dsbie.rearend.job.model.JobModel;
import com.dsbie.rearend.job.model.JobResult;
import com.dsbie.rearend.job.model.SinkConfig;
import com.dsbie.rearend.job.model.SourceConfig;

import javax.swing.*;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Main {

    public static void main(String[] args) {
        KToolsContext instance = KToolsContext.getInstance();
        JobModel jobModel = buildJobModel();

        JobContext jobContext = instance.getJobContext();
        Future<JobResult> future = jobContext.submit(jobModel, new JTextArea());
        try {
            JobResult jobResult = future.get();
            System.out.println(jobResult.getMessage());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            instance.showdown();
        }
    }

    private static JobModel buildJobModel() {
        JobModel jobModel = new JobModel();
        jobModel.setJobId("test");

        // 构建来源配置
        Properties sourceProperties = new Properties();
        sourceProperties.put("jdbcUrl", "jdbc:mysql://192.168.0.33:3306/kdm_sys");
        sourceProperties.put("username", "kdm_sys");
        sourceProperties.put("password", "Pqv]!/T)-1p");

        SourceConfig sourceConfig = new SourceConfig();
        sourceConfig.setSourceId("test1");
        sourceConfig.setSourceType("MYSQL");
        sourceConfig.setSourceProperties(sourceProperties);
        sourceConfig.setConfigValue("select * from t_s_user");
        jobModel.setSourceConfig(sourceConfig);

        // 构建目标配置信息
        SinkConfig sinkConfig = new SinkConfig();
        sinkConfig.setSinkId("test2");
        sinkConfig.setSinkType("LOCAL_FILE");
        sinkConfig.setSinkProperties(new Properties());
        sinkConfig.setFileType("CSV");
        sinkConfig.setSeparator(",");
        sinkConfig.setDirPath("E:/log");
        jobModel.setSinkConfig(sinkConfig);

        return jobModel;
    }
}