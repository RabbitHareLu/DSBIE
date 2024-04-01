package com.dsbie.rearend.service;

import com.dsbie.rearend.api.SystemApi;
import com.dsbie.rearend.common.utils.CollectionUtil;
import com.dsbie.rearend.exception.KToolException;
import com.dsbie.rearend.mybatis.entity.PropEntity;
import com.dsbie.rearend.mybatis.entity.TreeEntity;
import com.dsbie.rearend.mybatis.mapper.PropMapper;
import com.dsbie.rearend.mybatis.mapper.TreeMapper;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.core.update.UpdateWrapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author WCG
 */
public class SystemService extends BaseService implements SystemApi {

    @Override
    public List<TreeEntity> getTree(int nodeId) {
        List<TreeEntity> treeModelList = QueryChain.of(TreeEntity.class).list();

        Map<Integer, List<TreeEntity>> map = treeModelList
                .stream()
                .filter(node -> node.getParentNodeId() != null)
                .collect(Collectors.groupingBy(TreeEntity::getParentNodeId));

        treeModelList.forEach(node -> node.setChild(map.get(node.getId())));

        return treeModelList.stream().filter(node -> Objects.equals(node.getId(), nodeId)).toList();
    }

    @Override
    public void addNode(TreeEntity treeEntity) {
        // 检查名称是否相同
        checkNodeName(treeEntity);
        // 更新数据库
        this.getMapper(TreeMapper.class).insert(treeEntity);
    }

    @Override
    public void updateNode(TreeEntity treeEntity) {
        // 检查名称是否相同
        checkNodeName(treeEntity);
        // 更新数据库
        this.getMapper(TreeMapper.class).update(treeEntity);
    }

    private void checkNodeName(TreeEntity treeEntity) {
        String nodePath = treeEntity.getNodePath();
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(TreeEntity::getNodePath).eq(nodePath)
                .and(TreeEntity::getNodeName).eq(treeEntity.getNodeName())
                .and(TreeEntity::getId).ne(treeEntity.getId());
        List<TreeEntity> treeEntities = this.getMapper(TreeMapper.class).selectListByQuery(queryWrapper);
        if (CollectionUtil.isNotEmpty(treeEntities)) {
            throw new KToolException("节点名称已存在！");
        }
    }

    @Override
    public void deleteNode(TreeEntity treeEntity) {
        // 删除当前节点
        this.getMapper(TreeMapper.class).delete(treeEntity);
        // 删除全部子节点
        deleteChildNode(treeEntity);
    }

    @Override
    public void deleteChildNode(TreeEntity treeEntity) {
        // 删除全部子节点
        String path = treeEntity.getNodePath().concat("/").concat(String.valueOf(treeEntity.getId()));
        QueryWrapper queryWrapper = QueryWrapper.create().where(TreeEntity::getNodePath).likeLeft(path);
        this.getMapper(TreeMapper.class).deleteByQuery(queryWrapper);
    }

    @Override
    public void saveOrUpdateProp(String key, String value) {
        Properties properties = this.kToolsContext.getProperties();
        if (properties.containsKey(key)) {
            // 更新属性
            properties.replace(key, value);
            // 更新数据库
            UpdateChain.of(PropEntity.class)
                    .set(PropEntity::getValue, value)
                    .where(PropEntity::getKey).eq(key)
                    .update();
        } else {
            // 新增属性
            properties.put(key, value);
            // 更新数据库
            PropEntity entity = UpdateWrapper.of(PropEntity.class)
                    .set(PropEntity::getKey, key)
                    .set(PropEntity::getValue, value)
                    .toEntity();
            this.getMapper(PropMapper.class).insert(entity);
        }
    }

}
