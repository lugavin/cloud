package com.gavin.cloud.sys.web;

import com.gavin.cloud.common.base.page.Page;
import com.gavin.cloud.common.base.util.Constants;
import com.gavin.cloud.common.web.annotation.RequiresPermissions;
import com.gavin.cloud.common.web.util.HeaderUtils;
import com.gavin.cloud.sys.core.enums.LoginType;
import com.gavin.cloud.sys.core.service.UserService;
import com.gavin.cloud.sys.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserResource {

    @Autowired
    private UserService userService;

    @PostMapping
    @RequiresPermissions("user:create")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        return ResponseEntity.created(URI.create("/users/" + user.getUsername()))
                .headers(HeaderUtils.createAlert("userManagement.created", user.getUsername()))
                .body(userService.createUser(user));
    }

    @PutMapping
    @RequiresPermissions("user:update")
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        return ResponseEntity.ok()
                .headers(HeaderUtils.createAlert("userManagement.updated", user.getUsername()))
                .body(userService.updateUser(user));
    }

    @DeleteMapping("/{id}")
    @RequiresPermissions("user:delete")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok()
                .headers(HeaderUtils.createAlert("userManagement.deleted", id))
                .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping("/{account:" + Constants.REGEX_LOGIN_NAME + "}/{type:" + Constants.REGEX_LOGIN_TYPE + "}")
    public ResponseEntity<User> getUser(@PathVariable String account, @PathVariable int type) {
        return ResponseEntity.ok(userService.getUser(account, LoginType.fromType(type)));
    }

    @GetMapping
    @RequiresPermissions("user:search")
    public ResponseEntity<Page<User>> getUsers(@RequestParam Map<String, Object> param,
                                               @RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer rows) {
        return ResponseEntity.ok(userService.getUsers(param, page, rows));
    }

}
