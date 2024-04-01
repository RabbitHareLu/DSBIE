package com.dsbie.rearend.config;

import com.dsbie.rearend.exception.KToolException;
import com.dsbie.rearend.manager.datasource.model.KDataSourceConfig;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * 配置项解析工具类
 *
 * @author WCG
 */
public class ConfigParamUtil {

    /**
     * 解析配置类
     *
     * @param configClass 配置类
     * @return 配置信息
     */
    public static List<KDataSourceConfig> parseConfigClass(Class<?> configClass) {
        List<KDataSourceConfig> configs = new ArrayList<>();
        for (Field field : getFields(configClass)) {
            if (field.isAnnotationPresent(ConfigParam.class)) {
                ConfigParam configParam = field.getAnnotation(ConfigParam.class);
                configs.add(new KDataSourceConfig(configParam.name(), configParam.key(), configParam.must(), configParam.defaultValue()));
            }
        }
        return configs;
    }

    /**
     * 构建配置类
     *
     * @param properties 配置
     * @param tClass     配置类
     * @param <T>        泛型
     * @return 配置类实例
     */
    public static <T> T buildConfig(Properties properties, Class<T> tClass) throws KToolException {
        try {
            T t = tClass.getConstructor().newInstance();
            for (Field field : getFields(tClass)) {
                if (field.isAnnotationPresent(ConfigParam.class)) {
                    ConfigParam configParam = field.getAnnotation(ConfigParam.class);
                    field.setAccessible(true);
                    field.set(t, properties.getOrDefault(configParam.key(), null));
                    field.setAccessible(false);
                }
            }
            return t;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new KToolException("配置类构建异常！", e);
        }
    }

    /**
     * 获取当前类及其父类的所有属性(子类属性优先)
     */
    public static List<Field> getFields(Class<?> clazz) {
        if (clazz.getSuperclass() == null) {
            return new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        }

        List<Field> fields = getFields(clazz.getSuperclass());
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        return fields;
    }

}
