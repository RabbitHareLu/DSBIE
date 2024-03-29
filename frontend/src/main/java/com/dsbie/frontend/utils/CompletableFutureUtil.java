package com.dsbie.frontend.utils;

import com.dsbie.frontend.Main;
import com.dsbie.frontend.threadpool.FrontendThreadPool;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @author lsl
 * @version 1.0
 * @date 2024年03月30日 0:04
 */
@Slf4j
public class CompletableFutureUtil {
    public static void submit(Runnable runnable) {
        CompletableFuture.runAsync(runnable, FrontendThreadPool.getInstance().getExecutorService())
                .whenComplete((result, ex) -> {
                    if (Objects.nonNull(ex)) {
                        DialogUtil.showErrorDialog(Main.dsbieJFrame, "系统异常");
                        log.error(ex.getMessage(), ex);
                    }
                });
    }
}
