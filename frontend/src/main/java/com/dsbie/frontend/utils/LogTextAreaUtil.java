package com.dsbie.frontend.utils;

import com.dsbie.rearend.common.utils.StringUtil;
import lombok.NonNull;

import javax.swing.*;

/**
 * @author lsl
 * @version 1.0
 * @date 2024年04月03日 10:01
 */
public class LogTextAreaUtil {

    /**
     * 向日志框里默认追加一行日志信息, 内部已在消息结尾添加换行符
     *
     * @param logTextArea
     * @param message
     * @return
     * @author lsl
     * @date 2024/4/3 10:04
     */
    public static void appendLog(@NonNull JTextArea logTextArea, String message) {
        SwingUtilities.invokeLater(() -> {
            if (StringUtil.isNotBlank(message)) {
                logTextArea.append(message + "\n");
            }
        });
    }

}
