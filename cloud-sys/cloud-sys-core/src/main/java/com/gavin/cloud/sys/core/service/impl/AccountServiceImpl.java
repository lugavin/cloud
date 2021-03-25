package com.gavin.cloud.sys.core.service.impl;

import com.gavin.cloud.common.base.problem.AppBizException;
import com.gavin.cloud.common.base.util.Md5Hash;
import com.gavin.cloud.common.base.util.NanoIdUtils;
import com.gavin.cloud.sys.core.dao.ext.UserExtDao;
import com.gavin.cloud.sys.core.service.AccountService;
import com.gavin.cloud.sys.core.service.UserService;
import com.gavin.cloud.sys.pojo.User;
import com.gavin.cloud.sys.pojo.UserExample;
import com.gavin.cloud.sys.pojo.dto.RegisterDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.gavin.cloud.sys.core.enums.SysProblemType.EMAIL_NOT_FOUND_TYPE;
import static com.gavin.cloud.sys.core.enums.SysProblemType.USER_NOT_FOUND_TYPE;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final UserExtDao userExtMapper;
    private final UserService userService;

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
        List<User> users = userExtMapper.selectByExample(example);
        if (users.size() < 1) {
            throw new AppBizException(USER_NOT_FOUND_TYPE);
        }
        User user = users.get(0);
        user.setActivated(true);
        user.setActivationKey(null);
        userExtMapper.updateByPrimaryKey(user);
        return user;
    }

    @Override
    public User requestPasswordReset(String mail) {
        UserExample example = new UserExample();
        example.createCriteria().andEmailEqualTo(mail);
        List<User> users = userExtMapper.selectByExample(example);
        if (users.size() < 1) {
            throw new AppBizException(EMAIL_NOT_FOUND_TYPE);
        }
        User user = users.get(0);
        user.setResetKey(NanoIdUtils.randomNanoId());
        user.setResetDate(Date.from(Instant.now()));
        userExtMapper.updateByPrimaryKey(user);
        return user;
    }

    @Override
    public User finishPasswordReset(String key, String newPassword) {
        UserExample example = new UserExample();
        example.createCriteria().andActivationKeyEqualTo(key);
        List<User> users = userExtMapper.selectByExample(example);
        if (users.size() < 1) {
            throw new AppBizException(USER_NOT_FOUND_TYPE);
        }
        User user = users.get(0);
        user.setSalt(NanoIdUtils.randomNanoId());
        user.setPassword(Md5Hash.hash(user.getPassword(), user.getSalt()));
        user.setResetKey(null);
        user.setResetDate(null);
        userExtMapper.updateByPrimaryKey(user);
        return user;
    }

    @Override
    public void changePassword(Long id, String password) {
        User user = Optional.ofNullable(userExtMapper.selectByPrimaryKey(id)).orElseThrow(() -> new AppBizException(USER_NOT_FOUND_TYPE));
        user.setSalt(NanoIdUtils.randomNanoId());
        user.setPassword(Md5Hash.hash(user.getPassword(), user.getSalt()));
        user.setUpdatedBy(user.getUsername());
        user.setUpdatedAt(Date.from(Instant.now()));
        userExtMapper.updateByPrimaryKey(user);
    }

}
