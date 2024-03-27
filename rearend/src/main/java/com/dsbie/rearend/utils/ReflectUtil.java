package com.dsbie.rearend.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lsl
 * @version 1.0
 * @date 2024年03月27日 15:22
 */
public class ReflectUtil {

    /**
     * 获取当前类及其父类的所有属性
     *
     * @param clazz
     * @return {@link Field[]}
     * @author lsl
     * @date 2023/11/23 11:19
     */
    public static Field[] getAllFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        return fieldList.toArray(fields);
    }

}
