package com.dsbie.frontend.component;

import com.dsbie.frontend.Main;
import com.dsbie.frontend.constant.LeftTreeNodeType;
import com.dsbie.frontend.frame.DsbieJFrame;
import com.dsbie.frontend.panel.JdbcConnectionJPanel;
import com.dsbie.frontend.utils.CompletableFutureUtil;
import com.dsbie.frontend.utils.DialogUtil;
import com.dsbie.frontend.utils.ImageLoadUtil;
import com.dsbie.rearend.KToolsContext;
import com.dsbie.rearend.api.DataSourceApi;
import com.dsbie.rearend.api.SystemApi;
import com.dsbie.rearend.common.utils.StringUtil;
import com.dsbie.rearend.exception.KToolException;
import com.dsbie.rearend.manager.uid.UidKey;
import com.dsbie.rearend.mybatis.entity.TreeEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author lsl
 * @version 1.0
 * @date 2024年03月28日 20:03
 */
@Slf4j
@Data
public class FrameJPopupMenu {
    private static final FrameJPopupMenu INSTANCE = new FrameJPopupMenu();
    private JPopupMenu rootPopupMenu;
    private JPopupMenu folderPopupMenu;
    private JPopupMenu tabbedPanePopupMenu;
    private JPopupMenu connectionPopupMenu;
    private JPopupMenu schemaPopupMenu;
    private JPopupMenu tablePopupMenu;

    private FrameJPopupMenu() {
        initRootPopupMenu();
        initFolderPopupMenu();
        initTabbedPanePopupMenu();
        initConnectionPopupMenu();
        initSchemaPopupMenu();
        initTablePopupMenu();
    }

    public static FrameJPopupMenu getInstance() {
        return INSTANCE;
    }

    private void initTablePopupMenu() {
        tablePopupMenu = new JPopupMenu();
        JMenuItem importItem = new JMenuItem("导入");
        importItem.setIcon(ImageLoadUtil.getInstance().getImportIcon());
//        importItem.addActionListener(new RefreshSchemaNodeAction());
        tablePopupMenu.add(importItem);

        JMenuItem exportItem = new JMenuItem("导出");
        exportItem.setIcon(ImageLoadUtil.getInstance().getExportIcon());
//        exportItem.addActionListener(new RefreshSchemaNodeAction());
        tablePopupMenu.add(exportItem);
    }

    private void initSchemaPopupMenu() {
        schemaPopupMenu = new JPopupMenu();
        JMenuItem refreshItem = new JMenuItem("刷新");
        refreshItem.setIcon(ImageLoadUtil.getInstance().getRefreshIcon());
        refreshItem.addActionListener(new RefreshSchemaNodeAction());
        schemaPopupMenu.add(refreshItem);

        JMenuItem deleteDeleteItem = new JMenuItem("删除");
        deleteDeleteItem.setIcon(ImageLoadUtil.getInstance().getDeleteIcon());
        deleteDeleteItem.addActionListener(new DeleteTreeNodeAction());
        schemaPopupMenu.add(deleteDeleteItem);
    }

    private void initConnectionPopupMenu() {
        connectionPopupMenu = new JPopupMenu();
        JMenuItem refreshItem = new JMenuItem("刷新");
        refreshItem.setIcon(ImageLoadUtil.getInstance().getRefreshIcon());
        refreshItem.addActionListener(new RefreshConnectionNodeAction());
        connectionPopupMenu.add(refreshItem);

        JMenuItem editItem = new JMenuItem("编辑");
        editItem.setIcon(ImageLoadUtil.getInstance().getEditIcon());
        editItem.addActionListener(new JdbcConnectionJPanel.CreateJdbcConnectionJPanelAction());
        connectionPopupMenu.add(editItem);

        JMenuItem deleteDeleteItem = new JMenuItem("删除");
        deleteDeleteItem.setIcon(ImageLoadUtil.getInstance().getDeleteIcon());
        deleteDeleteItem.addActionListener(new DeleteTreeNodeAction());
        connectionPopupMenu.add(deleteDeleteItem);
    }

