package com.gavin.cloud.sys.core.service.impl;

import com.gavin.cloud.common.base.page.Page;
import com.gavin.cloud.common.base.page.PageRequest;
import com.gavin.cloud.common.base.util.Constants;
import com.gavin.cloud.common.base.util.Md5Hash;
import com.gavin.cloud.common.base.util.RandomUtils;
import com.gavin.cloud.common.base.util.SnowflakeIdWorker;
import com.gavin.cloud.sys.core.enums.LoginType;
import com.gavin.cloud.sys.core.mapper.UserMapper;
import com.gavin.cloud.sys.core.mapper.ext.UserExtMapper;
import com.gavin.cloud.sys.core.problem.EmailAlreadyUsedException;
import com.gavin.cloud.sys.core.problem.LoginAlreadyUsedException;
import com.gavin.cloud.sys.core.problem.PhoneAlreadyUsedException;
import com.gavin.cloud.sys.core.service.UserService;
import com.gavin.cloud.sys.pojo.User;
import com.gavin.cloud.sys.pojo.UserExample;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserExtMapper userExtMapper;

    public UserServiceImpl(UserMapper userMapper,
                           UserExtMapper userExtMapper) {
        this.userMapper = userMapper;
        this.userExtMapper = userExtMapper;
    }

    @Override
    public User createUser(User user) {
        if (isAlreadyUsed(user.getUsername(), LoginType.USERNAME)) {
            throw new LoginAlreadyUsedException();
        }
        if (isAlreadyUsed(user.getEmail(), LoginType.EMAIL)) {
            throw new EmailAlreadyUsedException();
        }
        if (StringUtils.isNotBlank(user.getPhone()) && isAlreadyUsed(user.getPhone(), LoginType.PHONE)) {
            throw new PhoneAlreadyUsedException();
        }
        if (StringUtils.isBlank(user.getLangKey())) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE);
        }
        user.setId(SnowflakeIdWorker.getInstance().nextId());
        user.setSalt(RandomUtils.randomAlphanumeric());
        user.setPassword(Md5Hash.hash(user.getPassword(), user.getSalt()));
        user.setActivated(Boolean.FALSE);
        user.setActivationKey(RandomUtils.randomNumeric());
        user.setCreatedBy(Constants.ACCOUNT_SYSTEM);
        user.setCreatedAt(Calendar.getInstance().getTime());
        userMapper.insert(user);
        return user;
    }

    @Override
    public User updateUser(Long id, User user) {
        user.setId(id);
        userMapper.updateByPrimaryKeySelective(user);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        userMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void deleteUsers(Long[] ids) {
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(Arrays.asList(ids));
        userMapper.deleteByExample(example);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void deleteNotActivatedUsers() {
        Date sysTime = Calendar.getInstance().getTime();
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andActivatedNotEqualTo(Boolean.TRUE);
        criteria.andCreatedAtNotBetween(DateUtils.addDays(sysTime, -3), sysTime);
        userMapper.deleteByExample(example);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(Long id) {
        return userMapper.selectByPrimaryKey(id);
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
        List<User> list = userMapper.selectByExample(example);
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
        return userExtMapper.getPage(new PageRequest<>(param, page, pageSize));
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
        return userMapper.countByExample(example) > 0;
    }

}
