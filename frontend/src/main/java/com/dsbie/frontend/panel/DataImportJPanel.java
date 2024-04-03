package com.dsbie.frontend.panel;

import com.dsbie.frontend.Main;
import com.dsbie.frontend.component.LeftTree;
import com.dsbie.frontend.component.LeftTreeNode;
import com.dsbie.frontend.constant.LeftTreeNodeType;
import com.dsbie.frontend.frame.DsbieJFrame;
import com.dsbie.frontend.utils.*;
import com.dsbie.rearend.KToolsContext;
import com.dsbie.rearend.api.DataSourceApi;
import com.dsbie.rearend.common.model.Pair;
import com.dsbie.rearend.common.utils.StringUtil;
import com.dsbie.rearend.mybatis.entity.TreeEntity;
import com.formdev.flatlaf.FlatClientProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.Serial;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

/**
 * 数据导入界面, 包括jdbc和kudu数据源, 其他的单独做导入界面
 *
 * @author lsl
 * @version 1.0
 * @date 2024年04月02日 16:15
 */
@Slf4j
@Getter
@Setter
public class DataImportJPanel extends JPanel {
    /**
     * 数据源
     */
    private JComboBox<String> connectionComboBox;
    /**
     * schema
     */
    private JComboBox<String> schemaComboBox;
    /**
     * 表
     */
    private JComboBox<String> tableComboBox;
    /**
     * 导入类型: csv, excel, 文本文件, sql
     */
    private JComboBox<String> importTypeComboBox;
    /**
     * sql输入框
     */
    private JTextArea sqlTextArea;
    private JTextField filePathField;
    private JTextArea logTextArea;

    /**
     * 来源数据源
     */
    private JComboBox<String> sourceConnectionComboBox;

    private JTabbedPane tabbedPane;
    private RegularJPanel regularJPanel;
    private AdvancedJPanel advancedJPanel;
    private LogJPanel logJPanel;

    private JTable table;
    private DefaultTableModel defaultTableModel;

    private LeftTreeNode currentTreeNode;
    private TreeEntity treeEntity;

    /**
     * importTypeComboBox旧值, 用于对比新值
     */
    private String oldImportType = "CSV";

    public DataImportJPanel(LeftTreeNode currentTreeNode) {
        this.currentTreeNode = currentTreeNode;
        this.treeEntity = currentTreeNode.getTreeEntity();
        setLayout(new BorderLayout());

        Box northBox = initNorthBox();
        Box southBox = initSouthBox();

        add(northBox, BorderLayout.CENTER);
        add(southBox, BorderLayout.SOUTH);
    }

