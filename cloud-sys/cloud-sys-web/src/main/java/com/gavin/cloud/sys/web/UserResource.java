package com.gavin.cloud.sys.web;

import com.gavin.cloud.common.base.page.Page;
import com.gavin.cloud.common.web.annotation.RequiresPermissions;
import com.gavin.cloud.common.web.util.HeaderUtils;
import com.gavin.cloud.sys.core.enums.LoginType;
import com.gavin.cloud.sys.core.service.UserService;
import com.gavin.cloud.sys.pojo.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Map;

import static com.gavin.cloud.common.base.util.Constants.REGEX_LOGIN_NAME;
import static com.gavin.cloud.common.base.util.Constants.REGEX_LOGIN_TYPE;

@RestController
@RequestMapping("/users")
public class UserResource {

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @RequiresPermissions("user:create")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        return ResponseEntity.created(URI.create("/users/" + user.getUsername()))
                .headers(HeaderUtils.createAlert("userManagement.created", user.getUsername()))
                .body(userService.createUser(user));
    }

    @PutMapping("/{id}")
    @RequiresPermissions("user:update")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        return ResponseEntity.ok()
                .headers(HeaderUtils.createAlert("userManagement.updated", user.getUsername()))
                .body(userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    @RequiresPermissions("user:delete")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok()
                .headers(HeaderUtils.createAlert("userManagement.deleted", Long.toString(id)))
                .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping("/{account:" + REGEX_LOGIN_NAME + "}/{type:" + REGEX_LOGIN_TYPE + "}")
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
