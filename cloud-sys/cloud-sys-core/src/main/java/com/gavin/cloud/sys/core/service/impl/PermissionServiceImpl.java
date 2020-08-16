package com.gavin.cloud.sys.core.service.impl;

import com.gavin.cloud.common.base.util.JsonUtils;
import com.gavin.cloud.common.base.util.SnowflakeIdWorker;
import com.gavin.cloud.sys.core.enums.ResourceType;
import com.gavin.cloud.sys.core.mapper.ext.PermissionExtMapper;
import com.gavin.cloud.sys.core.mapper.ext.RoleExtMapper;
import com.gavin.cloud.sys.core.mapper.ext.RolePermissionExtMapper;
import com.gavin.cloud.sys.core.service.PermissionService;
import com.gavin.cloud.sys.pojo.Permission;
import com.gavin.cloud.sys.pojo.PermissionExample;
import com.gavin.cloud.sys.pojo.RolePermission;
import com.gavin.cloud.sys.pojo.RolePermissionExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PermissionServiceImpl implements PermissionService {

    private static final long DEFAULT_TIMEOUT = TimeUnit.MINUTES.toSeconds(3);
    private static final String REDIS_KEY_PERM = "role";
    private static final String MUTEX_KEY_PREFIX = "mutex:role:";

    private final RoleExtMapper roleExtMapper;
    private final PermissionExtMapper permissionExtMapper;
    private final RolePermissionExtMapper rolePermissionExtMapper;
    private final StringRedisTemplate redisTemplate;

    public PermissionServiceImpl(RoleExtMapper roleExtMapper,
                                 PermissionExtMapper permissionExtMapper,
                                 RolePermissionExtMapper rolePermissionExtMapper,
                                 ObjectProvider<StringRedisTemplate> redisTemplateProvider) {
        this.roleExtMapper = roleExtMapper;
        this.permissionExtMapper = permissionExtMapper;
        this.rolePermissionExtMapper = rolePermissionExtMapper;
        this.redisTemplate = redisTemplateProvider.getIfAvailable();
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
    @Transactional
    public void assignPermissions(Long roleId, Long[] permIds) {
        Optional.ofNullable(roleExtMapper.selectByPrimaryKey(roleId)).ifPresent(role -> {
            RolePermissionExample example = new RolePermissionExample();
            example.createCriteria().andRoleIdEqualTo(roleId);
            rolePermissionExtMapper.deleteByExample(example);
            if (ArrayUtils.isNotEmpty(permIds)) {
                List<RolePermission> list = Arrays.stream(permIds).map(permId -> {
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setId(SnowflakeIdWorker.getInstance().nextId());
                    rolePermission.setRoleId(roleId);
                    rolePermission.setPermissionId(permId);
                    return rolePermission;
                }).collect(Collectors.toList());
                rolePermissionExtMapper.insertBatch(list);
            }
            syncCache(REDIS_KEY_PERM, role.getCode()); // 缓存同步
        });
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
    public List<Permission> getPermissions(String role) {
        // 从缓存中获取数据
        List<Permission> perms = getPermsFromCache(REDIS_KEY_PERM, role);
        if (perms != null) {
            return perms;
        }
        if (getMutexLock(role)) {
            // 从DB中查询数据
            perms = permissionExtMapper.getPermsByRole(role);
            // 将数据回设到缓存中
            setPermsToCache(REDIS_KEY_PERM, role, perms);
            return perms;
        }
        // 休眠500毫秒再重试
        sleep(500L);
        return getPermissions(role);
    }

    @Override
    public List<Permission> getPermissions(String... roles) {
        return Arrays.stream(roles)
                .map(this::getPermissions)
                .collect(Collectors.toList())
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<Permission> getPermissions(Long userId, ResourceType type) {
        return permissionExtMapper.getPermsByUid(userId, type);
    }

    private void syncCache(String key, String hashKey) {
        try {
            redisTemplate.opsForHash().delete(key, hashKey);
        } catch (Exception e) {
            log.warn("缓存同步失败", e);
        }
    }

    private void setPermsToCache(String key, String hashKey, List<Permission> perms) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, JsonUtils.toJson(perms));
            redisTemplate.delete(prefixKeyMutex(hashKey)); // 删除互斥锁
        } catch (Exception e) { // 缓存的添加不能影响正常业务逻辑
            log.warn("向缓存中添加权限数据失败", e);
        }
    }

    private List<Permission> getPermsFromCache(String key, String hashKey) {
        try {
            HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
            String json = opsForHash.get(key, hashKey);
            if (StringUtils.isNotBlank(json)) {
                return JsonUtils.fromJson(json, List.class, Permission.class);
            }
        } catch (Exception e) { // 缓存的添加不能影响正常业务逻辑
            log.warn("从缓存中获取权限数据失败", e);
        }
        return null;
    }

    private void sleep(long timeout) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch (InterruptedException ignored) {
        }
    }

    private boolean getMutexLock(String hashKey) {
        try {
            String mutexKey = prefixKeyMutex(hashKey);
            long nextTimeMillis = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(DEFAULT_TIMEOUT);
            boolean flag = redisTemplate.opsForValue().setIfAbsent(mutexKey, Long.toString(nextTimeMillis));
            if (flag) {
                redisTemplate.expire(mutexKey, DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            }
            return flag;
        } catch (Exception e) {
            return false;
        }
    }

    private static String prefixKeyMutex(String key) {
        return MUTEX_KEY_PREFIX + ":" + key;
    }

}
