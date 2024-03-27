package com.dsbie.frontend;

import com.dsbie.frontend.frame.DsbieJFrame;
import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;

public class Main {
    public static DsbieJFrame dsbieJFrame = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatIntelliJLaf.setup();
            UIManager.put("Button.arc", 50);  // 设置按钮的弧度
            UIManager.put("Component.focusWidth", 1);  // 设置组件的焦点边框宽度
            UIManager.put("TextComponent.arc", 10);
//            UIManager.put("defaultFont", new Font("新宋体", Font.BOLD, 20));
            dsbieJFrame = new DsbieJFrame();
        });
    }
}