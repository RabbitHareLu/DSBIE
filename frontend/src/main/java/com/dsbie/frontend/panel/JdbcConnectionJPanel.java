package com.dsbie.frontend.panel;

import com.dsbie.frontend.component.FrameJPopupMenu;
import com.dsbie.frontend.component.LeftTree;
import com.dsbie.frontend.frame.DsbieJFrame;
import com.dsbie.frontend.threadpool.FrontendThreadPool;
import com.dsbie.rearend.mybatis.entity.TreeEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static com.formdev.flatlaf.FlatClientProperties.*;

/**
 * @author lsl
 * @version 1.0
 * @date 2024年03月29日 16:55
 */

@Getter
@Setter
@Slf4j
public class JdbcConnectionJPanel extends JPanel {

    public JdbcConnectionJPanel(String value) {
        JLabel jLabel = new JLabel(value);
        add(jLabel);
    }


    @Slf4j
    public static class CreateJdbcConnectionJPanelAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            CompletableFuture.runAsync(() -> {
                JMenuItem source = (JMenuItem) e.getSource();
                initTabbedPane();
                SwingUtilities.invokeLater(() -> DsbieJFrame.rootJSplitPane.setRightComponent(DsbieJFrame.closableTabsTabbedPane));

                TreeEntity currentTreeEntity = LeftTree.getInstance().getCurrentTreeEntity();

                SwingUtilities.invokeLater(() -> {
                    String string = UUID.randomUUID().toString();
                    Component add = DsbieJFrame.closableTabsTabbedPane.add(source.getText() + string, new JdbcConnectionJPanel(string));
                    DsbieJFrame.closableTabsTabbedPane.setSelectedComponent(add);
                });
            }, FrontendThreadPool.getInstance().getExecutorService());
        }

        private static void initTabbedPane() {
            if (Objects.isNull(DsbieJFrame.closableTabsTabbedPane)) {
                JTabbedPane tabbedPane = new JTabbedPane();
                tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSABLE, true);
                tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, "Close");
                tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
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

}
