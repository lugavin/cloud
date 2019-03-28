package com.gavin.cloud.distributed.jwt;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class JsonParser {

    public static Map<String, String> parse(String json) {
        if (json != null) {
            json = json.trim();
            if (json.startsWith("{")) {
                return parseMapInternal(json);
            } else if (json.equals("")) {
                return new LinkedHashMap<>();
            }
        }
        throw new IllegalArgumentException("Invalid JSON (null)");
    }

    public static String toJson(Map<String, String> params) {
        StringBuilder builder = new StringBuilder("{");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            appendField(builder, entry.getKey(), entry.getValue());
        }
        builder.append("}");
        return builder.toString();
    }

    private static void appendField(StringBuilder builder, String name, String value) {
        if (builder.length() > 1) {
            builder.append(",");
        }
        builder.append("\"").append(name).append("\":\"").append(value).append("\"");
    }

    private static Map<String, String> parseMapInternal(String json) {
        Map<String, String> map = new LinkedHashMap<>();
        json = trimLeadingCharacter(trimTrailingCharacter(json, '}'), '{');
        for (String pair : json.split(",")) {
            String[] values = pair.split(":");
            String key = strip(values[0], '"');
            String value = null;
            if (values.length > 0) {
                value = strip(values[1], '"');
            }
            if (map.containsKey(key)) {
                throw new IllegalArgumentException("Duplicate '" + key + "' field");
            }
            map.put(key, value);
        }
        return map;
    }

    private static String strip(String str, char c) {
        return trimLeadingCharacter(trimTrailingCharacter(str.trim(), c), c);
    }

    private static String trimLeadingCharacter(String str, char c) {
        if (str.length() >= 0 && str.charAt(0) == c) {
            return str.substring(1);
        }
        return str;
    }

    private static String trimTrailingCharacter(String str, char c) {
        if (str.length() >= 0 && str.charAt(str.length() - 1) == c) {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }

}
