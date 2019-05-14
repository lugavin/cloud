package com.gavin.cloud.common.core.mapper;

import com.gavin.cloud.common.core.model.Counter;
import org.apache.ibatis.jdbc.SQL;

import java.util.Arrays;
import java.util.Objects;

public class CounterProvider {

    private static String COLUMNS = Arrays.stream(Counter.class.getDeclaredFields())
            .map(f -> toUnderscoreName(f.getName()))
            .reduce((s1, s2) -> s1 + "," + s2)
            .get();
    private static String FIELDS = Arrays.stream(Counter.class.getDeclaredFields())
            .map(f -> "#{" + f.getName() + "}")
            .reduce((s1, s2) -> s1 + "," + s2)
            .get();

    public String insert(Counter record) {
        return Objects.toString(new SQL() {{
            INSERT_INTO(Counter.class.getSimpleName());
            INTO_COLUMNS(COLUMNS);
            INTO_VALUES(FIELDS);
        }});
    }

    private static String toUnderscoreName(String camelCaseName) {
        StringBuilder result = new StringBuilder();
        result.append(camelCaseName.substring(0, 1).toLowerCase());
        for (int i = 1; i < camelCaseName.length(); i++) {
            String s = camelCaseName.substring(i, i + 1);
            String slc = s.toLowerCase();
            if (s.equals(slc)) {
                result.append(s);
            } else {
                result.append("_").append(slc);
            }
        }
        return result.toString();
    }

    private static String toCamelName(String underscoreName) {
        StringBuilder sb = new StringBuilder();
        for (String str : underscoreName.split("_")) {
            sb.append(Character.toTitleCase(str.charAt(0))).append(str.substring(1).toLowerCase());
        }
        String str = sb.toString();
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

}
