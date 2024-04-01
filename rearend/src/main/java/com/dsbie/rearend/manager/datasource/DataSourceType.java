package com.dsbie.rearend.manager.datasource;

import com.dsbie.rearend.manager.datasource.jdbc.JdbcType;
import com.dsbie.rearend.manager.datasource.model.KDataSourceMetadata;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据源类型
 *
 * @author WCG
 */
@Getter
public enum DataSourceType {

    JDBC(JdbcType.values());

    private final List<Type> supportedTypes;

    DataSourceType(Type[] supportedTypes) {
        this.supportedTypes = new ArrayList<>(List.of(supportedTypes));
    }

    public List<KDataSourceMetadata> getAllMetadata() {
        return supportedTypes.stream()
                .map(Type::getMetadata)
                .toList();
    }

}
