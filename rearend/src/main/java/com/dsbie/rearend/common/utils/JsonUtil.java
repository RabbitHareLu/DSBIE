package com.dsbie.rearend.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * json工具类
 *
 * @author WCG
 */
public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static <T> T readJson(String json, Class<T> tClass) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, tClass);
    }

    public static Map<String, Object> readJsonToMap(String json) throws JsonProcessingException {
        return readJsonToMap(json, String.class, Object.class);
    }

    public static <T, E> Map<T, E> readJsonToMap(String json, Class<T> kClass, Class<E> vClass) throws JsonProcessingException {
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(LinkedHashMap.class, kClass, vClass);
        return OBJECT_MAPPER.readValue(json, javaType);
    }

    public static String writeObjectToJson(Object object) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }
}
