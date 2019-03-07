package com.gavin.cloud.common.base;

import com.gavin.cloud.common.base.subject.Permission;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @see java.util.Optional
 * @see java.util.stream.Collectors
 */
public class Java8Test {

    private final List<Permission> permissions = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        for (int i = 0; i < 10; i++) {
            Permission permission = new Permission();
            permission.setId("id_" + i);
            permission.setCode("code_" + i);
            permissions.add(permission);
        }
    }

    @Test
    public void testStream() throws Exception {
        List<String> codes = permissions.stream().map(Permission::getCode).collect(Collectors.toList());
        System.out.println(Arrays.asList("code_5", "code_10").stream().anyMatch(codes::contains));
    }

    @Test
    public void testOptional() throws Exception {
        List<String> codes = permissions.stream().map(Permission::getCode).collect(Collectors.toList());
        Arrays.asList("code_5", "code_10").stream()
                .filter(codes::contains)
                .findFirst()
                .ifPresent(System.out::println);
    }

}
