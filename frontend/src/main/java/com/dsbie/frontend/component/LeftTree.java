package com.dsbie.frontend.component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

/**
 * @author lsl
 * @version 1.0
 * @date 2024年03月28日 11:21
 */
@Data
@Slf4j
public class LeftTree {

    private static final LeftTree INSTANCE = new LeftTree();

    private JTree jTree;
    private DefaultTreeModel defaultTreeModel;

    private LeftTree() {
        TreeNode root = initTree();
        defaultTreeModel = new DefaultTreeModel(root);
        jTree.setShowsRootHandles(true);
        jTree.setCellRenderer(new TreeNodeRenderer());
        jTree.setRootVisible(false);
//        jTree.setToggleClickCount(0);
        jTree.addMouseListener(new TreeMouseAdapter());
    }

    public static LeftTree getInstance() {
        return INSTANCE;
    }

    private TreeNode initTree() {
        return null;
    }

    private class TreeMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            // 判断是否为鼠标右键点击
            if (SwingUtilities.isRightMouseButton(e)) {
                int x = e.getX();
                int y = e.getY();

                TreePath path = jTree.getClosestPathForLocation(x, y);
                Rectangle pathBounds = jTree.getPathBounds(path);

                if (Objects.nonNull(pathBounds)) {
                    if (y >= pathBounds.getY() && y <= (pathBounds.getY() + pathBounds.getHeight())) {
                        // 如果在树的节点上点击
                        jTree.setSelectionPath(path);


                    } else {
                        // 如果在树的空白处点击
                        jTree.setSelectionPath(new TreePath(jTree.getModel().getRoot()));
//                        AllJPopupMenu.getInstance().getRootPopupMenu().show(jTree, x, y);
                    }
                } else {
                    // 当前菜单没有任何节点
                    jTree.setSelectionPath(new TreePath(jTree.getModel().getRoot()));
//                    AllJPopupMenu.getInstance().getRootPopupMenu().show(jTree, x, y);
                }
            }
        }


    }

    private class TreeNodeRenderer extends DefaultTreeCellRenderer {
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            TreeNode treeNode = (TreeNode) value;
            return this;
        }
    }
}
