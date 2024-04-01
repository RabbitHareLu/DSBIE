package com.dsbie.frontend.utils;

import com.dsbie.frontend.Main;
import com.dsbie.frontend.threadpool.FrontendThreadPool;
import com.dsbie.rearend.exception.KToolException;
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
                        Throwable rootCause = getRootCause(ex);
                        if (!(rootCause instanceof KToolException)) {
                            DialogUtil.showErrorDialog(Main.dsbieJFrame, "系统异常");
                        } else {
                            // 弹框提示message
                        }
                        log.error(rootCause.getMessage(), rootCause);
                    }
                });
    }

    public static Throwable getRootCause(Throwable throwable) {
        // 如果当前异常没有原因，说明已经是根异常，直接返回
        if (throwable.getCause() == null) {
            return throwable;
        } else {
            // 递归调用获取原因中的根异常
            return getRootCause(throwable.getCause());
        }
    }
}
