package com.dsbie.rearend.manager.datasource;

import com.dsbie.rearend.manager.datasource.jdbc.JdbcType;
import com.dsbie.rearend.manager.datasource.model.KDataSourceMetadata;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public Map<String, KDataSourceMetadata> getAllMetadata() {
        return supportedTypes.stream()
                .map(Type::getMetadata)
                .collect(Collectors.toMap(KDataSourceMetadata::getName, Function.identity()));
    }

}
