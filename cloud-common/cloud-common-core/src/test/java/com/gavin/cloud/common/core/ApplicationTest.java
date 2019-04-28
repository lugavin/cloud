package com.gavin.cloud.common.core;

import com.gavin.cloud.common.base.util.SnowflakeIdWorker;
import com.gavin.cloud.common.core.mapper.CommentMapper;
import com.gavin.cloud.common.core.mapper.CounterMapper;
import com.gavin.cloud.common.core.model.Comment;
import com.gavin.cloud.common.core.transaction.ServiceA;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 对于基于接口动态代理的 AOP 事务增强来说, 由于接口的方法是 public 的, 这就要求实现类的实现方法必须是 public 的
 * (不能是 protected, private 等), 同时不能使用 static 的修饰符. 所以, 可以实施接口动态代理的方法只能是使用 "public"
 * 或 "public final" 修饰符的方法, 其它方法不可能被动态代理, 相应的也就不能实施 AOP 增强, 也不能进行 Spring 事务增强了.
 * <p>
 * 基于 CGLib 字节码动态代理的方案是通过扩展被增强类, 动态创建子类的方式进行 AOP 增强植入的. 由于使用 final, static, private
 * 修饰符的方法都不能被子类覆盖, 相应的, 这些方法将不能被实施 AOP 增强. 所以, 必须特别注意这些修饰符的使用, 以免不小心成为事务管理的漏网之鱼.
 * <p>
 * | 动态代理策略        | 不能被事务增强的方法
 * |---------------------------------------------------------------------------
 * | 基于接口的动态代理   | 除 public 外的其它所有的方法, 此外 public static 也不能被增强
 * |---------------------------------------------------------------------------
 * | 基于CGLib的动态代理 | private, static, final 的方法
 *
 * @see <a href="https://www.ibm.com/developerworks/cn/java/j-lo-spring-ts1/">Spring Transaction Manager</a>
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ApplicationTest {

    @Autowired
    private ServiceA serviceA;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CounterMapper counterMapper;

    @Before
    public void setUp() {
        Assert.assertNotNull(serviceA);
        Assert.assertNotNull(commentMapper);
        Assert.assertNotNull(counterMapper);
    }

    @Test
    public void testTransaction() {
        serviceA.execute();
    }

    @Test
    public void testInsertProvider() {
        Comment record = new Comment();
        record.setId(SnowflakeIdWorker.getInstance().nextId());
        record.setCreatedBy("admin");
        log.debug("====== {} ======", commentMapper.insert(record));
    }

    @Test
    public void testSelectProvider() {
        log.debug("====== {} ======", commentMapper.selectByPrimaryKey(101L));
    }

    @Test
    public void testSelect() {
        log.debug("====== {} ======", counterMapper.selectByPrimaryKey(101L));
    }

}
