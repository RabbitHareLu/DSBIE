package com.dsbie.frontend.threadpool;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author lsl
 * @version 1.0
 * @date 2024年03月29日 13:51
 */
@Slf4j
@Data
public class FrontendThreadPool {

    private static final FrontendThreadPool INSTANCE = new FrontendThreadPool();

    private ExecutorService executorService;

    private FrontendThreadPool() {
        log.info("初始化前端虚拟线程池");
        executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    public static FrontendThreadPool getInstance() {
        return INSTANCE;
    }
}
