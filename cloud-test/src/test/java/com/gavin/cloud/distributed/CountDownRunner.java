package com.gavin.cloud.distributed;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RFuture;
import org.redisson.client.codec.StringCodec;
import org.redisson.client.protocol.RedisCommands;
import org.redisson.command.CommandExecutor;

import java.util.Collections;

@Slf4j
public class CountDownRunner {

    private static final String LUA_SCRIPT_INIT =
            "local currValue = redis.call('get', KEYS[1]); "
                    + "if (tonumber(ARGV[1]) == 0 and currValue == false) then "
                    + "redis.call('set', KEYS[1], ARGV[2], 'ex', ARGV[3]); "
                    + "return 1 "
                    + "else "
                    + "return 0 "
                    + "end";

    private static final String LUA_SCRIPT_COUNT_DOWN =
            "local currValue = redis.call('decr', KEYS[1]); "
                    + "if currValue <= 0 then "
                    + "redis.call('del', KEYS[1]); "
                    + "end "
                    + "return currValue; ";

    private final CommandExecutor commandExecutor;

    public CountDownRunner(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    /**
     * @see org.redisson.RedissonAtomicLong#compareAndSet
     */
    public void initCounter(String type, long count, long expireSeconds) {
        RFuture<Boolean> future = commandExecutor.evalWriteAsync(type, StringCodec.INSTANCE, RedisCommands.EVAL_BOOLEAN, LUA_SCRIPT_INIT,
                Collections.singletonList(type), 0, count, expireSeconds);
        if (commandExecutor.get(future)) {
            log.info("计数器初始化成功, 计数器类型: {}, 计数值: {}", type, count);
        } else {
            log.warn("计数器已初始化, 计数器类型: {}, 计数值: {}", type, count);
        }
    }

    public void countDown(String type, Runnable runnable) {
        RFuture<Long> future = commandExecutor.evalWriteAsync(type, StringCodec.INSTANCE, RedisCommands.EVAL_LONG, LUA_SCRIPT_COUNT_DOWN,
                Collections.singletonList(type));
        if (commandExecutor.get(future) == 0) {
            runnable.run();
        }
    }

}
