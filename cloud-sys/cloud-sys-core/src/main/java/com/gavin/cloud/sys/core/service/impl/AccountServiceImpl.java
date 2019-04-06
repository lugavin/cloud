package com.gavin.cloud.sys.core.service.impl;

import com.gavin.cloud.common.base.util.Md5Hash;
import com.gavin.cloud.common.base.util.RandomUtils;
import com.gavin.cloud.sys.core.mapper.UserMapper;
import com.gavin.cloud.sys.core.problem.EmailNotFoundException;
import com.gavin.cloud.sys.core.problem.UserNotFoundException;
import com.gavin.cloud.sys.core.service.AccountService;
import com.gavin.cloud.sys.core.service.UserService;
import com.gavin.cloud.sys.pojo.User;
import com.gavin.cloud.sys.pojo.UserExample;
import com.gavin.cloud.sys.pojo.dto.RegisterDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final UserMapper userMapper;
    private final UserService userService;

    public AccountServiceImpl(UserMapper userMapper,
                              UserService userService) {
        this.userMapper = userMapper;
        this.userService = userService;
    }

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
            throw new UserNotFoundException();
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
            throw new EmailNotFoundException();
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
            throw new UserNotFoundException();
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
    public void changePassword(Long id, String password) {
        User user = Optional.ofNullable(userMapper.selectByPrimaryKey(id))
                .orElseThrow(UserNotFoundException::new);
        user.setSalt(RandomUtils.randomAlphanumeric());
        user.setPassword(Md5Hash.hash(user.getPassword(), user.getSalt()));
        user.setUpdatedBy(user.getUsername());
        user.setUpdatedAt(Calendar.getInstance().getTime());
        userMapper.updateByPrimaryKey(user);
    }

}