    private void initTabbedPanePopupMenu() {
        tabbedPanePopupMenu = new JPopupMenu();
        JMenuItem closeTabbedItem = new JMenuItem("关闭所有");
        closeTabbedItem.setIcon(ImageLoadUtil.getInstance().getCloseTabbedIcon());
        closeTabbedItem.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            DsbieJFrame.closableTabsTabbedPane.removeAll();
            DsbieJFrame.rootJSplitPane.setRightComponent(DsbieJFrame.logoLabel);
        }));
        tabbedPanePopupMenu.add(closeTabbedItem);
    }

    private void initRootPopupMenu() {
        rootPopupMenu = new JPopupMenu();
        JMenuItem rootNewFolderItem = new JMenuItem("新建目录");
        rootNewFolderItem.setIcon(ImageLoadUtil.getInstance().getNewFolderIcon());
        rootNewFolderItem.addActionListener(new LeftTree.NewFolderAction());
        rootPopupMenu.add(rootNewFolderItem);

        JMenuItem newJDBCConnection = new JMenuItem("新建JDBC连接");
        newJDBCConnection.setIcon(ImageLoadUtil.getInstance().getNewJdbcIcon());
        newJDBCConnection.addActionListener(new JdbcConnectionJPanel.CreateJdbcConnectionJPanelAction());

        rootPopupMenu.add(newJDBCConnection);
    }

    private void initFolderPopupMenu() {
        folderPopupMenu = new JPopupMenu();

        JMenuItem folderNewFolderItem = new JMenuItem("新建目录");
        folderNewFolderItem.setIcon(ImageLoadUtil.getInstance().getNewFolderIcon());
        folderNewFolderItem.addActionListener(new LeftTree.NewFolderAction());
        folderPopupMenu.add(folderNewFolderItem);

        JMenuItem newJDBCConnection = new JMenuItem("新建JDBC连接");
        newJDBCConnection.setIcon(ImageLoadUtil.getInstance().getNewJdbcIcon());
        newJDBCConnection.addActionListener(new JdbcConnectionJPanel.CreateJdbcConnectionJPanelAction());
        folderPopupMenu.add(newJDBCConnection);

        JMenuItem folderRenameFolderItem = new JMenuItem("重命名");
        folderRenameFolderItem.setIcon(ImageLoadUtil.getInstance().getRenameIcon());
        folderRenameFolderItem.addActionListener(new RenameFolderAction());
        folderPopupMenu.add(folderRenameFolderItem);

        JMenuItem folderDeleteItem = new JMenuItem("删除");
        folderDeleteItem.setIcon(ImageLoadUtil.getInstance().getDeleteIcon());
        folderDeleteItem.addActionListener(new DeleteTreeNodeAction());
        folderPopupMenu.add(folderDeleteItem);
    }

    @Slf4j
    private static class RefreshSchemaNodeAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            CompletableFutureUtil.submit(() -> {
                LeftTree instance = LeftTree.getInstance();
                TreePath selectionPath = instance.getCurrentTreePath();
                LeftTreeNode currentTreeNode = instance.getCurrentTreeNode(selectionPath);
                LeftTreeNode jdbcNode = instance.getCurrentTreeNode(new TreePath(currentTreeNode.getParent()));
                TreeEntity jdbcTreeEntity = jdbcNode.getTreeEntity();
                TreeEntity treeEntity = currentTreeNode.getTreeEntity();
                String dbType = jdbcTreeEntity.getNodeInfo().get("dbType");

                KToolsContext.getInstance().getApi(DataSourceApi.class).conn(String.valueOf(jdbcTreeEntity.getId()), dbType, jdbcTreeEntity.getNodeInfo());
                if (currentTreeNode.getChildCount() > 0) {
                    KToolsContext.getInstance().getApi(SystemApi.class).deleteChildNode(treeEntity);
                    instance.deleteTreeChildNode(currentTreeNode);
                }

                List<String> tableNameList = KToolsContext.getInstance().getApi(DataSourceApi.class)
                        .selectAllTable(String.valueOf(jdbcNode.getTreeEntity().getId()), treeEntity.getNodeName());

                for (String tableName : tableNameList) {
                    TreeEntity newTreeEntity = new TreeEntity();
                    newTreeEntity.setId(KToolsContext.getInstance().getIdGenerator().getId(UidKey.TREE));
                    newTreeEntity.setParentNodeId(treeEntity.getId());
                    newTreeEntity.setNodeName(tableName);
                    newTreeEntity.setNodeType(LeftTreeNodeType.TABLE);
                    List<String> nodePathList = new ArrayList<>();
                    instance.buildTreeNodePath(nodePathList, selectionPath);
                    newTreeEntity.setNodePath(instance.getNodePathString(nodePathList));
                    newTreeEntity.setNodeInfo(null);
                    newTreeEntity.setChild(null);

                    KToolsContext.getInstance().getApi(SystemApi.class).addNode(newTreeEntity);

                    LeftTreeNode treeNode = new LeftTreeNode(newTreeEntity);
                    currentTreeNode.add(treeNode);
                }
                SwingUtilities.invokeLater(() -> {
                    instance.getDefaultTreeModel().nodeStructureChanged(currentTreeNode);
                    instance.expandTreeNode(selectionPath);
                });
            });
        }
    }

    @Slf4j
    private static class RefreshConnectionNodeAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            CompletableFutureUtil.submit(() -> {
                LeftTree instance = LeftTree.getInstance();
                TreePath selectionPath = instance.getCurrentTreePath();
                LeftTreeNode currentTreeNode = instance.getCurrentTreeNode(selectionPath);
                TreeEntity currentTreeEntity = currentTreeNode.getTreeEntity();
                String dbType = currentTreeEntity.getNodeInfo().get("dbType");

                KToolsContext.getInstance().getApi(DataSourceApi.class).conn(String.valueOf(currentTreeEntity.getId()), dbType, currentTreeEntity.getNodeInfo());
                if (currentTreeNode.getChildCount() > 0) {
                    KToolsContext.getInstance().getApi(SystemApi.class).deleteChildNode(currentTreeEntity);
                    instance.deleteTreeChildNode(currentTreeNode);
                }
                List<String> schemaList = KToolsContext.getInstance().getApi(DataSourceApi.class).selectAllSchema(String.valueOf(currentTreeEntity.getId()));
                for (String schema : schemaList) {
                    TreeEntity newTreeEntity = new TreeEntity();
                    newTreeEntity.setId(KToolsContext.getInstance().getIdGenerator().getId(UidKey.TREE));
                    newTreeEntity.setParentNodeId(currentTreeEntity.getId());
                    newTreeEntity.setNodeName(schema);
                    newTreeEntity.setNodeType(LeftTreeNodeType.SCHEMA);
                    newTreeEntity.setNodeComment(null);

                    List<String> nodePathList = new ArrayList<>();
                    instance.buildTreeNodePath(nodePathList, selectionPath);
                    newTreeEntity.setNodePath(instance.getNodePathString(nodePathList));
                    newTreeEntity.setNodeInfo(null);
                    newTreeEntity.setChild(null);

                    KToolsContext.getInstance().getApi(SystemApi.class).addNode(newTreeEntity);
                    LeftTreeNode treeNode = new LeftTreeNode(newTreeEntity);

                    currentTreeNode.add(treeNode);
                }
                SwingUtilities.invokeLater(() -> {
                    instance.getDefaultTreeModel().nodeStructureChanged(currentTreeNode);
                    instance.expandTreeNode(selectionPath);
                });
            });
        }
    }

    @Slf4j
    private static class DeleteTreeNodeAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            CompletableFutureUtil.submit(() -> {
                JTree jTree = LeftTree.getInstance().getJTree();
                TreePath selectionPath = jTree.getSelectionPath();

                if (Objects.nonNull(selectionPath)) {
                    int result = JOptionPane.showConfirmDialog(Main.dsbieJFrame, new Object[]{"是否删除当前节点"}, "删除", JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        LeftTreeNode currentTreeNode = (LeftTreeNode) selectionPath.getLastPathComponent();
                        log.info("删除节点: {}", currentTreeNode.getUserObject());

                        TreeEntity treeEntity = currentTreeNode.getTreeEntity();
                        KToolsContext.getInstance().getApi(SystemApi.class).deleteNode(treeEntity);
                        SwingUtilities.invokeLater(() -> {
                            DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
                            model.removeNodeFromParent(currentTreeNode);
                        });
                    }
                }
            });
        }
    }

    @Slf4j
    public static class RenameFolderAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            CompletableFutureUtil.submit(() -> {
                LeftTree instance = LeftTree.getInstance();
                TreePath selectionPath = instance.getCurrentTreePath();
                LeftTreeNode currentTreeNode = instance.getCurrentTreeNode(selectionPath);
                if (Objects.equals(currentTreeNode.getTreeEntity().getNodeType(), LeftTreeNodeType.ROOT)) {
                    DialogUtil.showErrorDialog(Main.dsbieJFrame, "请先选中一个节点进行编辑!");
                    log.error("不允许重命名Root节点");
                    throw new KToolException("不允许重命名Root节点");
                }

                String oldNodeName = currentTreeNode.getTreeEntity().getNodeName();

                Object o = JOptionPane.showInputDialog(
                        Main.dsbieJFrame,
                        "节点名称",
                        "重命名节点",
                        JOptionPane.INFORMATION_MESSAGE,
                        null, null, oldNodeName
                );

                if (Objects.nonNull(o)) {
                    String result = String.valueOf(o);
                    if (StringUtil.isNotBlank(result)) {
                        log.info("重命名节点: {} -> {}", oldNodeName, result);
                        if (!Objects.equals(oldNodeName, result)) {
                            TreeEntity treeEntity = currentTreeNode.getTreeEntity();
                            treeEntity.setNodeName(result);

                            KToolsContext.getInstance().getApi(SystemApi.class).updateNode(treeEntity);

                            SwingUtilities.invokeLater(() -> {
                                currentTreeNode.setTreeEntity(treeEntity);
                                instance.getDefaultTreeModel().nodeChanged(currentTreeNode);
                            });
                        } else {
                            log.info("重命名未修改名称, 不做任何操作");
                        }
                    }
                }
            });
        }
    }
}
