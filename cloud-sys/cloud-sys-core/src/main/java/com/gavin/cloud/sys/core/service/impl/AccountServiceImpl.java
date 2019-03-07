package com.gavin.cloud.sys.core.service.impl;

import com.gavin.cloud.sys.api.dto.RegisterDTO;
import com.gavin.cloud.sys.api.model.User;
import com.gavin.cloud.sys.api.model.UserExample;
import com.gavin.cloud.sys.core.mapper.UserMapper;
import com.gavin.cloud.sys.core.service.AccountService;
import com.gavin.cloud.sys.core.service.UserService;
import com.gavin.cloud.common.base.exception.AppException;
import com.gavin.cloud.common.base.exception.CommonMessageType;
import com.gavin.cloud.common.base.util.Md5Hash;
import com.gavin.cloud.common.base.util.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Override
    public User register(RegisterDTO registerDTO) {
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword());
        return userService.createUser(user);
    }

    @Override
    public User activateRegistration(String key) {
        UserExample example = new UserExample();
        example.createCriteria().andActivationKeyEqualTo(key);
        List<User> users = userMapper.selectByExample(example);
        if (users.size() < 1) {
            throw new AppException(CommonMessageType.ERR_BUSINESS, "No user was found for this reset key");
        }
        User user = users.get(0);
        user.setActivated(true);
        user.setActivationKey(null);
        userMapper.updateByPrimaryKey(user);
        return user;
    }

    @Override
    public User requestPasswordReset(String mail) {
        UserExample example = new UserExample();
        example.createCriteria().andEmailEqualTo(mail);
        List<User> users = userMapper.selectByExample(example);
        if (users.size() < 1) {
            throw new AppException(CommonMessageType.ERR_BUSINESS, "Email address not registered");
        }
        User user = users.get(0);
        user.setResetKey(RandomUtils.randomNumeric());
        user.setResetDate(Calendar.getInstance().getTime());
        userMapper.updateByPrimaryKey(user);
        return user;
    }

    @Override
    public User finishPasswordReset(String key, String newPassword) {
        UserExample example = new UserExample();
        example.createCriteria().andActivationKeyEqualTo(key);
        List<User> users = userMapper.selectByExample(example);
        if (users.size() < 1) {
            throw new AppException(CommonMessageType.ERR_BUSINESS, "No user was found for this reset key");
        }
        User user = users.get(0);
        user.setSalt(RandomUtils.randomAlphanumeric());
        user.setPassword(Md5Hash.hash(user.getPassword(), user.getSalt()));
        user.setResetKey(null);
        user.setResetDate(null);
        userMapper.updateByPrimaryKey(user);
        return user;
    }

    @Override
    public void changePassword(String id, String password) {
        User user = userMapper.selectByPrimaryKey(id);
        if (user == null) {
            throw new AppException(CommonMessageType.ERR_BUSINESS, "No user was found for this id");
        }
        user.setSalt(RandomUtils.randomAlphanumeric());
        user.setPassword(Md5Hash.hash(user.getPassword(), user.getSalt()));
        user.setLastModifiedBy(user.getUsername());
        user.setLastModifiedDate(Calendar.getInstance().getTime());
        userMapper.updateByPrimaryKey(user);
    }

}
