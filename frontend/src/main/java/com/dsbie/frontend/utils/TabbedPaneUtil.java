package com.dsbie.frontend.utils;

import com.dsbie.frontend.component.FrameJPopupMenu;
import com.dsbie.frontend.frame.DsbieJFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import java.util.function.BiConsumer;

import static com.formdev.flatlaf.FlatClientProperties.*;

/**
 * @author lsl
 * @version 1.0
 * @date 2024年04月02日 16:22
 */
public class TabbedPaneUtil {

    public static void initTabbedPane() {
        if (Objects.isNull(DsbieJFrame.closableTabsTabbedPane)) {
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSABLE, true);
            tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, "Close");
            tabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
            tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_CALLBACK,
                    (BiConsumer<JTabbedPane, Integer>) (tabPane, tabIndex) -> {
                        int tabCount = tabbedPane.getTabCount();
                        if (tabCount <= 1) {
                            SwingUtilities.invokeLater(() -> DsbieJFrame.rootJSplitPane.setRightComponent(DsbieJFrame.logoLabel));
                        }
                        SwingUtilities.invokeLater(() -> tabbedPane.remove(tabIndex));
                    });
            tabbedPane.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        int x = e.getX();
                        int y = e.getY();
                        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                            Rectangle boundsAt = tabbedPane.getBoundsAt(i);
                            if (y < (boundsAt.getY() + boundsAt.height) && x < (boundsAt.getX() + boundsAt.getWidth())) {
                                FrameJPopupMenu.getInstance().getTabbedPanePopupMenu().show(tabbedPane, x, y);
                            }
                        }
                    }
                }
            });

            DsbieJFrame.closableTabsTabbedPane = tabbedPane;
        }
    }
}
