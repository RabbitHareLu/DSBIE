package com.dsbie.frontend.panel;

import com.dsbie.frontend.component.LeftTree;
import com.dsbie.frontend.component.LeftTreeNode;
import com.dsbie.frontend.constant.LeftTreeNodeType;
import com.dsbie.frontend.frame.DsbieJFrame;
import com.dsbie.frontend.utils.CompletableFutureUtil;
import com.dsbie.frontend.utils.TabbedPaneUtil;
import com.dsbie.rearend.common.model.Pair;
import com.dsbie.rearend.mybatis.entity.TreeEntity;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
     * 文件路径
     */
    private JTextField filePathField;
    /**
     * sql输入框
     */
    private JTextArea sqlTextArea;

    private LeftTreeNode currentTreeNode;
    private TreeEntity treeEntity;

    public DataImportJPanel(LeftTreeNode currentTreeNode) {
        this.currentTreeNode = currentTreeNode;
        this.treeEntity = currentTreeNode.getTreeEntity();
        setLayout(new BorderLayout());

        Box northBox = initNorthBox();
        add(northBox, BorderLayout.NORTH);
    }


    private Box initNorthBox() {
        Box verticalBox = Box.createVerticalBox();
        verticalBox.add(Box.createVerticalStrut(30));

        Box connectionBox = Box.createHorizontalBox();
        connectionBox.add(Box.createHorizontalStrut(100));
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
        Vector<String> connectionVector = new Vector<>();
        connectionVector.add(pair.getKey());

        DefaultComboBoxModel<String> connectionComboBoxModel = new DefaultComboBoxModel<>(connectionVector);
        connectionComboBox.setModel(connectionComboBoxModel);

        connectionBox.add(Box.createHorizontalStrut(100));

        Box schemaTableBox = Box.createHorizontalBox();
        schemaTableBox.add(Box.createHorizontalStrut(100));
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

        schemaTableBox.add(Box.createHorizontalStrut(100));


        verticalBox.add(connectionBox);
        verticalBox.add(Box.createVerticalStrut(30));
        verticalBox.add(schemaTableBox);
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
}
