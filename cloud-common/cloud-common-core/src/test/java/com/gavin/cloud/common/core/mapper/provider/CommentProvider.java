package com.gavin.cloud.common.core.mapper.provider;

import com.gavin.cloud.common.core.model.Comment;
import org.apache.ibatis.jdbc.SQL;

import java.util.Arrays;
import java.util.Objects;

public class CommentProvider {

    private static final Integer SLICES = 2;

    private static String COLUMNS = Arrays.stream(Comment.class.getDeclaredFields())
            .map(f -> underscoreName(f.getName()))
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
        return "comment_" + Objects.hash(id) % SLICES;
    }

    private static String underscoreName(String name) {
        StringBuilder result = new StringBuilder();
        if (name != null && name.length() > 0) {
            result.append(name.substring(0, 1).toUpperCase());
            for (int i = 1; i < name.length(); i++) {
                String s = name.substring(i, i + 1);
                if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
                    result.append("_");
                }
                result.append(s.toUpperCase());
            }
        }
        return result.toString().toLowerCase();
    }

}