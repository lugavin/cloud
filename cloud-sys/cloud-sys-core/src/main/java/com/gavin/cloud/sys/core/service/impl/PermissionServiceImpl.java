package com.gavin.cloud.sys.core.service.impl;

import com.gavin.cloud.common.base.util.JsonUtils;
import com.gavin.cloud.common.base.util.SnowflakeIdWorker;
import com.gavin.cloud.sys.core.enums.ResourceType;
import com.gavin.cloud.sys.core.mapper.PermissionMapper;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class PermissionServiceImpl implements PermissionService {

    private static final int DEFAULT_TIMEOUT = 3 * 60;
    private static final String MUTEX_KEY_PREFIX = "mutex:perm:";

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private PermissionExtMapper permissionExtMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private RolePermissionExtMapper rolePermissionExtMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    @Transactional
    public Permission createPermission(Permission permission) {
        permission.setId(SnowflakeIdWorker.getInstance().nextId());
        permissionMapper.insert(permission);
        return permission;
    }

    @Override
    @Transactional
    public Permission updatePermission(Permission permission) {
        permissionMapper.updateByPrimaryKey(permission);
        return permission;
    }

    @Override
    @Transactional
    public void deletePermission(Long id) {
        permissionMapper.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional
    public void deletePermissions(Long[] ids) {
        PermissionExample example = new PermissionExample();
        example.createCriteria().andIdIn(Arrays.asList(ids));
        permissionMapper.deleteByExample(example);
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, Long[] permIds) {
        RolePermissionExample example = new RolePermissionExample();
        example.createCriteria().andRoleIdEqualTo(roleId);
        rolePermissionMapper.deleteByExample(example);
        if (ArrayUtils.isNotEmpty(permIds)) {
            List<RolePermission> list = new ArrayList<>();
            for (Long permId : permIds) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setId(SnowflakeIdWorker.getInstance().nextId());
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permId);
                list.add(rolePermission);
            }
            rolePermissionExtMapper.insertBatch(list);
        }
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
    public List<Permission> getPermissions(Long userId, ResourceType type) {
        String hashKey = Long.toString(userId);
        // 从缓存中获取数据
        List<Permission> perms = getPermsFromCache(type.name(), hashKey);
        if (perms != null) {
            return perms;
        }
        if (getMutexLock(hashKey)) {
            // 从DB中查询数据
            perms = permissionExtMapper.getPerms(userId, type);
            // 将数据回设到缓存中
            setPermsToCache(type.name(), hashKey, perms);
            return perms;
        }
        // 休眠500毫秒再重试
        sleep(500L);
        return getPermissions(userId, type);
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
