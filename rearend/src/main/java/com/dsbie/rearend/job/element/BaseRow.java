package com.dsbie.rearend.job.element;

import com.dsbie.rearend.exception.KToolException;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;

/**
 *
 *
 * @author WCG
 */
@Getter
public class BaseRow implements Serializable {

    private final Map<String, BaseColumn> columns;

    public BaseRow(int initialCapacity) {
        this.columns = new LinkedHashMap<>(initialCapacity);
    }

    public void addField(BaseColumn value) {
        if (this.columns.containsKey(value.getColumnName().toUpperCase())) {
            throw new KToolException("列名重复：" + value.getColumnName());
        }
        this.columns.put(value.getColumnName().toUpperCase(), value);
    }

    public BaseColumn getField(String name) {
        return columns.getOrDefault(name.toUpperCase(), null);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<BaseColumn> columnList = new ArrayList<>(columns.values());
        StringJoiner joiner = new StringJoiner(",");
        for (BaseColumn baseColumn : columnList) {
            joiner.add(String.valueOf(baseColumn.getData()));
        }
        sb.append("(");
        sb.append(joiner);
        sb.append(")");
        return sb.toString();
    }

}
