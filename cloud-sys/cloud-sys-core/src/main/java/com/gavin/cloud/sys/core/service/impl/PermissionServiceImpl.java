package com.gavin.cloud.sys.core.service.impl;

import com.gavin.cloud.common.base.problem.AppBizException;
import com.gavin.cloud.common.base.util.JsonUtils;
import com.gavin.cloud.common.base.util.SnowflakeIdWorker;
import com.gavin.cloud.sys.core.enums.RedisKey;
import com.gavin.cloud.sys.core.enums.ResourceType;
import com.gavin.cloud.sys.core.mapper.ext.PermissionExtMapper;
import com.gavin.cloud.sys.core.mapper.ext.RoleExtMapper;
import com.gavin.cloud.sys.core.mapper.ext.RolePermissionExtMapper;
import com.gavin.cloud.sys.core.service.PermissionService;
import com.gavin.cloud.sys.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.gavin.cloud.sys.core.enums.SysProblemType.ROLE_NOT_FOUND_TYPE;

@Slf4j
@Service
public class PermissionServiceImpl implements PermissionService {

    private static final long DEFAULT_TIMEOUT = TimeUnit.SECONDS.toMillis(60);
    private static final long DEFAULT_SLEEP_TIME = 500L;

    private final RoleExtMapper roleExtMapper;
    private final PermissionExtMapper permissionExtMapper;
    private final RolePermissionExtMapper rolePermissionExtMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public PermissionServiceImpl(RoleExtMapper roleExtMapper,
                                 PermissionExtMapper permissionExtMapper,
                                 RolePermissionExtMapper rolePermissionExtMapper,
                                 ObjectProvider<StringRedisTemplate> redisTemplateProvider) {
        this.roleExtMapper = roleExtMapper;
        this.permissionExtMapper = permissionExtMapper;
        this.rolePermissionExtMapper = rolePermissionExtMapper;
        this.stringRedisTemplate = redisTemplateProvider.getIfAvailable();
    }

    @Override
    @Transactional
    public Permission createPermission(Permission permission) {
        permission.setId(SnowflakeIdWorker.getInstance().nextId());
        permissionExtMapper.insertSelective(permission);
        return permission;
    }

    @Override
    @Transactional
    public Permission updatePermission(Long id, Permission permission) {
        permission.setId(id);
        permissionExtMapper.updateByPrimaryKey(permission);
        return permission;
    }

