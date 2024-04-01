package com.dsbie.frontend.panel;

import com.dsbie.frontend.component.FrameJPopupMenu;
import com.dsbie.frontend.component.LeftTree;
import com.dsbie.frontend.component.LeftTreeNode;
import com.dsbie.frontend.constant.LeftTreeNodeType;
import com.dsbie.frontend.frame.DsbieJFrame;
import com.dsbie.frontend.utils.CompletableFutureUtil;
import com.dsbie.frontend.utils.ComponentVerifierUtil;
import com.dsbie.frontend.utils.ImageLoadUtil;
import com.dsbie.rearend.KToolsContext;
import com.dsbie.rearend.api.SystemApi;
import com.dsbie.rearend.common.utils.StringUtil;
import com.dsbie.rearend.exception.KToolException;
import com.dsbie.rearend.manager.uid.UidKey;
import com.dsbie.rearend.mybatis.entity.TreeEntity;
import com.formdev.flatlaf.FlatClientProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.util.List;
import java.util.*;
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
    private JComboBox<String> dbTypeComboBox;

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
        nameInputField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        ComponentVerifierUtil.notBlank(nameInputField);
        box.add(nameInputField);

        box.add(Box.createHorizontalStrut(50));

        JLabel commentLabel = new JLabel("备注: ");
        commentLabel.setToolTipText("备注");
        box.add(commentLabel);

        box.add(Box.createHorizontalStrut(20));

        commentInputField = new JTextField(comment);
        commentInputField.setText(comment);
        commentInputField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        box.add(commentInputField);

        box.add(Box.createHorizontalStrut(100));

        verticalBox.add(box);
        verticalBox.add(Box.createVerticalStrut(30));

        Optional.ofNullable(treeEntity).ifPresent(treeEntity -> {
            nameInputField.setText(treeEntity.getNodeName());
            commentInputField.setText(treeEntity.getNodeComment());
        });

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
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        box.add(testButton);

        box.add(Box.createHorizontalGlue());

        JButton okButton = new JButton("确认");
        okButton.setBackground(new Color(53, 116, 240));
        okButton.addActionListener(e -> CompletableFutureUtil.submit(() -> {
            if (StringUtil.isBlank(nameInputField.getText())) {
                throw new KToolException("名称不能为空");
            }
            if (StringUtil.isBlank(urlInputField.getText())) {
                throw new KToolException("URL不能为空");
            }

            LeftTree instance = LeftTree.getInstance();
            TreePath selectionPath = instance.getCurrentTreePath();
            LeftTreeNode currentTreeNode = instance.getCurrentTreeNode(selectionPath);

            TreeEntity treeEntity1 = new TreeEntity();
            treeEntity1.setId(KToolsContext.getInstance().getIdGenerator().getId(UidKey.TREE));
            treeEntity1.setParentNodeId(currentTreeNode.getTreeEntity().getId());
            treeEntity1.setNodeName(nameInputField.getText());
            treeEntity1.setNodeType(LeftTreeNodeType.CONNECTION);
            treeEntity1.setNodeComment(commentInputField.getText());

            List<String> nodePathList = new ArrayList<>();
            instance.buildTreeNodePath(nodePathList, selectionPath);
            treeEntity1.setNodePath(instance.getNodePathString(nodePathList));

            Map<String, String> nodeInfoMap = new LinkedHashMap<>();
            nodeInfoMap.put("dbType", dbTypeComboBox.getItemAt(dbTypeComboBox.getSelectedIndex()));
            nodeInfoMap.put("driver", StringUtil.isBlank(driverInputField.getText()) ? (String) driverInputField.getClientProperty("JTextField.placeholderText") : driverInputField.getText());
            nodeInfoMap.put("username", usernameInputField.getText());
            nodeInfoMap.put("password", new String(passwordInputField.getPassword()));
            nodeInfoMap.put("url", urlInputField.getText());

            for (int row = 0; row < table.getRowCount(); row++) {
                String key = null;
                String value = null;
                for (int col = 0; col < table.getColumnCount(); col++) {
                    if (col == 0) {
                        key = (String) table.getValueAt(row, col);
                    } else {
                        value = (String) table.getValueAt(row, col);
                    }
                }
                if (StringUtil.isNotBlank(key)) {
                    nodeInfoMap.put(key, value);
                }
            }
            treeEntity1.setNodeInfo(nodeInfoMap);

            treeEntity1.setChild(null);

            KToolsContext.getInstance().getApi(SystemApi.class).addNode(treeEntity1);

            LeftTreeNode leftTreeNode = new LeftTreeNode(treeEntity1);
            DefaultTreeModel model = (DefaultTreeModel) instance.getTreeModel();
            SwingUtilities.invokeLater(() -> {
                model.insertNodeInto(leftTreeNode, currentTreeNode, currentTreeNode.getChildCount());
                instance.expandTreeNode(selectionPath);
                int selectedIndex = DsbieJFrame.closableTabsTabbedPane.getSelectedIndex();
                int tabCount = DsbieJFrame.closableTabsTabbedPane.getTabCount();
                if (tabCount <= 1) {
                    DsbieJFrame.rootJSplitPane.setRightComponent(DsbieJFrame.logoLabel);
                }
                DsbieJFrame.closableTabsTabbedPane.remove(selectedIndex);
            });
        }));

        box.add(okButton);
        box.add(Box.createHorizontalStrut(30));
        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            int selectedIndex = DsbieJFrame.closableTabsTabbedPane.getSelectedIndex();
            int tabCount = DsbieJFrame.closableTabsTabbedPane.getTabCount();
            if (tabCount <= 1) {
                DsbieJFrame.rootJSplitPane.setRightComponent(DsbieJFrame.logoLabel);
            }
            DsbieJFrame.closableTabsTabbedPane.remove(selectedIndex);
        }));
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
                JdbcConnectionJPanel jdbcConnectionJPanel = null;

                if (Objects.equals(source.getText(), "编辑")) {
                    TreeEntity currentTreeEntity = LeftTree.getInstance().getCurrentTreeEntity();
                    if (Objects.equals(currentTreeEntity.getNodeType(), LeftTreeNodeType.CONNECTION)) {
                        jdbcConnectionJPanel = new JdbcConnectionJPanel(currentTreeEntity);
                        JdbcConnectionJPanel finalJdbcConnectionJPanel = jdbcConnectionJPanel;
                        SwingUtilities.invokeLater(() -> {
                            Component add = DsbieJFrame.closableTabsTabbedPane.add("编辑" + currentTreeEntity.getNodeName() + "数据源", finalJdbcConnectionJPanel);
                            DsbieJFrame.closableTabsTabbedPane.setSelectedComponent(add);
                            DsbieJFrame.rootJSplitPane.setRightComponent(DsbieJFrame.closableTabsTabbedPane);
                        });
                    }
                } else {
                    jdbcConnectionJPanel = new JdbcConnectionJPanel(null);
                    JdbcConnectionJPanel finalJdbcConnectionJPanel = jdbcConnectionJPanel;
                    SwingUtilities.invokeLater(() -> {
                        Component add = DsbieJFrame.closableTabsTabbedPane.add("新建数据源", finalJdbcConnectionJPanel);
                        DsbieJFrame.closableTabsTabbedPane.setSelectedComponent(add);
                        DsbieJFrame.rootJSplitPane.setRightComponent(DsbieJFrame.closableTabsTabbedPane);
                    });
                }
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

            Box dbTypeBox = Box.createHorizontalBox();
            JLabel dbTypeLabel = new JLabel("类型: ");
            dbTypeBox.add(dbTypeLabel);
            dbTypeBox.add(Box.createHorizontalStrut(30));
            dbTypeComboBox = new JComboBox<>();
            dbTypeComboBox.setModel(new DefaultComboBoxModel<>(new String[]{
                    "Mysql",
                    "Oracle"
            }));
            dbTypeComboBox.addActionListener(e -> {
                String itemAt = dbTypeComboBox.getItemAt(dbTypeComboBox.getSelectedIndex());
                if (Objects.equals(itemAt, "Mysql")) {
                    driverInputField.putClientProperty("JTextField.placeholderText", "com.mysql.cj.jdbc.driver");
                } else if (Objects.equals(itemAt, "Oracle")) {
                    driverInputField.putClientProperty("JTextField.placeholderText", "oracle.jdbc.OracleDriver");
                }
            });
            dbTypeComboBox.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
            dbTypeBox.add(dbTypeComboBox);

            Dimension dimension = dbTypeLabel.getPreferredSize();
            double fixedWidth = 80;
            double height = dimension.getHeight();
            dimension.setSize(fixedWidth, height);
            dbTypeLabel.setPreferredSize(dimension);

            Box driverBox = Box.createHorizontalBox();
            JLabel driverLabel = new JLabel("驱动: ");
            driverBox.add(driverLabel);
            driverBox.add(Box.createHorizontalStrut(30));
            driverInputField = new JTextField();
            driverInputField.putClientProperty("JTextField.placeholderText", "com.mysql.cj.jdbc.driver");
            driverInputField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
            driverBox.add(driverInputField);
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
            passwordInputField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
            passwordBox.add(passwordInputField);
            passwordLabel.setPreferredSize(dimension);

            Box urlBox = Box.createHorizontalBox();
            JLabel urlLabel = new JLabel("URL: ");
            urlBox.add(urlLabel);
            urlBox.add(Box.createHorizontalStrut(30));
            urlInputField = new JTextField();
            urlInputField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
            ComponentVerifierUtil.notBlank(urlInputField);
            urlBox.add(urlInputField);
            urlLabel.setPreferredSize(dimension);

            verticalBox.add(dbTypeBox);
            verticalBox.add(Box.createVerticalStrut(30));
            verticalBox.add(driverBox);
            verticalBox.add(Box.createVerticalStrut(30));
            verticalBox.add(usernameBox);
            verticalBox.add(Box.createVerticalStrut(30));
            verticalBox.add(passwordBox);
            verticalBox.add(Box.createVerticalStrut(30));
            verticalBox.add(urlBox);

            Optional.ofNullable(treeEntity).ifPresent(treeEntity -> {
                Map<String, String> nodeInfo = treeEntity.getNodeInfo();
                dbTypeComboBox.setSelectedItem(nodeInfo.get("dbType"));
                driverInputField.setText(nodeInfo.get("driver"));
                usernameInputField.setText(nodeInfo.get("username"));
                passwordInputField.setText(nodeInfo.get("password"));
                urlInputField.setText(nodeInfo.get("url"));
            });

            add(verticalBox, BorderLayout.NORTH);
        }

    }

    @Getter
    @Setter
    private class AdvancedJPanel extends JPanel {

        public AdvancedJPanel() {
            setLayout(new BorderLayout());
            Box horizontalBox = Box.createHorizontalBox();

            Vector<Vector<String>> data = new Vector<>();

            if (Objects.isNull(treeEntity)) {
                Vector<String> batchDate = new Vector<>();
                batchDate.add("batchCount");
                batchDate.add("1000");
                data.add(batchDate);
            } else {
                Map<String, String> nodeInfo = treeEntity.getNodeInfo();
                for (Map.Entry<String, String> stringStringEntry : nodeInfo.entrySet()) {
                    if (!Objects.equals(stringStringEntry.getKey(), "driver") &&
                            !Objects.equals(stringStringEntry.getKey(), "username") &&
                            !Objects.equals(stringStringEntry.getKey(), "password") &&
                            !Objects.equals(stringStringEntry.getKey(), "url") &&
                            !Objects.equals(stringStringEntry.getKey(), "dbType")) {
                        Vector<String> strings = new Vector<>();
                        strings.add(stringStringEntry.getKey());
                        strings.add(stringStringEntry.getValue());
                        data.add(strings);
                    }
                }
            }

            JScrollPane tableJScrollPane = new JScrollPane();
            table = new JTable();
            Vector<String> columnNames = new Vector<>();
            columnNames.add("Key");
            columnNames.add("Value");

            defaultTableModel = new DefaultTableModel(data, columnNames) {
                @Serial
                private static final long serialVersionUID = -470007373566602554L;

                final Class<?>[] columnTypes = {
                        String.class, String.class
                };

                final boolean[] columnEditable = {
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
                    new JComboBox<>(new DefaultComboBoxModel<>(new String[]{
                            "batchCount",
                    }))));

            ((JComboBox<?>) ((DefaultCellEditor) table.getColumnModel().getColumn(0).getCellEditor()).getComponent())
                    .setEditable(true);

            table.setShowHorizontalLines(true);
            table.setShowVerticalLines(true);
            tableJScrollPane.setViewportView(table);
            horizontalBox.add(tableJScrollPane);

            JToolBar toolBar = new JToolBar();
            toolBar.setOrientation(SwingConstants.VERTICAL);
            JButton addButton = new JButton();
            addButton.setIcon(ImageLoadUtil.getInstance().getAddRowIcon());
            addButton.addActionListener(e -> CompletableFutureUtil.submit(() -> SwingUtilities.invokeLater(() -> defaultTableModel.addRow(new String[]{}))));
            toolBar.add(addButton);
            JButton deleteButton = new JButton();
            deleteButton.setIcon(ImageLoadUtil.getInstance().getDeleteRowIcon());
            deleteButton.addActionListener(e -> CompletableFutureUtil.submit(() -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    SwingUtilities.invokeLater(() -> defaultTableModel.removeRow(selectedRow));
                }
            }));
            toolBar.add(deleteButton);
            toolBar.add(Box.createVerticalGlue());
            horizontalBox.add(toolBar);

            add(horizontalBox);
        }
    }
}
