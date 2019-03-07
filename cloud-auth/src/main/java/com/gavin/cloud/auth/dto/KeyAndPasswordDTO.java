package com.gavin.cloud.auth.dto;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

public class KeyAndPasswordDTO {

    @NotBlank
    @Size(min = 19, max = 20)
    private String key;

    @NotBlank
    @Size(min = 4, max = 100)
    private String newPassword;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
