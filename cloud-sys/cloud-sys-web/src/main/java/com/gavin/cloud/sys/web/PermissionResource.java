package com.gavin.cloud.sys.web;

import com.gavin.cloud.sys.core.enums.ResourceType;
import com.gavin.cloud.common.web.annotation.RequiresPermissions;
import com.gavin.cloud.sys.core.service.PermissionService;
import com.gavin.cloud.sys.pojo.Permission;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/perms")
public class PermissionResource {

    private final PermissionService permissionService;

    public PermissionResource(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * GET http://cloud-sys/perms/codes/ROLE_ADMIN,ROLE_USER
     */
    @GetMapping("/codes/{roles}")
    public ResponseEntity<Set<String>> getPermissionCodes(@PathVariable String[] roles) {
        return ResponseEntity.ok(permissionService.getPermissionCodes(roles));
    }

    /**
     * GET http://cloud-sys/perms/ROLE_ADMIN,ROLE_USER/roles
     */
    @GetMapping("/roles/{roles}")
    public ResponseEntity<List<Permission>> getPermissions(@PathVariable String[] roles) {
        return ResponseEntity.ok(permissionService.getPermissions(roles));
    }

    @GetMapping("/menus/{userId}")
    public ResponseEntity<List<Permission>> getMenuPerms(@PathVariable Long userId) {
        return ResponseEntity.ok(permissionService.getPermissions(userId, ResourceType.MENU));
    }

    @GetMapping("/funcs/{userId}")
    public ResponseEntity<List<Permission>> getFuncPerms(@PathVariable Long userId) {
        return ResponseEntity.ok(permissionService.getPermissions(userId, ResourceType.FUNC));
    }

    @GetMapping
    @RequiresPermissions("permission:search")
    public ResponseEntity<List<Permission>> getPermissions() {
        return ResponseEntity.ok(permissionService.getPermissions());
    }

}
