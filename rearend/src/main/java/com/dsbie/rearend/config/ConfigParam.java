package com.dsbie.rearend.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配置项注解
 *
 * @author WCG
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigParam {

    /**
     * 配置名称
     */
    String name();

    /**
     * 配置key
     */
    String key();

    /**
     * 是否必须
     */
    boolean must() default false;

    /**
     * 默认值
     */
    String defaultValue() default "";

}
