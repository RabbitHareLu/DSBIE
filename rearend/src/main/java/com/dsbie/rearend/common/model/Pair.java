package com.dsbie.rearend.common.model;

import lombok.Getter;

import java.util.Objects;

/**
 * @author lsl
 * @version 1.0
 * @date 2024年04月02日 17:59
 */
@Getter
public class Pair<K, V> {
    protected K key;
    protected V value;


    /**
     * 构建{@code Pair}对象
     *
     * @param <K>   键类型
     * @param <V>   值类型
     * @param key   键
     * @param value 值
     * @return {@code Pair}
     * @since 5.4.3
     */
    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }

    /**
     * 构造
     *
     * @param key   键
     * @param value 值
     */
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Pair [key=" + key + ", value=" + value + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o instanceof Pair<?, ?> pair) {
            return Objects.equals(getKey(), pair.getKey()) &&
                    Objects.equals(getValue(), pair.getValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        //copy from 1.8 HashMap.Node
        return Objects.hashCode(key) ^ Objects.hashCode(value);
    }
}
