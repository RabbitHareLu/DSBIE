package com.dsbie.frontend.utils;

import com.dsbie.rearend.KToolsContext;
import com.formdev.flatlaf.FlatLaf;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author lsl
 * @version 1.0
 * @date 2024年03月27日 15:26
 */
@Slf4j
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

    public static void initUIFont() {
        try {
            CompletableFuture.runAsync(() -> {
                Properties properties = KToolsContext.getInstance().getProperties();
                String fontName = String.valueOf(properties.get("font.name"));
                int fontSize = Integer.parseInt(String.valueOf(properties.get("font.size")));
                String fontStyle = String.valueOf(properties.get("font.style"));
                log.info("初始化界面字体: {} {} {}", fontName, fontSize, fontStyle);
                UIManager.put("defaultFont", new Font(fontName, FontUtil.getFontStyle(fontStyle), fontSize));
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
