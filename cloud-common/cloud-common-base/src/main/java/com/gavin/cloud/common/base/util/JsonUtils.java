package com.gavin.cloud.common.base.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

/**
 * @see com.google.gson.reflect.TypeToken
 * @see com.google.gson.internal.$Gson$Types.ParameterizedTypeImpl
 */
public abstract class JsonUtils {

    private static final TypeToken<?> MAP_TYPE = new MapTypeToken();

    private static Gson gson = new GsonBuilder()
            .setDateFormat(Constants.FORMATTER_DATETIME)
            .create();

    public static String toJson(Object src) {
        return gson.toJson(src);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static <T> List<T> getList(String json, Class<T> clazz) {
        return gson.fromJson(json, TypeToken.getParameterized(List.class, clazz).getType());
    }

    public static <T> T[] getArray(String json, Class<T> clazz) {
        return gson.fromJson(json, TypeToken.getArray(clazz).getType());
    }

    public static Map<String, Object> getMap(String json) {
        return gson.fromJson(json, MAP_TYPE.getType());
    }

    private static final class MapTypeToken extends TypeToken<Map<String, Object>> {
    }

}
