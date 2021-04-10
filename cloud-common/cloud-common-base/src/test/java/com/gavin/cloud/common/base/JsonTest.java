package com.gavin.cloud.common.base;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.*;

@Slf4j
public class JsonTest {

    @Test
    public void testJsonApiMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1L);
        map.put("name", "test");
        Jsonb jsonb = JsonbBuilder.create();
        String json = jsonb.toJson(map);
        log.debug(json);
        Map<String, Object> m = jsonb.fromJson(json, new HashMap<String, Object>() {
        }.getClass().getGenericSuperclass());
        log.debug("{}", m);
    }

    @Test
    public void testJsonApiList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", i);
            map.put("name", "test_" + i);
            list.add(map);
        }
        Jsonb jsonb = JsonbBuilder.create();
        String json = jsonb.toJson(list);
        log.debug(json);
        List<Map<String, Object>> mapList = jsonb.fromJson(json, new ArrayList<Map<String, Object>>() {
        }.getClass().getGenericSuperclass());
        log.debug("{}", mapList);
    }

    @Test
    public void testParse() {
        Map<String, String> map = JsonParser.parseMap(" {\"uid\": \"1001\", \"username\": \"admin\"} ");
        log.debug("{}", map);
    }

    static abstract class JsonParser {

        public static Map<String, String> parseMap(String json) {
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

}
