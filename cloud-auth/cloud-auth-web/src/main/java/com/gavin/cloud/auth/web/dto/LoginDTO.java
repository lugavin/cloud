package com.gavin.cloud.auth.web.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

import static com.gavin.cloud.common.base.util.Constants.REGEX_LOGIN_NAME;

@Data
public class LoginDTO implements Serializable {

    private static final long serialVersionUID = 20180331L;

    @NotBlank
    @Size(min = 4, max = 50)
    @Pattern(regexp = REGEX_LOGIN_NAME)
    private String username;

    @NotBlank
    @Size(min = 4, max = 100)
    private String password;

    private Boolean rememberMe;

}
