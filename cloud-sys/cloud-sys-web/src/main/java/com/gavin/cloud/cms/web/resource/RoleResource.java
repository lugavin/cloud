package com.gavin.cloud.cms.web.resource;

import com.gavin.cloud.sys.api.model.Role;
import com.gavin.cloud.sys.core.service.RoleService;
import com.gavin.cloud.common.base.page.Page;
import com.gavin.cloud.common.web.auth.RequiresPermissions;
import com.gavin.cloud.common.web.util.HeaderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/roles")
public class RoleResource {

    @Autowired
    private RoleService roleService;

    @PostMapping
    @RequiresPermissions("role:create")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role role) {
        return ResponseEntity.created(URI.create("/roles/" + role.getCode()))
                .headers(HeaderUtils.createAlert("roleManagement.created", role.getCode()))
                .body(roleService.createRole(role));
    }

    @GetMapping("/{userId}/codes")
    public ResponseEntity<List<String>> getRoles(@PathVariable String userId) {
        return ResponseEntity.ok(roleService.getRoles(userId).stream()
                .map(Role::getCode).collect(Collectors.toList()));
    }

    @GetMapping
    @RequiresPermissions("role:search")
    public ResponseEntity<Page<Role>> getRoles(@RequestParam Map<String, Object> param,
                                               @RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer rows) {
        return ResponseEntity.ok(roleService.getRoles(param, page, rows));
    }

}
