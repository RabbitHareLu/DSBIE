package com.dsbie.rearend.api;

import com.dsbie.rearend.mybatis.entity.TreeEntity;

import java.util.List;

/**
 * @author WCG
 */
public interface SystemApi {

    List<TreeEntity> getTree(int nodeId);

    void addNode(TreeEntity treeEntity);

    void updateNode(TreeEntity treeEntity);

    void deleteNode(TreeEntity treeEntity);

    void deleteChildNode(TreeEntity treeEntity);

    void saveOrUpdateProp(String key, String value);

}