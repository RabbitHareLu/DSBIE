package com.dsbie.rearend.service;

import com.dsbie.rearend.api.JobApi;
import com.dsbie.rearend.job.model.JobModel;
import com.dsbie.rearend.job.model.JobResult;

import javax.swing.*;
import java.util.concurrent.Future;

/**
 *
 *
 * @author WCG
 */
public class JobService extends BaseService implements JobApi {

    @Override
    public Future<JobResult> submit(JobModel jobModel, JTextArea jTextArea) {
        return kToolsContext.getJobContext().submit(jobModel, jTextArea);
    }

}
