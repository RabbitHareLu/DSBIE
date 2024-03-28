package com.dsbie.frontend.component;

import com.dsbie.frontend.Main;
import com.dsbie.frontend.utils.DialogUtil;
import com.dsbie.frontend.utils.ImageLoadUtil;
import com.dsbie.rearend.KToolsContext;
import com.dsbie.rearend.api.SystemApi;
import com.dsbie.rearend.common.utils.StringUtil;
import com.dsbie.rearend.exception.KToolException;
import com.dsbie.rearend.mybatis.entity.TreeEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    private FrameJPopupMenu() {
        initRootPopupMenu();
        initFolderPopupMenu();
    }

    public static FrameJPopupMenu getInstance() {
        return INSTANCE;
    }

    private void initRootPopupMenu() {
        rootPopupMenu = new JPopupMenu();
        JMenuItem rootNewFolderItem = new JMenuItem("新建目录");
        rootNewFolderItem.setIcon(ImageLoadUtil.getInstance().getNewFolderIcon());
        rootNewFolderItem.addActionListener(new LeftTree.NewFolderAction());
        rootPopupMenu.add(rootNewFolderItem);
    }

    private void initFolderPopupMenu() {
        folderPopupMenu = new JPopupMenu();
        JMenuItem folderNewFolderItem = new JMenuItem("新建目录");
        folderNewFolderItem.setIcon(ImageLoadUtil.getInstance().getNewFolderIcon());
        folderNewFolderItem.addActionListener(new LeftTree.NewFolderAction());
        folderPopupMenu.add(folderNewFolderItem);

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
    private static class DeleteTreeNodeAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JTree jTree = LeftTree.getInstance().getJTree();
            TreePath selectionPath = jTree.getSelectionPath();

            if (Objects.nonNull(selectionPath)) {
                int result = JOptionPane.showConfirmDialog(Main.dsbieJFrame, new Object[]{"是否删除当前节点"}, "删除", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    LeftTreeNode currentTreeNode = (LeftTreeNode) selectionPath.getLastPathComponent();
                    log.info("删除节点: {}", currentTreeNode.getUserObject());

                    TreeEntity treeEntity = currentTreeNode.getTreeEntity();
                    KToolsContext.getInstance().getApi(SystemApi.class).deleteNode(treeEntity);

                    DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
                    model.removeNodeFromParent(currentTreeNode);
                }
            }
        }
    }

    @Slf4j
    private static class RenameFolderAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            LeftTree instance = LeftTree.getInstance();
            TreePath selectionPath = instance.getCurrentTreePath();
            LeftTreeNode currentTreeNode = instance.getCurrentTreeNode(selectionPath);

            String result = String.valueOf(JOptionPane.showInputDialog(
                    Main.dsbieJFrame,
                    "目录名称",
                    "重命名文件夹",
                    JOptionPane.INFORMATION_MESSAGE,
                    null, null, currentTreeNode.getTreeEntity().getNodeName()
            ));

            if (StringUtil.isNotBlank(result)) {
                log.info("重命名文件夹: {} -> {}", currentTreeNode.getTreeEntity().getNodeName(), result);

                TreeEntity treeEntity = currentTreeNode.getTreeEntity();
                treeEntity.setNodeName(result);

                JFrame jFrame = (JFrame) SwingUtilities.getWindowAncestor((JMenuItem) e.getSource());
                try {
                    KToolsContext.getInstance().getApi(SystemApi.class).updateNode(treeEntity);
                } catch (KToolException ex) {
                    DialogUtil.showErrorDialog(jFrame, ex.getMessage());
                    throw new RuntimeException(ex);
                }

                currentTreeNode.setTreeEntity(treeEntity);
                instance.getDefaultTreeModel().nodeChanged(currentTreeNode);
            }
        }
    }
}