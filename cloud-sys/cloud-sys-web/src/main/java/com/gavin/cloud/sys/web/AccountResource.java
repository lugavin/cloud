package com.gavin.cloud.sys.web;

import com.gavin.cloud.common.web.annotation.RequiresGuest;
import com.gavin.cloud.sys.core.service.AccountService;
import com.gavin.cloud.sys.pojo.User;
import com.gavin.cloud.sys.pojo.dto.RegisterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/account")
public class AccountResource {

    @Autowired
    private AccountService accountService;

    @RequiresGuest
    @PostMapping("/register")
    public ResponseEntity<User> registerAccount(@Valid @RequestBody RegisterDTO registerDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.register(registerDTO));
    }

    @RequiresGuest
    @GetMapping("/activate")
    public ResponseEntity<User> activateRegistration(@RequestParam("key") String key) {
        return ResponseEntity.ok(accountService.activateRegistration(key));
    }

    @RequiresGuest
    @GetMapping("/reset-password/init")
    public ResponseEntity<User> requestPasswordReset(@RequestParam("mail") String mail) {
        return ResponseEntity.ok(accountService.requestPasswordReset(mail));
    }

    @PostMapping("/reset-password/finish/{key}")
    public ResponseEntity<User> finishPasswordReset(@PathVariable String key, @RequestBody String newPassword) {
        return ResponseEntity.ok(accountService.finishPasswordReset(key, newPassword));
    }

    @PostMapping("/change-password/{id}")
    public void changePassword(@PathVariable Long id, @RequestBody String password) {
        accountService.changePassword(id, password);
    }

}
