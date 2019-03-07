package com.gavin.cloud.auth.dto;

import com.gavin.cloud.common.base.util.Constants;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class LoginDTO implements Serializable {

    private static final long serialVersionUID = 20180331L;

    @NotBlank
    @Size(min = 4, max = 50)
    @Pattern(regexp = Constants.REGEX_LOGIN_NAME)
    private String username;

    @NotBlank
    @Size(min = 4, max = 100)
    private String password;

    private Boolean rememberMe;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}
