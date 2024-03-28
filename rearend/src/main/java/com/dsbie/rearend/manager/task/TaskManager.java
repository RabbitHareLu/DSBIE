package com.dsbie.rearend.manager.task;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 任务执行器
 *
 * @author WCG
 */
@Slf4j
public class TaskManager {

    private final ExecutorService executorService;

    private final List<ScheduledExecutorService> scheduledExecutorServices;

    public TaskManager() {
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
        this.scheduledExecutorServices = new ArrayList<>();
    }

    public <V> Future<V> submitTask(Callable<V> callable) {
        return executorService.submit(callable);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        ScheduledExecutorService service = createScheduledExecutorService();
        scheduledExecutorServices.add(service);
        return service.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        ScheduledExecutorService service = createScheduledExecutorService();
        scheduledExecutorServices.add(service);
        return service.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }

    private ScheduledExecutorService createScheduledExecutorService() {
        ThreadFactory threadFactory = Thread.ofVirtual().factory();
        return Executors.newSingleThreadScheduledExecutor(threadFactory);
    }

    public void shutdown() {
        executorService.shutdown();
        scheduledExecutorServices.forEach(ScheduledExecutorService::shutdown);
    }

}
