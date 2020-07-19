package com.gavin.cloud.auth.dto;

import com.gavin.cloud.common.base.util.Constants;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
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

}
