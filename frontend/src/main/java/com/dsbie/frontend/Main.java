package com.dsbie.frontend;

import com.dsbie.frontend.frame.DsbieJFrame;
import com.dsbie.frontend.utils.FontUtil;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static DsbieJFrame dsbieJFrame = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatIntelliJLaf.setup();
            UIManager.put("Button.arc", 50);  // 设置按钮的弧度
            UIManager.put("Component.focusWidth", 0);  // 设置组件的焦点边框宽度
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("OptionPane.showIcon", true);
            FontUtil.initUIFont();
            dsbieJFrame = new DsbieJFrame();
        });
    }
}