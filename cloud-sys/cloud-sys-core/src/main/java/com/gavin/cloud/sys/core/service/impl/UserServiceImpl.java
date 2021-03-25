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
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserExtMapper userExtMapper;

    @Override
    public User createUser(User user) {
        if (Objects.nonNull(userExtMapper.getByLogin(user.getUsername(), LoginType.USERNAME))) {
            throw new AppAlertException(LOGIN_ALREADY_USED_TYPE);
        }
        if (Objects.nonNull(userExtMapper.getByLogin(user.getEmail(), LoginType.EMAIL))) {
            throw new AppAlertException(EMAIL_ALREADY_USED_TYPE);
        }
        if (StringUtils.hasText(user.getPhone()) && Objects.nonNull(userExtMapper.getByLogin(user.getPhone(), LoginType.PHONE))) {
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
        example.createCriteria().andIdIn(Arrays.asList(ids));
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
        example.createCriteria().andActivatedNotEqualTo(Boolean.TRUE)
                .andCreatedAtNotBetween(Date.from(Instant.now().minus(3, DAYS)), Date.from(Instant.now()));
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
        return userExtMapper.getByLogin(account, loginType);
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

}
