package com.dsbie.rearend.manager.uid;

import com.dsbie.rearend.mybatis.MybatisContext;
import com.dsbie.rearend.mybatis.entity.PropEntity;
import com.dsbie.rearend.mybatis.mapper.PropMapper;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;

import java.util.ArrayList;
import java.util.List;

/**
 * ID生成器
 *
 * @author WCG
 */
public class IdGenerator {

    private static final String ID_PROP_PRE = "K_TOOLS.ID.K_";

    private final PropMapper propMapper;

    public IdGenerator(MybatisContext mybatisContext) {
        this.propMapper = mybatisContext.getMapper(PropMapper.class);
    }

    /**
     * 获取id
     *
     * @param key 业务key
     * @return id
     */
    public Integer getId(UidKey key) {
        return getId(key, 1).getFirst();
    }

    public synchronized List<Integer> getId(UidKey key, int size) {
        String propKey = ID_PROP_PRE.concat(key.name());
        QueryWrapper queryWrapper = QueryChain.of(PropEntity.class).eq(PropEntity::getKey, propKey).toQueryWrapper();
        PropEntity propEntity = this.propMapper.selectOneByQuery(queryWrapper);

        if (propEntity != null) {
            int startId = Integer.parseInt(propEntity.getValue());
            int endId = startId + size;

            // 更新数据库
            UpdateChain.of(PropEntity.class)
                    .set(PropEntity::getValue, String.valueOf(endId))
                    .where(PropEntity::getKey).eq(propKey)
                    .update();

            List<Integer> result = new ArrayList<>();
            for (int i = startId; i < endId; i++) {
                result.add(i);
            }
            return result;
        }
        throw new RuntimeException("key不存在");
    }

}
