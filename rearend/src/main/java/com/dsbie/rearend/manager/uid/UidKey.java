package com.dsbie.rearend.manager.uid;

import com.dsbie.rearend.mybatis.mapper.PropMapper;
import com.dsbie.rearend.mybatis.mapper.TreeMapper;
import com.mybatisflex.core.BaseMapper;
import lombok.Getter;

/**
 * @author WCG
 */

@Getter
public enum UidKey {

    PROP(PropMapper.class),

    TREE(TreeMapper.class);

    private final Class<? extends BaseMapper<?>> mapper;

    UidKey(Class<? extends BaseMapper<?>> mapper) {
        this.mapper = mapper;
    }

}
