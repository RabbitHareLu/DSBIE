package com.dsbie.rearend.job.runnable;

import com.dsbie.rearend.job.JobContext;
import com.dsbie.rearend.job.model.JobModel;
import com.dsbie.rearend.job.model.SinkConfig;
import com.dsbie.rearend.job.model.SourceConfig;
import com.dsbie.rearend.manager.datasource.DataSourceFactory;
import com.dsbie.rearend.manager.datasource.KDataSourceHandler;

/**
 * 任务执行器
 *
 * @author WCG
 */
public class JobRunner extends AbstractJobRunner {

    public JobRunner(JobModel jobModel, JobContext jobContext) {
        super(jobModel, jobContext);
    }

    @Override
    protected void doTask() {
        // 获取源节点处理器
        SourceConfig sourceConfig = jobModel.getSourceConfig();
        KDataSourceHandler sourceHandler = DataSourceFactory.getDataSourceHandler(sourceConfig.getSourceId(), sourceConfig.getSourceType(), sourceConfig.getSourceProperties());
        sourceHandler.conn();
        // 获取目标节点处理器
        SinkConfig sinkConfig = jobModel.getSinkConfig();
        KDataSourceHandler sinkHandler = DataSourceFactory.getDataSourceHandler(sinkConfig.getSinkId(), sinkConfig.getSinkType(), sinkConfig.getSinkProperties());
        sinkHandler.conn();
        // 开始执行数据任务
        sourceHandler.selectData(jobModel, baseRowStream -> sinkHandler.syncData(jobModel, baseRowStream));
    }

    @Override
    public void close() {

    }

}
