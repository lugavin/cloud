package com.gavin.cloud.common.base.auth;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Data
public class ActiveUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Long uid;
    private final String username;
    private final String clientIP;
    private final List<String> roles;

    public boolean hasRole(String role) {
        return this.roles.contains(role);
    }

    public boolean hasAnyRole(String... roles) {
        return Arrays.stream(roles).anyMatch(this.roles::contains);
    }

    public boolean hasAnyRole(Collection<String> roles) {
        return roles.stream().anyMatch(this.roles::contains);
    }

    public boolean hasAllRoles(String... roles) {
        return Arrays.stream(roles).allMatch(this.roles::contains);
    }

    public boolean hasAllRoles(Collection<String> roles) {
        return this.roles.containsAll(roles);
    }

}
