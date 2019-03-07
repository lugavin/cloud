package com.gavin.cloud.sys.core.service;

import com.gavin.cloud.sys.api.enums.LoginType;
import com.gavin.cloud.sys.api.model.User;
import com.gavin.cloud.common.base.page.Page;

import java.util.List;
import java.util.Map;

public interface UserService {

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(String id);

    void deleteUsers(String[] ids);

    User getUser(String id);

    User getUser(String account, LoginType loginType);

    List<User> getUsers(Map<String, Object> param);

    Page<User> getUsers(Map<String, Object> param, int page, int pageSize);

}
