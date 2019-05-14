package com.gavin.cloud.common.base.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.lang.reflect.Type;

/**
 * @see com.google.gson.reflect.TypeToken
 * @see com.google.gson.internal.$Gson$Types.ParameterizedTypeImpl
 */
public abstract class JsonUtils {

    private static Gson gson = new GsonBuilder()
            .setDateFormat(Constants.FORMATTER_DATETIME)
            .create();

    public static String toJson(Object src) {
        return gson.toJson(src);
    }

    public static <T> T fromJson(String json, Type rawType, Type... genericType) {
        return gson.fromJson(json, TypeToken.getParameterized(rawType, genericType).getType());
    }

    public static <T> T fromJson(Reader json, Type rawType, Type... genericType) {
        return gson.fromJson(json, TypeToken.getParameterized(rawType, genericType).getType());
    }

    public static <T> T[] getArray(String json, Type componentType) {
        return gson.fromJson(json, TypeToken.getArray(componentType).getType());
    }

    public static <T> T[] getArray(Reader json, Type componentType) {
        return gson.fromJson(json, TypeToken.getArray(componentType).getType());
    }

}
