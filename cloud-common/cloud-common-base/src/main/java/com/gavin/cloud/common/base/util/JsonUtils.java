package com.gavin.cloud.common.base.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public abstract class JsonUtils {

    private static Gson gson;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat(Constants.DATETIME_FORMAT);
        gson = builder.create();
    }

    public static String toJson(Object src) {
        return gson.toJson(src);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, Type type) {
        return gson.fromJson(json, type);
    }

    public static Map<String, Object> toMap(String json) {
        return fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());
    }

    public static <T> T fromMap(Map<String, Object> map, Class<T> clazz) {
        return gson.fromJson(gson.toJsonTree(map), clazz);
    }

}
