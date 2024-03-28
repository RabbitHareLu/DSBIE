package com.dsbie.rearend.api;

import com.dsbie.rearend.exception.KToolException;
import com.dsbie.rearend.mybatis.entity.TreeEntity;

import java.util.List;

/**
 * @author WCG
 */
public interface SystemApi {

    List<TreeEntity> getTree(int nodeId);

    void addNode(TreeEntity treeEntity) throws KToolException;

    void updateNode(TreeEntity treeEntity) throws KToolException;

    void deleteNode(TreeEntity treeEntity);

    void deleteChildNode(TreeEntity treeEntity);

    void saveOrUpdateProp(String key, String value);

}