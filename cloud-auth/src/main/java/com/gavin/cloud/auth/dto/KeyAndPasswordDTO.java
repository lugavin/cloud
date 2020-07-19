package com.gavin.cloud.auth.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

@Data
public class KeyAndPasswordDTO {

    @NotBlank
    @Size(min = 19, max = 20)
    private String key;

    @NotBlank
    @Size(min = 4, max = 100)
    private String newPassword;

}