    @Override
    @Transactional
    public void deletePermission(Long id) {
        RolePermissionExample example = new RolePermissionExample();
        example.createCriteria().andPermissionIdEqualTo(id);
        rolePermissionExtMapper.deleteByExample(example);
        permissionExtMapper.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional
    public void deletePermissions(Long[] ids) {
        RolePermissionExample rolePermExample = new RolePermissionExample();
        rolePermExample.createCriteria().andPermissionIdIn(Arrays.asList(ids));
        rolePermissionExtMapper.deleteByExample(rolePermExample);

        PermissionExample example = new PermissionExample();
        example.createCriteria().andIdIn(Arrays.asList(ids));
        permissionExtMapper.deleteByExample(example);
    }

    @Override
    public Permission getPermission(Long id) {
        return permissionExtMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Permission> getPermissions() {
        return permissionExtMapper.selectByExample(new PermissionExample());
    }

    @Override
    public List<Permission> getPermissions(Long userId, ResourceType type) {
        return permissionExtMapper.getPermsByUid(userId, type);
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, Long[] permIds) {
        Role role = Optional.ofNullable(roleExtMapper.selectByPrimaryKey(roleId))
                .orElseThrow(() -> new AppBizException(ROLE_NOT_FOUND_TYPE));
        RolePermissionExample example = new RolePermissionExample();
        example.createCriteria().andRoleIdEqualTo(roleId);
        rolePermissionExtMapper.deleteByExample(example);
        if (Array.getLength(permIds) > 0) {
            rolePermissionExtMapper.insertBatch(Arrays.stream(permIds).map(permId -> {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setId(SnowflakeIdWorker.getInstance().nextId());
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permId);
                return rolePermission;
            }).collect(Collectors.toList()));
        }
        // 缓存同步
        delRolePermsCache(role.getCode());
    }

    @Override
    public List<Permission> getPermissions(String role) {
        // 先从缓存中获取, 若缓存不存在则从DB获取
        List<Permission> permList = getRolePermsFromCache(role);
        if (!permList.isEmpty()) {
            return permList;
        }
        // 获取互斥锁
        if (getRoleMutexLock(role)) {
            try {
                // 从DB中查询数据
                List<Permission> perms = permissionExtMapper.getPermsByRole(role);
                // 将数据回写到缓存中
                setRolePermsToCache(role, perms);
                return perms;
            } finally {
                // 删除互斥锁
                delRoleMutexLock(role);
            }
        }
        // 休眠500毫秒再重试
        sleep(DEFAULT_SLEEP_TIME);
        return getPermissions(role);
    }

    @Override
    public List<Permission> getPermissions(Set<String> roles) {
        return roles.parallelStream()
                .map(this::getPermissions)
                .collect(Collectors.toList())
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public Set<String> getPermissionCodes(String role) {
        // 先从缓存中获取, 若缓存不存在则从DB获取
        Set<String> permCodes = getRolePermCodesFromCache(role);
        if (!permCodes.isEmpty()) {
            return permCodes;
        }
        // 获取互斥锁
        if (getRoleMutexLock(role)) {
            try {
                // 从DB中查询数据
                List<Permission> perms = permissionExtMapper.getPermsByRole(role);
                // 将数据回写到缓存中
                setRolePermsToCache(role, perms);
                return perms.stream().map(Permission::getCode).collect(Collectors.toSet());
            } finally {
                // 删除互斥锁
                delRoleMutexLock(role);
            }
        }
        // 休眠500毫秒再重试
        sleep(DEFAULT_SLEEP_TIME);
        return getPermissionCodes(role);
    }

    @Override
    public Set<String> getPermissionCodes(Set<String> roles) {
        return roles.parallelStream()
                .map(this::getPermissionCodes)
                .collect(Collectors.toSet())
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    /**
     * 缓存的添加不能影响正常业务逻辑
     * <pre>{@code
     *   applicationEventPublisher.publishEvent(new PublishEvent(role.getCode()));
     *
     *   @TransactionalEventListener
     *   public void onPublish(PublishEvent event) {
     *       // Do something
     *   }
     * }</pre>
     */
    private void delRolePermsCache(String role) {
        try {
            stringRedisTemplate.delete(RedisKey.ROLE_PERMS.getKey(role));
        } catch (Exception e) {
            log.warn("缓存同步失败", e);
        }
    }

    private List<Permission> getRolePermsFromCache(String role) {
        try {
            HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
            List<String> perms = opsForHash.values(RedisKey.ROLE_PERMS.getKey(role));
            if (!CollectionUtils.isEmpty(perms)) {
                return JsonUtils.fromJson(perms.toString(), List.class, Permission.class);
            }
        } catch (Exception e) {
            log.warn("从缓存中获取数据失败", e);
        }
        return Collections.emptyList();
    }

    private Set<String> getRolePermCodesFromCache(String role) {
        try {
            HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
            return opsForHash.keys(RedisKey.ROLE_PERMS.getKey(role));
        } catch (Exception e) {
            log.warn("从缓存中获取数据失败", e);
        }
        return Collections.emptySet();
    }

    private void setRolePermsToCache(String role, List<Permission> perms) {
        try {
            stringRedisTemplate.opsForHash().putAll(RedisKey.ROLE_PERMS.getKey(role),
                    perms.stream().collect(Collectors.toMap(Permission::getCode, JsonUtils::toJson)));
        } catch (Exception e) {
            log.warn("向缓存中添加数据失败", e);
        }
    }

    private boolean getRoleMutexLock(String role) {
        try {
            String mutexKey = RedisKey.ROLE_MUTEX.getKey(role);
            long nextTimeMillis = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(DEFAULT_TIMEOUT);
            ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
            boolean flag = Boolean.TRUE.equals(opsForValue.setIfAbsent(mutexKey, Long.toString(nextTimeMillis)));
            if (flag) {
                stringRedisTemplate.expire(mutexKey, DEFAULT_TIMEOUT + (int) (Math.random() * 1000), TimeUnit.MILLISECONDS);
            }
            return flag;
        } catch (Exception e) {
            log.error("获取互斥锁失败", e);
            return false;
        }
    }

    private void delRoleMutexLock(String role) {
        try {
            stringRedisTemplate.delete(RedisKey.ROLE_MUTEX.getKey(role));
        } catch (Exception e) {
            log.error("删除互斥锁失败", e);
        }
    }

    private void sleep(long timeout) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch (InterruptedException ignored) {
        }
    }

}
