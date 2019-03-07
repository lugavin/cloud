package com.gavin.cloud.common.base.subject;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;

@Data
@ToString
public class Subject implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private String langKey;
    private Boolean activated;
    private Date resetDate;
    private String createdBy;
    private Date createdDate;
    private String lastModifiedBy;
    private Date lastModifiedDate;

    private final List<Permission> menus = new ArrayList<>();
    private final List<Permission> funcs = new ArrayList<>();
    private final Set<String> perms = new HashSet<>();

    public List<Permission> getMenus() {
        return menus;
    }

    public List<Permission> getFuncs() {
        return funcs;
    }

    public Set<String> getPerms() {
        return perms;
    }

    public boolean isPermittedAll(String... permissions) {
        return isPermittedAll(Arrays.asList(permissions));
    }

    public boolean isPermittedAll(Collection<String> permissions) {
        return perms.containsAll(permissions);
    }

    public boolean isPermittedAny(String... permissions) {
        return isPermittedAny(Arrays.asList(permissions));
    }

    public boolean isPermittedAny(Collection<String> permissions) {
        return permissions.stream().anyMatch(perms::contains);
    }

    public void addMenus(Collection<Permission> menus) {
        this.menus.addAll(menus);
        addPerms(menus);
    }

    public void addFuncs(Collection<Permission> funcs) {
        this.funcs.addAll(funcs);
        addPerms(funcs);
    }

    private void addPerms(Collection<Permission> perms) {
        perms.stream()
                .filter(perm -> StringUtils.isNotBlank(perm.getCode()))
                .map(Permission::getCode)
                .forEach(this.perms::add);
    }

}
