package com.dsbie.rearend.api;

import com.dsbie.rearend.job.model.JobModel;
import com.dsbie.rearend.job.model.JobResult;

import javax.swing.*;
import java.util.concurrent.Future;

/**
 *
 *
 * @author WCG
 */
public interface JobApi {

    Future<JobResult> submit(JobModel jobModel, JTextArea jTextArea);

}
