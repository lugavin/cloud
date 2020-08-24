package com.gavin.cloud.sys.core.service.impl;

import com.gavin.cloud.common.base.problem.AppAlertException;
import com.gavin.cloud.common.base.page.Page;
import com.gavin.cloud.common.base.util.Constants;
import com.gavin.cloud.common.base.util.Md5Hash;
import com.gavin.cloud.common.base.util.NanoIdUtils;
import com.gavin.cloud.common.base.util.SnowflakeIdWorker;
import com.gavin.cloud.sys.core.enums.LoginType;
import com.gavin.cloud.sys.core.mapper.ext.UserExtMapper;
import com.gavin.cloud.sys.core.service.UserService;
import com.gavin.cloud.sys.pojo.User;
import com.gavin.cloud.sys.pojo.UserExample;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;

import static com.gavin.cloud.sys.core.enums.SysAlertType.*;
import static java.time.temporal.ChronoUnit.DAYS;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserExtMapper userExtMapper;

    public UserServiceImpl(UserExtMapper userExtMapper) {
        this.userExtMapper = userExtMapper;
    }

    @Override
    public User createUser(User user) {
        if (isAlreadyUsed(user.getUsername(), LoginType.USERNAME)) {
            throw new AppAlertException(LOGIN_ALREADY_USED_TYPE);
        }
        if (isAlreadyUsed(user.getEmail(), LoginType.EMAIL)) {
            throw new AppAlertException(EMAIL_ALREADY_USED_TYPE);
        }
        if (StringUtils.hasText(user.getPhone()) && isAlreadyUsed(user.getPhone(), LoginType.PHONE)) {
            throw new AppAlertException(PHONE_ALREADY_USED_TYPE);
        }
        if (!StringUtils.hasText(user.getLangKey())) {
            user.setLangKey(Locale.getDefault().getLanguage());
        }
        user.setId(SnowflakeIdWorker.getInstance().nextId());
        user.setSalt(NanoIdUtils.randomNanoId());
        user.setPassword(Md5Hash.hash(user.getPassword(), user.getSalt()));
        user.setActivated(Boolean.FALSE);
        user.setActivationKey(NanoIdUtils.randomNanoId());
        user.setCreatedBy(Constants.ACCOUNT_SYSTEM);
        user.setCreatedAt(Calendar.getInstance().getTime());
        userExtMapper.insert(user);
        return user;
    }

    @Override
    public User updateUser(Long id, User user) {
        user.setId(id);
        userExtMapper.updateByPrimaryKeySelective(user);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        userExtMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void deleteUsers(Long[] ids) {
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(Arrays.asList(ids));
        userExtMapper.deleteByExample(example);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void deleteNotActivatedUsers() {
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andActivatedNotEqualTo(Boolean.TRUE);
        criteria.andCreatedAtNotBetween(Date.from(Instant.now().minus(3, DAYS)), Date.from(Instant.now()));
        userExtMapper.deleteByExample(example);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(Long id) {
        return userExtMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(String account, LoginType loginType) {
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        if (loginType == LoginType.USERNAME) {
            criteria.andUsernameEqualTo(account);
        } else if (loginType == LoginType.PHONE) {
            criteria.andPhoneEqualTo(account);
        } else {
            criteria.andEmailEqualTo(account);
        }
        List<User> list = userExtMapper.selectByExample(example);
        return !list.isEmpty() ? list.get(0) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers(Map<String, Object> param) {
        return userExtMapper.getList(param);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> getUsers(Map<String, Object> param, int page, int pageSize) {
        return userExtMapper.getPage(param, page, pageSize);
    }

    /**
     * Check if the account is already used
     *
     * @param login User account
     * @param type  {1:USERNAME, 2:PHONE, 3:EMAIL}
     * @return If used return true else false
     */
    private boolean isAlreadyUsed(String login, LoginType type) {
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        if (type == LoginType.USERNAME) {
            criteria.andUsernameEqualTo(login);
        } else if (type == LoginType.PHONE) {
            criteria.andPhoneEqualTo(login);
        } else {
            criteria.andEmailEqualTo(login);
        }
        return userExtMapper.countByExample(example) > 0;
    }

}
