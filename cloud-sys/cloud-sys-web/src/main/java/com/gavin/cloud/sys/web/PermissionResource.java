package com.gavin.cloud.sys.web;

import com.gavin.cloud.common.web.annotation.RequiresPermissions;
import com.gavin.cloud.sys.core.service.PermissionService;
import com.gavin.cloud.sys.pojo.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/permissions")
public class PermissionResource {

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/{userId}/menus")
    public ResponseEntity<List<Permission>> getMenuPermissions(@PathVariable String userId) {
        return ResponseEntity.ok(permissionService.getMenuPermissions(userId));
    }

    @GetMapping("/{userId}/funcs")
    public ResponseEntity<List<Permission>> getFuncPermissions(@PathVariable String userId) {
        return ResponseEntity.ok(permissionService.getFuncPermissions(userId));
    }

    @GetMapping
    @RequiresPermissions("permission:search")
    public ResponseEntity<List<Permission>> getPermissions() {
        return ResponseEntity.ok(permissionService.getPermissions());
    }

}
