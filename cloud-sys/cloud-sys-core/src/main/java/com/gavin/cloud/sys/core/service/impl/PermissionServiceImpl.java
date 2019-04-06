package com.gavin.cloud.sys.core.service.impl;

import com.gavin.cloud.common.base.util.JsonUtils;
import com.gavin.cloud.common.base.util.SnowflakeIdWorker;
import com.gavin.cloud.sys.core.enums.ResourceType;
import com.gavin.cloud.sys.core.mapper.PermissionMapper;
import com.gavin.cloud.sys.core.mapper.RoleMapper;
import com.gavin.cloud.sys.core.mapper.RolePermissionMapper;
import com.gavin.cloud.sys.core.mapper.ext.PermissionExtMapper;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PermissionServiceImpl implements PermissionService {

    private static final int DEFAULT_TIMEOUT = 3 * 60;
    private static final String REDIS_KEY_PERM = "role";
    private static final String MUTEX_KEY_PREFIX = "mutex:role:";

    private final StringRedisTemplate redisTemplate;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final PermissionExtMapper permissionExtMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final RolePermissionExtMapper rolePermissionExtMapper;

    public PermissionServiceImpl(ObjectProvider<StringRedisTemplate> redisTemplateProvider,
                                 RoleMapper roleMapper,
                                 PermissionMapper permissionMapper,
                                 PermissionExtMapper permissionExtMapper,
                                 RolePermissionMapper rolePermissionMapper,
                                 RolePermissionExtMapper rolePermissionExtMapper) {
        this.redisTemplate = redisTemplateProvider.getIfAvailable();
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
        this.permissionExtMapper = permissionExtMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.rolePermissionExtMapper = rolePermissionExtMapper;
    }

    @Override
    @Transactional
    public Permission createPermission(Permission permission) {
        permission.setId(SnowflakeIdWorker.getInstance().nextId());
        permissionMapper.insertSelective(permission);
        return permission;
    }

    @Override
    @Transactional
    public Permission updatePermission(Long id, Permission permission) {
        permission.setId(id);
        permissionMapper.updateByPrimaryKey(permission);
        return permission;
    }

    @Override
    @Transactional
    public void deletePermission(Long id) {
        RolePermissionExample example = new RolePermissionExample();
        example.createCriteria().andPermissionIdEqualTo(id);
        rolePermissionMapper.deleteByExample(example);
        permissionMapper.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional
    public void deletePermissions(Long[] ids) {
        RolePermissionExample rolePermExample = new RolePermissionExample();
        rolePermExample.createCriteria().andPermissionIdIn(Arrays.asList(ids));
        rolePermissionMapper.deleteByExample(rolePermExample);
        PermissionExample example = new PermissionExample();
        example.createCriteria().andIdIn(Arrays.asList(ids));
        permissionMapper.deleteByExample(example);
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, Long[] permIds) {
        Optional.ofNullable(roleMapper.selectByPrimaryKey(roleId))
                .ifPresent(role -> {
                    RolePermissionExample example = new RolePermissionExample();
                    example.createCriteria().andRoleIdEqualTo(roleId);
                    rolePermissionMapper.deleteByExample(example);
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
        return permissionMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Permission> getPermissions() {
        return permissionMapper.selectByExample(new PermissionExample());
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
                return JsonUtils.getList(json, Permission.class);
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
