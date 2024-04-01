package com.dsbie.frontend.component;

import com.dsbie.rearend.mybatis.entity.TreeEntity;
import lombok.Data;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author lsl
 * @version 1.0
 * @date 2024年03月28日 14:15
 */
@Data
public class LeftTreeNode extends DefaultMutableTreeNode {

    private TreeEntity treeEntity;

    public LeftTreeNode() {

    }

    public LeftTreeNode(TreeEntity treeEntity) {
        super(treeEntity.getNodeName());
        this.treeEntity = treeEntity;
    }

    public LeftTreeNode(Integer id, Integer parentNodeId, String nodeName, String nodeType, String nodeComment) {
        super(nodeName);
        this.treeEntity = new TreeEntity(id, parentNodeId, nodeName, nodeType, nodeComment, "0", null, null);
    }

    public void setTreeEntity(TreeEntity treeEntity) {
        setUserObject(treeEntity.getNodeName());
        this.treeEntity = treeEntity;
    }

}
