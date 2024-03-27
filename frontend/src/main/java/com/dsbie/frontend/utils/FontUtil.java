package com.dsbie.frontend.utils;

import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Properties;

/**
 * @author lsl
 * @version 1.0
 * @date 2024年03月27日 15:26
 */
public class FontUtil {
    private FontUtil() {
    }

    public static String[] getAllFonts() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        return ge.getAvailableFontFamilyNames();
    }

    public static void updateUIFont(Font newFont) {
        UIManager.put("defaultFont", newFont);
        FlatLaf.updateUI();
    }

    public static int getFontStyle(String fontStyle) {
        if (Objects.equals(fontStyle, "粗体")) {
            return Font.BOLD;
        } else if (Objects.equals(fontStyle, "斜体")) {
            return Font.ITALIC;
        } else {
            return Font.PLAIN;
        }
    }

    public static void putUIFont() {
//        Properties properties = KToolsContext.getInstance().getProperties();
//        String fontName = String.valueOf(properties.get("font.name"));
//        int fontSize = Integer.parseInt(String.valueOf(properties.get("font.size")));
//        String fontStyle.svg = String.valueOf(properties.get("font.style"));
//        UIManager.put("defaultFont", new Font(fontName, FontUtil.getFontStyle(fontStyle.svg), fontSize));
    }


}
