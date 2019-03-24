package com.gavin.cloud.common.base.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @see org.springframework.boot.json.GsonJsonParser
 * @see org.springframework.boot.json.JsonParserFactory
 */
public abstract class JsonUtils {

    private static final TypeToken<?> MAP_TYPE = new MapTypeToken();

    private static final TypeToken<?> LIST_TYPE = new ListTypeToken();

    private static Gson gson = new GsonBuilder()
            .setDateFormat(Constants.DATETIME_FORMAT)
            .create();

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
        return gson.fromJson(json, MAP_TYPE.getType());
    }

    public static List<Object> toList(String json) {
        return parseList(json, (trimmed) -> gson.fromJson(trimmed, LIST_TYPE.getType()));
    }

    private static <R> R parseList(String json, Function<String, R> parser) {
        String trimmed = json != null ? json.trim() : "";
        if (trimmed.startsWith("[")) {
            return parser.apply(trimmed);
        }
        throw new IllegalArgumentException("Cannot parse JSON");
    }

    private static final class MapTypeToken extends TypeToken<Map<String, Object>> {
    }

    private static final class ListTypeToken extends TypeToken<List<Object>> {
    }

}
