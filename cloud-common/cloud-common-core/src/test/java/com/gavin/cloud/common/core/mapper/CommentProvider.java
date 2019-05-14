package com.gavin.cloud.common.core.mapper;

import com.gavin.cloud.common.core.model.Comment;
import org.apache.ibatis.jdbc.SQL;

import java.util.Arrays;
import java.util.Objects;

public class CommentProvider {

    private static final Integer SLICES = 2;

    private static String COLUMNS = Arrays.stream(Comment.class.getDeclaredFields())
            .map(f -> toUnderscoreName(f.getName()))
            .reduce((s1, s2) -> s1 + "," + s2)
            .get();
    private static String FIELDS = Arrays.stream(Comment.class.getDeclaredFields())
            .map(f -> "#{" + f.getName() + "}")
            .reduce((s1, s2) -> s1 + "," + s2)
            .get();

    public String insert(Comment record) {
        return Objects.toString(new SQL() {{
            INSERT_INTO(getTableName(record.getId()));
            INTO_COLUMNS(COLUMNS);
            INTO_VALUES(FIELDS);
        }});
    }

    public String selectByPrimaryKey(Long id) {
        return Objects.toString(new SQL() {{
            SELECT(COLUMNS);
            FROM(getTableName(id));
            WHERE("id=" + id);
        }});
    }

    private static String getTableName(Long id) {
        return Comment.class.getSimpleName() + "_" + Objects.hash(id) % SLICES;
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