package com.dsbie.frontend.component;

import com.dsbie.frontend.Main;
import com.dsbie.frontend.constant.LeftTreeNodeType;
import com.dsbie.frontend.utils.CompletableFutureUtil;
import com.dsbie.frontend.utils.DialogUtil;
import com.dsbie.frontend.utils.ImageLoadUtil;
import com.dsbie.rearend.KToolsContext;
import com.dsbie.rearend.api.SystemApi;
import com.dsbie.rearend.common.utils.CollectionUtil;
import com.dsbie.rearend.common.utils.StringUtil;
import com.dsbie.rearend.exception.KToolException;
import com.dsbie.rearend.manager.uid.UidKey;
import com.dsbie.rearend.mybatis.entity.TreeEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
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

    private FrameJPopupMenu frameJPopupMenu = FrameJPopupMenu.getInstance();

    private LeftTree() {
        LeftTreeNode root = initTree();
        defaultTreeModel = new DefaultTreeModel(root);

        jTree = new JTree(defaultTreeModel);
        jTree.setDragEnabled(true);
        jTree.setDropMode(DropMode.ON_OR_INSERT);
        jTree.setShowsRootHandles(true);
        jTree.setCellRenderer(new TreeNodeRenderer());
        jTree.setRootVisible(false);
//        jTree.setToggleClickCount(0);
        jTree.addMouseListener(new TreeMouseAdapter());
        jTree.setTransferHandler(new DummyTransferHandler());
    }

    public static LeftTree getInstance() {
        return INSTANCE;
    }

    private LeftTreeNode initTree() {
        SystemApi api = KToolsContext.getInstance().getApi(SystemApi.class);
        List<TreeEntity> tree = api.getTree(0);

        LeftTreeNode rootNode = new LeftTreeNode(0, null, "ROOT", LeftTreeNodeType.ROOT, "ROOT");
        buildTree(rootNode, tree.getFirst().getChild());
        return rootNode;
    }

    private void buildTree(LeftTreeNode parentNode, List<TreeEntity> children) {
        if (CollectionUtil.isNotEmpty(children)) {
            for (TreeEntity child : children) {
                LeftTreeNode treeNode = new LeftTreeNode(child);
                parentNode.add(treeNode);
                buildTree(treeNode, child.getChild());
            }
        }
    }

    private class TreeMouseAdapter extends MouseAdapter {

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
                        LeftTreeNode currentTreeNode = (LeftTreeNode) path.getLastPathComponent();

                        if (Objects.equals(currentTreeNode.getTreeEntity().getNodeType(), LeftTreeNodeType.FOLDER)) {
                            frameJPopupMenu.getFolderPopupMenu().show(jTree, x, y);
                        } else if (Objects.equals(currentTreeNode.getTreeEntity().getNodeType(), LeftTreeNodeType.SCHEMA)) {
//                            frameJPopupMenu.getSchemaPopupMenu().show(jTree, x, y);
                        } else if (Objects.equals(currentTreeNode.getTreeEntity().getNodeType(), LeftTreeNodeType.TABLE)) {
//                            frameJPopupMenu.getTablePopupMenu().show(jTree, x, y);
                        } else {
                            frameJPopupMenu.getConnectionPopupMenu().show(jTree, x, y);
                        }
                    } else {
                        // 如果在树的空白处点击
                        jTree.setSelectionPath(new TreePath(jTree.getModel().getRoot()));
                        frameJPopupMenu.getRootPopupMenu().show(jTree, x, y);
                    }
                } else {
                    // 当前菜单没有任何节点
                    jTree.setSelectionPath(new TreePath(jTree.getModel().getRoot()));
                    frameJPopupMenu.getRootPopupMenu().show(jTree, x, y);
                }
            }
        }


    }

    private static class TreeNodeRenderer extends DefaultTreeCellRenderer {
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            LeftTreeNode treeNode = (LeftTreeNode) value;
            this.setText(String.valueOf(treeNode.getUserObject()));

            // 处理根节点的展开和叶子节点的情况
            if (Objects.isNull(treeNode.getParent())) {
                if (tree.isRootVisible()) {
                    // 如果根节点可见，则处理展开和叶子节点的情况
                    if (expanded) {
                        setIcon(ImageLoadUtil.getInstance().getNewFolderIcon());
                    } else {
                        setIcon(ImageLoadUtil.getInstance().getFolderCloseIcon());
                    }
                }
            } else {
                // 非根节点
                switch (treeNode.getTreeEntity().getNodeType()) {
                    case LeftTreeNodeType.FOLDER -> {
                        if (expanded) {
                            setIcon(ImageLoadUtil.getInstance().getNewFolderIcon());
                        } else {
                            setIcon(ImageLoadUtil.getInstance().getFolderCloseIcon());
                        }
                    }
                    case LeftTreeNodeType.CONNECTION -> setIcon(ImageLoadUtil.getInstance().getNewJdbcIcon());
                    default -> log.info("default");
                }
            }
            return this;
        }
    }

    public TreeEntity getCurrentTreeEntity() {
        TreePath selectionPath = jTree.getSelectionPath();

        // 如果selectionPath为null, 说明未选择任何节点, 因此直接默认在根节点的目录下创建
        if (Objects.isNull(selectionPath)) {
            selectionPath = new TreePath(jTree.getModel().getRoot());
        }
        return ((LeftTreeNode) selectionPath.getLastPathComponent()).getTreeEntity();
    }

    /**
     * 判断当前节点是否还在树中
     *
     * @param leftTreeNode
     * @return {@link Boolean}
     * @author lsl
     * @date 2024/4/1 14:44
     */
    public Boolean isNodeDescendant(LeftTreeNode leftTreeNode) {
        return ((LeftTreeNode) defaultTreeModel.getRoot()).isNodeDescendant(leftTreeNode);
    }

    public TreePath getCurrentTreePath() {
        TreePath selectionPath = jTree.getSelectionPath();

        // 如果selectionPath为null, 说明未选择任何节点, 因此直接默认在根节点的目录下创建
        if (Objects.isNull(selectionPath)) {
            selectionPath = new TreePath(jTree.getModel().getRoot());
        }
        return selectionPath;
    }

    public LeftTreeNode getCurrentTreeNode(TreePath treePath) {
        return (LeftTreeNode) treePath.getLastPathComponent();
    }

    public void buildTreeNodePath(List<String> list, TreePath selectionPath) {
        LeftTreeNode currentTreeNode = (LeftTreeNode) selectionPath.getLastPathComponent();
        Integer id = currentTreeNode.getTreeEntity().getId();
        list.add(String.valueOf(id));

        TreePath parentPath = selectionPath.getParentPath();
        if (Objects.nonNull(parentPath)) {
            buildTreeNodePath(list, parentPath);
        }
    }

    public String getNodePathString(List<String> nodePathList) {
        StringBuilder nodePathString = new StringBuilder();
        for (int i = nodePathList.size() - 1; i >= 0; i--) {
            nodePathString.append(nodePathList.get(i)).append("/");
        }
        return nodePathString.delete(nodePathString.length() - 1, nodePathString.length()).toString();
    }

    public TreeModel getTreeModel() {
        return jTree.getModel();
    }

    public void expandTreeNode(TreePath selectionPath) {
        if (Objects.nonNull(selectionPath)) {
            if (!jTree.isExpanded(selectionPath)) {
                jTree.expandPath(selectionPath);
            }
        }
    }

    /*public boolean isNodeValid(TreeEntity treeEntity) {
        String nodePath = treeEntity.getNodePath();
        List<String> split = StringUtil.split(nodePath, "/");
        TreeModel treeModel = getTreeModel();
        LeftTreeNode root = (LeftTreeNode) treeModel.getRoot();
        root.isNodeChild()

        return false;
    }*/

    public static class NewFolderAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            CompletableFutureUtil.submit(() -> {
                LeftTree instance = getInstance();
                TreePath selectionPath = instance.getCurrentTreePath();
                LeftTreeNode currentTreeNode = instance.getCurrentTreeNode(selectionPath);

                String result = JOptionPane.showInputDialog(
                        Main.dsbieJFrame,
                        "目录名称",
                        "新建文件夹",
                        JOptionPane.INFORMATION_MESSAGE
                );

                if (StringUtil.isNotBlank(result)) {
                    log.info("新建文件夹: {}", result);

                    TreeEntity treeEntity = new TreeEntity();
                    treeEntity.setId(KToolsContext.getInstance().getIdGenerator().getId(UidKey.TREE));
                    treeEntity.setParentNodeId(currentTreeNode.getTreeEntity().getId());
                    treeEntity.setNodeName(result);
                    treeEntity.setNodeType(LeftTreeNodeType.FOLDER);
                    treeEntity.setNodeComment(null);
                    treeEntity.setChild(null);

                    List<String> nodePathList = new ArrayList<>();
                    instance.buildTreeNodePath(nodePathList, selectionPath);
                    treeEntity.setNodePath(instance.getNodePathString(nodePathList));

                    try {
                        KToolsContext.getInstance().getApi(SystemApi.class).addNode(treeEntity);
                    } catch (KToolException ex) {
                        DialogUtil.showErrorDialog(Main.dsbieJFrame, ex.getMessage());
                        log.error(ex.getMessage(), ex);
                        throw new RuntimeException(ex);
                    }

                    LeftTreeNode treeNode = new LeftTreeNode(treeEntity);
                    DefaultTreeModel model = (DefaultTreeModel) instance.getTreeModel();
                    SwingUtilities.invokeLater(() -> {
                        model.insertNodeInto(treeNode, currentTreeNode, currentTreeNode.getChildCount());
                        instance.expandTreeNode(selectionPath);
                    });
                }
            });
        }
    }

    private static class DummyTransferHandler
            extends TransferHandler {
        @Serial
        private static final long serialVersionUID = -2940564469811926228L;

        @Override
        protected Transferable createTransferable(JComponent c) {
            if (c instanceof JList && ((JList<?>) c).isSelectionEmpty())
                return null;
            if (c instanceof JTree && ((JTree) c).isSelectionEmpty())
                return null;
            if (c instanceof JTable && ((JTable) c).getSelectionModel().isSelectionEmpty())
                return null;

            return new StringSelection("dummy");
        }

        @Override
        public int getSourceActions(JComponent c) {
            return COPY;
        }

        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferSupport support) {
            String message = String.valueOf(support.getDropLocation());
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, message, "Drop", JOptionPane.PLAIN_MESSAGE);
            });
            return false;
        }
    }
}