    private Box initNorthBox() {
        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalStrut(100));
        regularJPanel = new RegularJPanel();
        advancedJPanel = new AdvancedJPanel();
        logJPanel = new LogJPanel();
//        addDbTypeComboBoxActionListener();
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("常规", null, regularJPanel, "常规");
        tabbedPane.addTab("高级", null, advancedJPanel, "高级");
        tabbedPane.addTab("日志", null, logJPanel, "日志");
        box.add(tabbedPane);
        box.add(Box.createHorizontalStrut(100));
        return box;
    }

    private Box initSouthBox() {
        Box verticalBox = Box.createVerticalBox();
        verticalBox.add(Box.createVerticalStrut(30));

        Box box = Box.createHorizontalBox();
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
    public static class CreateDataImportJPanelAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            CompletableFutureUtil.submit(() -> {
                TabbedPaneUtil.initTabbedPane();

                LeftTree instance = LeftTree.getInstance();
                TreePath currentTreePath = instance.getCurrentTreePath();
                LeftTreeNode currentTreeNode = instance.getCurrentTreeNode(currentTreePath);

                DataImportJPanel dataImportJPanel = new DataImportJPanel(currentTreeNode);
                SwingUtilities.invokeLater(() -> {
                    Component add = DsbieJFrame.closableTabsTabbedPane.add("数据导入", dataImportJPanel);
                    DsbieJFrame.closableTabsTabbedPane.setSelectedComponent(add);
                    DsbieJFrame.rootJSplitPane.setRightComponent(DsbieJFrame.closableTabsTabbedPane);
                });
            });
        }

    }

    @Getter
    @Setter
    private class RegularJPanel extends JPanel {

        public RegularJPanel() {
            setLayout(new BorderLayout());

            Box verticalBox = Box.createVerticalBox();
            verticalBox.add(Box.createVerticalStrut(30));

            Box connectionBox = Box.createHorizontalBox();
            JLabel connectionLabel = new JLabel("数据源:");
            connectionBox.add(connectionLabel);
            connectionBox.add(Box.createHorizontalStrut(30));

            Dimension dimension = connectionLabel.getPreferredSize();
            double fixedWidth = 80;
            double height = dimension.getHeight();
            dimension.setSize(fixedWidth, height);
            connectionLabel.setPreferredSize(dimension);

            connectionComboBox = new JComboBox<>();
            connectionBox.add(connectionComboBox);

            Pair<String, Boolean> pair = LeftTree.getInstance().buildTreeNodePath(currentTreeNode);
            log.info("当前选择的导入数据源为: {}", pair.getKey());

            Map<String, TreeEntity> dataSourceMap = KToolsContext.getInstance().getApi(DataSourceApi.class).selectAllDataSource();
            Vector<String> connectionVector = new Vector<>(dataSourceMap.keySet());

            DefaultComboBoxModel<String> connectionComboBoxModel = new DefaultComboBoxModel<>(connectionVector);
            connectionComboBox.setModel(connectionComboBoxModel);

            Box schemaTableBox = Box.createHorizontalBox();
            JLabel schemaLabel = new JLabel("Schema: ");
            schemaTableBox.add(schemaLabel);
            schemaTableBox.add(Box.createHorizontalStrut(30));
            schemaLabel.setPreferredSize(dimension);

            Vector<String> schemaVector = new Vector<>();
            Vector<String> tableVector = new Vector<>();

            schemaComboBox = new JComboBox<>(schemaVector);
            schemaTableBox.add(schemaComboBox);
            schemaTableBox.add(Box.createHorizontalStrut(50));

            JLabel tableLabel = new JLabel("表名:");
            schemaTableBox.add(tableLabel);
            schemaTableBox.add(Box.createHorizontalStrut(30));

            tableComboBox = new JComboBox<>(tableVector);
            schemaTableBox.add(tableComboBox);

            Box importTypeBox = Box.createHorizontalBox();
            JLabel importTypeLabel = new JLabel("类型: ");
            importTypeBox.add(importTypeLabel);
            importTypeBox.add(Box.createHorizontalStrut(30));
            importTypeLabel.setPreferredSize(dimension);

            String[] importTypeArr = {"CSV", "Excel", "文本文件", "SQL"};
            importTypeComboBox = new JComboBox<>(importTypeArr);
            importTypeBox.add(importTypeComboBox);

            verticalBox.add(connectionBox);
            verticalBox.add(Box.createVerticalStrut(30));
            verticalBox.add(schemaTableBox);
            verticalBox.add(Box.createVerticalStrut(30));
            verticalBox.add(importTypeBox);
            verticalBox.add(Box.createVerticalStrut(30));


            if (Objects.equals(treeEntity.getNodeType(), LeftTreeNodeType.CONNECTION)) {
                for (int i = 0; i < currentTreeNode.getChildCount(); i++) {
                    String schemaStr = (String) ((LeftTreeNode) currentTreeNode.getChildAt(i)).getUserObject();
                    schemaVector.add(schemaStr);
                }
            } else if (Objects.equals(treeEntity.getNodeType(), LeftTreeNodeType.SCHEMA)) {
                LeftTreeNode connectionTreeNode = (LeftTreeNode) currentTreeNode.getParent();
                for (int i = 0; i < connectionTreeNode.getChildCount(); i++) {
                    String schemaStr = (String) ((LeftTreeNode) connectionTreeNode.getChildAt(i)).getUserObject();
                    schemaVector.add(schemaStr);
                }
                for (int i = 0; i < currentTreeNode.getChildCount(); i++) {
                    String tableStr = (String) ((LeftTreeNode) currentTreeNode.getChildAt(i)).getUserObject();
                    tableVector.add(tableStr);
                }
                schemaComboBox.setSelectedItem(currentTreeNode.getUserObject());
            } else {
                LeftTreeNode connectionTreeNode = (LeftTreeNode) currentTreeNode.getParent().getParent();
                for (int i = 0; i < connectionTreeNode.getChildCount(); i++) {
                    String schemaStr = (String) ((LeftTreeNode) connectionTreeNode.getChildAt(i)).getUserObject();
                    schemaVector.add(schemaStr);
                }

                LeftTreeNode schemaTreeNode = (LeftTreeNode) currentTreeNode.getParent();
                for (int i = 0; i < schemaTreeNode.getChildCount(); i++) {
                    String tableStr = (String) ((LeftTreeNode) schemaTreeNode.getChildAt(i)).getUserObject();
                    tableVector.add(tableStr);
                }
                schemaComboBox.setSelectedItem(schemaTreeNode.getUserObject());
                tableComboBox.setSelectedItem(currentTreeNode.getUserObject());
            }

            connectionComboBox.setSelectedItem(pair.getKey());

            add(verticalBox, BorderLayout.NORTH);

            Box filePathBox = Box.createHorizontalBox();
            JLabel filePathLabel = new JLabel("路径: ");
            filePathBox.add(filePathLabel);
            filePathBox.add(Box.createHorizontalStrut(30));
            filePathLabel.setPreferredSize(dimension);

            JButton chooseFileButton = initChooseFileButton();
            filePathField = new JTextField();
            filePathField.setDragEnabled(true);
            filePathField.setDropTarget(new DropTarget(filePathField, new DropTargetAdapter() {
                @Override
                public void drop(DropTargetDropEvent dtde) {
                    try {
                        dtde.acceptDrop(DnDConstants.ACTION_COPY);
                        Transferable transferable = dtde.getTransferable();
                        if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                            String transferData = transferable.getTransferData(DataFlavor.javaFileListFlavor).toString();
                            String replace = transferData.replace("[", "").replace("]", "");
                            if (StringUtil.isNotBlank(replace)) {
                                File file = new File(replace);
                                if (file.isFile()) {
                                    SwingUtilities.invokeLater(() -> filePathField.setText(replace));
                                } else {
                                    DialogUtil.showErrorDialog(Main.dsbieJFrame, replace + " 不是单个文件");
                                    LogTextAreaUtil.appendLog(logTextArea, replace + " 不是单个文件");
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        throw new RuntimeException("拖拽赋值文件绝对路径异常");
                    }
                }
            }));

            filePathField.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, chooseFileButton);
            filePathBox.add(filePathField);
            verticalBox.add(filePathBox);

            JLabel sqlLabel = new JLabel("SQL: ");
            sqlLabel.setPreferredSize(dimension);
            sqlTextArea = new JTextArea();
            sqlTextArea.setColumns(10);
            JScrollPane jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(sqlTextArea);

            Box centerVertical = Box.createVerticalBox();

            Box sourceConnectionBox = Box.createHorizontalBox();
            JLabel sourceConnectionLabel = new JLabel("数据源:");
            sourceConnectionBox.add(sourceConnectionLabel);
            sourceConnectionBox.add(Box.createHorizontalStrut(30));
            sourceConnectionLabel.setPreferredSize(dimension);

            sourceConnectionComboBox = new JComboBox<>(connectionVector);
            sourceConnectionBox.add(sourceConnectionComboBox);
            importTypeComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    CompletableFutureUtil.submit(() -> {
                        String newImportType = importTypeComboBox.getItemAt(importTypeComboBox.getSelectedIndex());
                        if (!Objects.equals(newImportType, oldImportType)) {
                            if (Objects.equals(newImportType, "SQL")) {
                                SwingUtilities.invokeLater(() -> {
                                    // 删除路径box
                                    verticalBox.remove(verticalBox.getComponentCount() - 1);
                                    // 将数据源box添加到verticalBox的底部
                                    verticalBox.add(sourceConnectionBox, -1);
                                    // verticalBox的底部再添加一个30 空白行
                                    verticalBox.add(Box.createVerticalStrut(30), -1);

                                    // 复用filePathBox, 删除filePathBox中的路径输入框
                                    // 删除filePathBox中的空白列
                                    // 删除filePathBox中的路径label
                                    filePathBox.removeAll();

                                    // filePathBox添加SQL Label
                                    filePathBox.add(sqlLabel);
                                    // filePathBox添加空白列
                                    filePathBox.add(Box.createHorizontalStrut(30));
                                    // filePathBox添加jScrollPane-sqlTextArea
                                    filePathBox.add(jScrollPane);
                                    // filePathBox添加到centerVertical中
                                    centerVertical.add(filePathBox);
                                    add(centerVertical, BorderLayout.CENTER);
                                    centerVertical.revalidate();
                                });
                            } else if (Objects.equals(oldImportType, "SQL")) {
                                SwingUtilities.invokeLater(() -> {
                                    // 清空centerVertical
                                    centerVertical.removeAll();
                                    // 删除verticalBox的空白行
                                    verticalBox.remove(verticalBox.getComponentCount() - 1);
                                    // 删除verticalBox的数据源box
                                    verticalBox.remove(verticalBox.getComponentCount() - 1);

                                    filePathBox.removeAll();
                                    filePathBox.add(filePathLabel);
                                    filePathBox.add(Box.createHorizontalStrut(30));
                                    filePathBox.add(filePathField);
                                    verticalBox.add(filePathBox);

                                    centerVertical.revalidate();
                                });
                            }
                            oldImportType = newImportType;
                        }
                    });
                }
            });

            add(centerVertical, BorderLayout.CENTER);
        }

        private JButton initChooseFileButton() {
            JButton chooseFileButton = new JButton(ImageLoadUtil.getInstance().getChooseFileIcon());
            chooseFileButton.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setMultiSelectionEnabled(false);
                fileChooser.setCurrentDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
                fileChooser.addChoosableFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        if (Objects.equals(String.valueOf(importTypeComboBox.getSelectedItem()).toUpperCase(), "CSV")) {
                            return f.getName().toLowerCase().endsWith(".csv");
                        } else if (Objects.equals(String.valueOf(importTypeComboBox.getSelectedItem()).toUpperCase(), "EXCEL")) {
                            return f.getName().toLowerCase().endsWith(".xlsx") || f.getName().toLowerCase().endsWith(".xls");
                        }
                        return false;
                    }

                    @Override
                    public String getDescription() {
                        if (Objects.equals(String.valueOf(importTypeComboBox.getSelectedItem()).toUpperCase(), "CSV")) {
                            return "CSV文件(*.csv)";
                        } else if (Objects.equals(String.valueOf(importTypeComboBox.getSelectedItem()).toUpperCase(), "EXCEL")) {
                            return "Excel文件(*.xlsx, *.xls)";
                        }
                        return null;
                    }
                });
                int result = fileChooser.showOpenDialog(null);
                if (Objects.equals(result, JFileChooser.APPROVE_OPTION)) {
                    filePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }

            });
            return chooseFileButton;
        }

    }

    @Getter
    @Setter
    private class AdvancedJPanel extends JPanel {
        public AdvancedJPanel() {
            setLayout(new BorderLayout());
            Box horizontalBox = Box.createHorizontalBox();

            JScrollPane tableJScrollPane = new JScrollPane();
            table = new JTable();
            Vector<String> columnNames = new Vector<>();
            columnNames.add("Key");
            columnNames.add("Value");

            Vector<Vector<String>> data = new Vector<>();

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
            initKeyJComboBox();

            table.setShowHorizontalLines(true);
            table.setShowVerticalLines(true);
            tableJScrollPane.setViewportView(table);
            horizontalBox.add(tableJScrollPane);

            tableJScrollPane.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (table.isEditing()) {
                        table.getCellEditor().stopCellEditing();
                    }
                }
            });

            JToolBar toolBar = new JToolBar();
            toolBar.setOrientation(SwingConstants.VERTICAL);
            JButton addButton = new JButton();
            addButton.setIcon(ImageLoadUtil.getInstance().getAddRowIcon());
            addButton.addActionListener(e -> CompletableFutureUtil.submit(() -> SwingUtilities.invokeLater(() -> {
                if (table.isEditing()) {
                    table.getCellEditor().stopCellEditing();
                }
                defaultTableModel.addRow(new String[]{});
            })));
            toolBar.add(addButton);
            JButton deleteButton = new JButton();
            deleteButton.setIcon(ImageLoadUtil.getInstance().getDeleteRowIcon());
            deleteButton.addActionListener(e -> CompletableFutureUtil.submit(() -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    SwingUtilities.invokeLater(() -> {
                        if (table.isEditing()) {
                            table.getCellEditor().stopCellEditing();
                        }
                        defaultTableModel.removeRow(selectedRow);
                    });
                }
            }));
            toolBar.add(deleteButton);
            toolBar.add(Box.createVerticalGlue());
            horizontalBox.add(toolBar);

            add(horizontalBox, BorderLayout.CENTER);
        }
    }

    @Getter
    @Setter
    private class LogJPanel extends JPanel {
        public LogJPanel() {
            setLayout(new BorderLayout());
            Box horizontalBox = Box.createHorizontalBox();

            logTextArea = new JTextArea();
            logTextArea.setWrapStyleWord(true);
            JScrollPane logJScrollPane = new JScrollPane();
            logJScrollPane.setViewportView(logTextArea);
            horizontalBox.add(logJScrollPane);

            JToolBar toolBar = new JToolBar();
            toolBar.setOrientation(SwingConstants.VERTICAL);
            JToggleButton lineWrapButton = new JToggleButton();
            lineWrapButton.setIcon(ImageLoadUtil.getInstance().getLineWrapIcon());
            lineWrapButton.addActionListener(e -> logTextArea.setLineWrap(!logTextArea.getLineWrap()));
            toolBar.add(lineWrapButton);

            JButton clearButton = new JButton();
            clearButton.setIcon(ImageLoadUtil.getInstance().getDeleteIcon());
            clearButton.addActionListener(e -> logTextArea.setText(""));
            toolBar.add(clearButton);

            toolBar.add(Box.createVerticalGlue());
            horizontalBox.add(toolBar);

            add(horizontalBox, BorderLayout.CENTER);
        }
    }

    /**
     * 设置key列下拉框的值
     *
     * @param
     * @return
     * @author lsl
     * @date 2024/4/1 20:27
     */
    private void initKeyJComboBox() {
        TableColumnModel columnModel = table.getColumnModel();

        Vector<String> comboBoxVector = new Vector<>();
        // key列下拉
       /* KDataSourceMetadata kDataSourceMetadata = supportJdbcMap.get(dbTypeComboBox.getItemAt(dbTypeComboBox.getSelectedIndex()));
        for (KDataSourceConfig kDataSourceConfig : kDataSourceMetadata.getConfig()) {
            if (!Objects.equals(kDataSourceConfig.getKey(), "username") &&
                    !Objects.equals(kDataSourceConfig.getKey(), "password") &&
                    !Objects.equals(kDataSourceConfig.getKey(), "jdbcUrl")) {
                comboBoxVector.add(kDataSourceConfig.getKey());
            }
        }*/

        columnModel.getColumn(0).setCellEditor(new DefaultCellEditor(
                new JComboBox<>(new DefaultComboBoxModel<>(comboBoxVector))));

        ((JComboBox<?>) ((DefaultCellEditor) table.getColumnModel().getColumn(0).getCellEditor()).getComponent())
                .setEditable(true);
    }
}
