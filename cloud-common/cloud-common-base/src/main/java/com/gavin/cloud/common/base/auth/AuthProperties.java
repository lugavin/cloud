package com.gavin.cloud.common.base.auth;

import lombok.Data;

import java.util.List;

@Data
public class AuthProperties {

    private List<String> anonUrls;   // 匿名访问地址(不用登录即可访问)
    private List<String> userUrls;   // 公共访问地址(登录后就可访问)
    private List<String> authcUrls;  // 授权访问地址(需要授权方可访问)

}
