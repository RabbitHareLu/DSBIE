package com.dsbie.frontend.panel;

import com.dsbie.frontend.component.FrameJPopupMenu;
import com.dsbie.frontend.component.LeftTree;
import com.dsbie.frontend.constant.LeftTreeNodeType;
import com.dsbie.frontend.frame.DsbieJFrame;
import com.dsbie.frontend.utils.CompletableFutureUtil;
import com.dsbie.rearend.mybatis.entity.TreeEntity;
import com.formdev.flatlaf.FlatClientProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
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
    private JTextField driverInputField;
    private JTextField usernameInputField;
    private JPasswordField passwordInputField;
    private JTextField urlInputField;

    private JTable table;
    private DefaultTableModel defaultTableModel;

    private JTabbedPane tabbedPane;
    private RegularJPanel regularJPanel;
    private AdvancedJPanel advancedJPanel;

    private TreeEntity treeEntity;

    public JdbcConnectionJPanel() {

    }

    public JdbcConnectionJPanel(TreeEntity treeEntity) {
        this.treeEntity = treeEntity;
        setLayout(new BorderLayout());
        Box northBox = null;
        Box centerBox = null;

        if (Objects.nonNull(treeEntity)) {
            northBox = initNorthBox(treeEntity.getNodeName(), treeEntity.getNodeComment());
            centerBox = initCenterBox(treeEntity);
        } else {
            northBox = initNorthBox(null, null);
            centerBox = initCenterBox(null);
        }
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
        regularJPanel = new RegularJPanel();
        advancedJPanel = new AdvancedJPanel();
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
            CompletableFutureUtil.submit(() -> {
                JMenuItem source = (JMenuItem) e.getSource();
                initTabbedPane();
                SwingUtilities.invokeLater(() -> DsbieJFrame.rootJSplitPane.setRightComponent(DsbieJFrame.closableTabsTabbedPane));

                TreeEntity currentTreeEntity = LeftTree.getInstance().getCurrentTreeEntity();
                JdbcConnectionJPanel jdbcConnectionJPanel = null;
                if (Objects.equals(currentTreeEntity.getNodeType(), LeftTreeNodeType.CONNECTION)) {
                    jdbcConnectionJPanel = new JdbcConnectionJPanel(currentTreeEntity);
                } else {
                    jdbcConnectionJPanel = new JdbcConnectionJPanel(null);
                }

                JdbcConnectionJPanel finalJdbcConnectionJPanel = jdbcConnectionJPanel;
                SwingUtilities.invokeLater(() -> {
                    Component add = DsbieJFrame.closableTabsTabbedPane.add("新建" + source.getText() + "数据源", finalJdbcConnectionJPanel);
                    DsbieJFrame.closableTabsTabbedPane.setSelectedComponent(add);
                });
            });
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

        public RegularJPanel() {
            setLayout(new BorderLayout());
            Box verticalBox = Box.createVerticalBox();
            verticalBox.add(Box.createVerticalStrut(30));

            Box driverBox = Box.createHorizontalBox();
            JLabel driverLabel = new JLabel("驱动: ");
            driverBox.add(driverLabel);
            driverBox.add(Box.createHorizontalStrut(30));
            driverInputField = new JTextField();
            driverInputField.putClientProperty("JTextField.placeholderText", "com.mysql.cj.jdbc.driver");
            driverInputField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
            driverBox.add(driverInputField);

            Dimension dimension = driverLabel.getPreferredSize();
            double fixedWidth = 80;
            double height = dimension.getHeight();
            dimension.setSize(fixedWidth, height);
            driverLabel.setPreferredSize(dimension);

            Box usernameBox = Box.createHorizontalBox();
            JLabel usernameLabel = new JLabel("用户名: ");
            usernameBox.add(usernameLabel);
            usernameBox.add(Box.createHorizontalStrut(30));
            usernameInputField = new JTextField();
            usernameInputField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
            usernameBox.add(usernameInputField);
            usernameLabel.setPreferredSize(dimension);

            Box passwordBox = Box.createHorizontalBox();
            JLabel passwordLabel = new JLabel("密码: ");
            passwordBox.add(passwordLabel);
            passwordBox.add(Box.createHorizontalStrut(30));
            passwordInputField = new JPasswordField();
            passwordInputField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
            passwordBox.add(passwordInputField);
            passwordLabel.setPreferredSize(dimension);

            Box urlBox = Box.createHorizontalBox();
            JLabel urlLabel = new JLabel("URL: ");
            urlBox.add(urlLabel);
            urlBox.add(Box.createHorizontalStrut(30));
            urlInputField = new JPasswordField();
            urlInputField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
            urlBox.add(urlInputField);
            urlLabel.setPreferredSize(dimension);

            verticalBox.add(driverBox);
            verticalBox.add(Box.createVerticalStrut(30));
            verticalBox.add(usernameBox);
            verticalBox.add(Box.createVerticalStrut(30));
            verticalBox.add(passwordBox);
            verticalBox.add(Box.createVerticalStrut(30));
            verticalBox.add(urlBox);

            add(verticalBox, BorderLayout.NORTH);
        }

    }

    @Getter
    @Setter
    private class AdvancedJPanel extends JPanel {

        public AdvancedJPanel() {
            setLayout(new BorderLayout());
            JScrollPane tableJScrollPane = new JScrollPane();
            table = new JTable();
            defaultTableModel = new DefaultTableModel(new Object[][]{
                    {"item 1", "item 1b"},
                    {"item 2", "item 2b"},
            },
                    new String[]{
                            "Key", "Value"
                    }) {
                Class<?>[] columnTypes = {
                        String.class, String.class
                };

                boolean[] columnEditable = {
                        true, true
                };

                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    return columnTypes[columnIndex];
                }

                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return columnEditable[columnIndex];
                }

            };
            table.setModel(defaultTableModel);
            TableColumnModel columnModel = table.getColumnModel();
            columnModel.getColumn(0).setCellEditor(new DefaultCellEditor(
                    new JComboBox(new DefaultComboBoxModel(new String[]{
                            "BatchCount",
                            "BatchCount2",
                    }))));

            ((JComboBox) ((DefaultCellEditor) table.getColumnModel().getColumn(0).getCellEditor()).getComponent())
                    .setEditable(true);
            tableJScrollPane.setViewportView(table);
            add(tableJScrollPane);
        }
    }
}
