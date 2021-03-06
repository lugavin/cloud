package com.gavin.cloud.common.core;

import com.gavin.cloud.common.base.util.SnowflakeIdWorker;
import com.gavin.cloud.common.core.dto.PrcDTO;
import com.gavin.cloud.common.core.mapper.CommentMapper;
import com.gavin.cloud.common.core.mapper.CounterMapper;
import com.gavin.cloud.common.core.model.Comment;
import com.gavin.cloud.common.core.model.Counter;
import com.gavin.cloud.common.core.transaction.ServiceA;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
public class ApplicationTest {

    private static final String PRC_RET_CODE_OK = "000000";

    @Autowired
    private ServiceA serviceA;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CounterMapper counterMapper;

    @BeforeEach
    public void setUp() {
        assertNotNull(serviceA);
        assertNotNull(commentMapper);
        assertNotNull(counterMapper);
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
        int row = commentMapper.insert(record);
        assertEquals(1, row);
    }

    @Test
    public void testSelectProvider() {
        Optional.ofNullable(commentMapper.selectByPrimaryKey(101L))
                .ifPresent(r -> log.debug("====== {} ======", r));
    }

    @Test
    public void testSharding() {
        commentMapper.selectAll()
                .forEach(r -> log.debug("====== {} ======", r));
    }

    @Test
    public void testPartition() {
        Counter record = new Counter();
        record.setId(SnowflakeIdWorker.getInstance().nextId());
        record.setCreatedAt(Calendar.getInstance().getTime());
        record.setCreatedBy("admin");
        int row = counterMapper.insert(record);
        assertEquals(1, row);
    }

    @Test
    public void testCallPrc() {
        PrcDTO prcDTO = new PrcDTO();
        prcDTO.setBizTable("counter");
        prcDTO.setHisTable("counter_his");
        prcDTO.setTmpTable("counter_tmp");
        prcDTO.setRemDays(7);
        counterMapper.callPrc(prcDTO);
        if (!PRC_RET_CODE_OK.equals(prcDTO.getRetCode())) {
            throw new RuntimeException(prcDTO.getRetMsg());
        }
    }

}
