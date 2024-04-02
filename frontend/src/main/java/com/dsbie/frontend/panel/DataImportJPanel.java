package com.dsbie.frontend.panel;

import com.dsbie.frontend.component.LeftTree;
import com.dsbie.frontend.component.LeftTreeNode;
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
        JLabel connectionLabel = new JLabel("数据源: ");
        connectionBox.add(connectionLabel);
        connectionBox.add(Box.createHorizontalStrut(30));
        connectionComboBox = new JComboBox<>();

        Pair<String, Boolean> pair = LeftTree.getInstance().buildTreeNodePath(currentTreeNode);
        log.info("{}", pair.getKey());

        DefaultComboBoxModel<String> connectionComboBoxModel = new DefaultComboBoxModel<>();
        connectionComboBox.setModel(connectionComboBoxModel);


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
