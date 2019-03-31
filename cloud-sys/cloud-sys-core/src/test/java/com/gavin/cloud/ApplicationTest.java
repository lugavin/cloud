package com.gavin.cloud;

import com.gavin.cloud.common.base.page.Page;
import com.gavin.cloud.common.base.util.Constants;
import com.gavin.cloud.common.base.util.JsonUtils;
import com.gavin.cloud.sys.core.mapper.ext.PermissionExtMapper;
import com.gavin.cloud.sys.core.mapper.ext.RoleExtMapper;
import com.gavin.cloud.sys.pojo.User;
import com.gavin.cloud.sys.pojo.UserExample;
import com.gavin.cloud.sys.core.mapper.UserMapper;
import com.gavin.cloud.sys.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 当在Junit单元测试类中加了{@link SpringBootTest}注解时, 如果你的单元测试方法上加了{@link Transactional}注解,
 * 默认情况下它会在每个测试方法结束时回滚事务, 以防止测试数据对数据库造成污染, 如果想要测试数据不回滚, 可以加上{@link Rollback}注解并设置值为false.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(Constants.PROFILE_DEV)
public class ApplicationTest {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleExtMapper roleExtMapper;

    @Autowired
    private PermissionExtMapper permissionExtMapper;

    @Autowired
    private UserService userService;

    @Before
    public void setUp() throws Exception {
        Assert.assertNotNull(sqlSessionFactory);
    }

    /**
     * 一级缓存
     */
    @Test
    public void testPrimaryCache() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            log.info("{}", userMapper.selectByPrimaryKey("101")); // 发出SQL语句
            log.info("{}", userMapper.selectByPrimaryKey("101")); // 不发出SQL语句
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 二级缓存
     */
    @Test
    public void testSecondaryCache() {
        try (SqlSession sqlSession1 = sqlSessionFactory.openSession();
             SqlSession sqlSession2 = sqlSessionFactory.openSession()) {

            UserMapper userMapper1 = sqlSession1.getMapper(UserMapper.class);
            log.info("{}", userMapper1.selectByPrimaryKey("101")); // 发出SQL语句

            // sqlSession1.commit(); // 执行SELECT的commit操作会将SqlSession中的数据存入二级缓存区域

            // User user = new User();
            // user.setId("102");
            // user.setUsername("Alan");
            // user.setPassword("111111");
            // user.setActivated(Boolean.TRUE);
            // user.setCreatedBy("Admin");
            // user.setCreatedDate(new Date());
            // userMapper1.insert(user);
            sqlSession1.commit(); // 当执行了非SELECT语句时整个namespace中的缓存会被清空

            UserMapper userMapper2 = sqlSession2.getMapper(UserMapper.class);
            log.info("{}", userMapper2.selectByPrimaryKey("101")); // 不发出SQL语句

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetUsers() throws Exception {
        Page<User> users = userService.getUsers(Collections.singletonMap("username", "admin"), 1, 10);
        log.debug(JsonUtils.toJson(users));
    }

    @Test
    @Transactional(readOnly = true)
    public void testSelectByExample() throws Exception {
        Date sysTime = Calendar.getInstance().getTime();
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andActivatedNotEqualTo(Boolean.TRUE);
        criteria.andCreatedDateNotBetween(DateUtils.addDays(sysTime, -3), sysTime);
        List<User> users = userMapper.selectByExample(example);
        log.info(JsonUtils.toJson(users));
    }

    @Test
    @Transactional(readOnly = true)
    public void testGetRoles() throws Exception {
        roleExtMapper.getList(Collections.emptyMap())
                .forEach(r -> log.info("====== {} ======", JsonUtils.toJson(r)));
    }

    @Test
    @Transactional(readOnly = true)
    public void testGetPermission() throws Exception {
        Optional.ofNullable(permissionExtMapper.getById("11"))
                .ifPresent(r -> log.info("====== {} ======", JsonUtils.toJson(r)));
    }

    @Test
    public void testCollection() {
        List<String> newRoles = Arrays.asList("admin", "user");
        List<String> oldRoles = Arrays.asList("user", "guest");
        List<String> deleteRoles = new ArrayList<>(oldRoles);
        deleteRoles.removeAll(newRoles);
        log.info("=== deleteRoles:{} ===", deleteRoles);
        List<String> insertRoles = new ArrayList<>(newRoles);
        insertRoles.removeAll(oldRoles);
        log.info("=== insertRoles:{} ===", insertRoles);
    }

}
