package com.gavin.cloud.common.base.auth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ActiveUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String uid;
    private final String username;
    private final String clientIP;
    private final List<String> roles;

    public ActiveUser(String uid, String username, String clientIP, List<String> roles) {
        this.uid = uid;
        this.username = username;
        this.clientIP = clientIP;
        this.roles = Optional.ofNullable(roles).orElseGet(ArrayList::new);
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getClientIP() {
        return clientIP;
    }

    public List<String> getRoles() {
        return roles;
    }

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
