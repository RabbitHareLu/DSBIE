package com.dsbie.frontend.utils;

import javax.swing.*;
import java.awt.*;

/**
 * @author lsl
 * @version 1.0
 * @date 2024年03月28日 20:11
 */
public class DialogUtil {

    public static void showErrorDialog(Component parentComponent, Object message) {
        JOptionPane.showMessageDialog(
                parentComponent,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
