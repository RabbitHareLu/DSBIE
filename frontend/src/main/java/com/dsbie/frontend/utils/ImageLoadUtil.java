package com.dsbie.frontend.utils;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import lombok.Data;

import javax.swing.*;

/**
 * @author lsl
 * @version 1.0
 * @date 2024年03月27日 15:39
 */
@Data
public class ImageLoadUtil {

    private static final ImageLoadUtil INSTANCE = new ImageLoadUtil();
    private ImageIcon logoIcon = null;

    private ImageLoadUtil() {
        logoIcon = buildIcon("images/logo.svg");
    }

    public static ImageLoadUtil getInstance() {
        return INSTANCE;
    }

    public FlatSVGIcon buildIcon(String name) {
        return new FlatSVGIcon(name);
    }
}
