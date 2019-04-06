package com.gavin.cloud.sys.core.service;

import com.gavin.cloud.common.base.page.Page;
import com.gavin.cloud.sys.core.enums.LoginType;
import com.gavin.cloud.sys.pojo.User;

import java.util.List;
import java.util.Map;

public interface UserService {

    User createUser(User user);

    User updateUser(Long id, User user);

    void deleteUser(Long id);

    void deleteUsers(Long[] ids);

    User getUser(Long id);

    User getUser(String account, LoginType loginType);

    List<User> getUsers(Map<String, Object> param);

    Page<User> getUsers(Map<String, Object> param, int page, int pageSize);

}
