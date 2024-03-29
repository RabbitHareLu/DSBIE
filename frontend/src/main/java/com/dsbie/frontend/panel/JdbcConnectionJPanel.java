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
    private JTextField nameInputField;
    private JTextField commentInputField;
    private JTabbedPane tabbedPane;
    private RegularJPanel regularJPanel;
    private AdvancedJPanel advancedJPanel;

    public JdbcConnectionJPanel() {

    }

    public JdbcConnectionJPanel(TreeEntity treeEntity) {
        setLayout(new BorderLayout());

        Box northBox = initNorthBox(treeEntity.getNodeName(), treeEntity.getNodeComment());
        Box centerBox = initCenterBox(treeEntity);
        Box southBox = initSouthBox();

        add(northBox, BorderLayout.NORTH);
        add(centerBox, BorderLayout.CENTER);
        add(southBox, BorderLayout.SOUTH);
    }

    private Box initNorthBox(String name, String comment) {
        Box verticalBox = Box.createVerticalBox();
        verticalBox.add(Box.createVerticalStrut(30));

        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalStrut(100));

        JLabel nameLabel = new JLabel("名称: ");
        nameLabel.setToolTipText("名称");
        box.add(nameLabel);

        box.add(Box.createHorizontalStrut(20));

        nameInputField = new JTextField();
        nameInputField.setText(name);
        box.add(nameInputField);

        box.add(Box.createHorizontalStrut(50));

        JLabel commentLabel = new JLabel("备注: ");
        commentLabel.setToolTipText("备注");
        box.add(commentLabel);

        box.add(Box.createHorizontalStrut(20));

        commentInputField = new JTextField(comment);
        commentInputField.setText(comment);
        box.add(commentInputField);

        box.add(Box.createHorizontalStrut(100));

        verticalBox.add(box);
        verticalBox.add(Box.createVerticalStrut(30));

        return verticalBox;
    }

    private Box initCenterBox(TreeEntity treeEntity) {
        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalStrut(100));
        regularJPanel = new RegularJPanel(treeEntity);
        advancedJPanel = new AdvancedJPanel(treeEntity);
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("常规", null, regularJPanel, "常规");
        tabbedPane.addTab("高级", null, advancedJPanel, "高级");
        box.add(tabbedPane);
        box.add(Box.createHorizontalStrut(100));
        return box;
    }

    private Box initSouthBox() {
        Box verticalBox = Box.createVerticalBox();
        verticalBox.add(Box.createVerticalStrut(30));

        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalStrut(100));

        JButton testButton = new JButton("测试连接");
        box.add(testButton);

        box.add(Box.createHorizontalGlue());

        JButton okButton = new JButton("确认");
        okButton.setBackground(new Color(53, 116, 240));
        box.add(okButton);
        box.add(Box.createHorizontalStrut(30));
        JButton cancelButton = new JButton("取消");
        box.add(cancelButton);

        box.add(Box.createHorizontalStrut(100));
        verticalBox.add(box);
        verticalBox.add(Box.createVerticalStrut(60));

        return verticalBox;
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

                JdbcConnectionJPanel jdbcConnectionJPanel = new JdbcConnectionJPanel(currentTreeEntity);
                SwingUtilities.invokeLater(() -> {
                    Component add = DsbieJFrame.closableTabsTabbedPane.add("新建" + source.getText() + "数据源", jdbcConnectionJPanel);
                    DsbieJFrame.closableTabsTabbedPane.setSelectedComponent(add);
                });
            }, FrontendThreadPool.getInstance().getExecutorService());
        }

        private static void initTabbedPane() {
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


    @Getter
    @Setter
    private class RegularJPanel extends JPanel {

        private TreeEntity treeEntity;

        public RegularJPanel() {

        }

        public RegularJPanel(TreeEntity treeEntity) {

        }

    }

    @Getter
    @Setter
    private class AdvancedJPanel extends JPanel {

        private TreeEntity treeEntity;

        public AdvancedJPanel() {

        }

        public AdvancedJPanel(TreeEntity treeEntity) {

        }
    }
}
