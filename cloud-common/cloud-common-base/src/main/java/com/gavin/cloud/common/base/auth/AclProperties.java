package com.gavin.cloud.common.base.auth;

import lombok.Data;

import java.util.Set;

@Data
public class AclProperties {

    private Set<String> anonUrls;  // 匿名访问地址
    private Set<String> userUrls;  // 公共访问地址
    private Set<String> authUrls;  // 授权访问地址

}
