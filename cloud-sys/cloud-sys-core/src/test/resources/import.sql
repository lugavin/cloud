/*
Navicat MySQL Data Transfer

Source Server         : 127.0.0.1 - mysql
Source Server Version : 50170
Source Host           : 127.0.0.1:3306
Source Database       : cloud

Target Server Type    : MYSQL
Target Server Version : 50170
File Encoding         : 65001

Date: 2019-04-25 19:45:58
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `sys_permission`
-- ----------------------------
CREATE TABLE `sys_permission` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `code` varchar(128) NOT NULL COMMENT '资源代码',
  `name` varchar(128) NOT NULL COMMENT '资源名称',
  `type` varchar(10) NOT NULL COMMENT '资源类型(菜单|功能)',
  `url` varchar(128) NOT NULL COMMENT '资源路径',
  `method` varchar(10) NOT NULL COMMENT '资源请求类型',
  `seq` smallint(6) DEFAULT NULL COMMENT '排序号',
  `icon` varchar(32) DEFAULT NULL COMMENT '资源图标',
  `is_parent` tinyint(1) DEFAULT NULL COMMENT '是否叶子节点',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父结点ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `sys_permission_url_method_uk` (`url`, `method`) USING BTREE,
  KEY `sys_permission_parent_id_fk` (`parent_id`) USING BTREE,
  CONSTRAINT `sys_permission_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `sys_permission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='权限表';

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` (`id`, `code`, `name`, `type`, `url`, `method`, `seq`, `icon`, `is_parent`, `parent_id`) VALUES
('1', 'sys:mgmt', '系统管理', 'MENU', '/rest/sys', 'GET', '1', 'fa fa-desktop', '1', NULL),
('11', 'user:mgmt', '用户管理', 'MENU', '/rest/sys/users', 'GET', '101', 'fa fa-user', '1', '1'),
('12', 'user:create', '用户新增', 'FUNC', '/rest/sys/users', 'POST', '102', '', '0', '11'),
('13', 'user:delete', '用户删除', 'FUNC', '/rest/sys/users/{id}', 'DELETE', '103', '', '0', '11'),
('14', 'user:update', '用户修改', 'FUNC', '/rest/sys/users/{id}', 'PUT', '104', '', '0', '11'),
('15', 'user:search', '用户查询', 'FUNC', '/rest/sys/users/{id}', 'GET', '105', '', '0', '11'),
('21', 'role:mgmt', '角色管理', 'MENU', '/rest/sys/roles', 'GET', '201', 'fa fa-lock', '1', '1'),
('22', 'role:create', '角色新增', 'FUNC', '/rest/sys/roles/{id}', 'POST', '202', '', '0', '21'),
('23', 'role:delete', '角色删除', 'FUNC', '/rest/sys/roles/{id}', 'DELETE', '203', '', '0', '21'),
('24', 'role:update', '角色修改', 'FUNC', '/rest/sys/roles/{id}', 'PUT', '204', '', '0', '21'),
('25', 'role:search', '角色查询', 'FUNC', '/rest/sys/roles/{id}', 'GET', '205', '', '0', '21'),
('31', 'perm:mgmt', '权限管理', 'MENU', '/rest/sys/perms', 'GET', '301', 'fa fa-key', '1', '1'),
('32', 'perm:create', '权限新增', 'FUNC', '/rest/sys/perms/{id}', 'POST', '302', '', '0', '31'),
('33', 'perm:delete', '权限删除', 'FUNC', '/rest/sys/perms/{id}', 'DELETE', '303', '', '0', '31'),
('34', 'perm:update', '权限修改', 'FUNC', '/rest/sys/perms/{id}', 'PUT', '304', '', '0', '31'),
('35', 'perm:search', '权限查询', 'FUNC', '/rest/sys/perms/{id}', 'GET', '305', '', '0', '31');

-- ----------------------------
-- Table structure for `sys_role`
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `code` varchar(128) NOT NULL COMMENT '角色编码',
  `name` varchar(128) NOT NULL COMMENT '角色名称',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `SYS_ROLE_CODE_UK` (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1001', 'admin', '管理员', '');

-- ----------------------------
-- Table structure for `sys_role_permission`
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `permission_id` bigint(20) NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `SYS_ROLE_PERMISSION_UK` (`role_id`,`permission_id`) USING BTREE,
  KEY `SYS_ROLE_PERMISSION_FK` (`permission_id`) USING BTREE,
  CONSTRAINT `sys_role_permission_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`),
  CONSTRAINT `sys_role_permission_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `sys_permission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色-权限中间表';

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES
(10001, 1001, 1),
(10002, 1001, 11),
(10003, 1001, 12),
(10004, 1001, 13),
(10005, 1001, 14),
(10006, 1001, 15),
(10008, 1001, 21),
(10009, 1001, 22),
(10010, 1001, 23),
(10011, 1001, 24),
(10012, 1001, 25),
(10013, 1001, 31),
(10014, 1001, 32),
(10015, 1001, 33),
(10016, 1001, 34),
(10017, 1001, 35);

-- ----------------------------
-- Table structure for `sys_user`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `username` varchar(20) NOT NULL COMMENT '用户名',
  `password` varchar(60) NOT NULL COMMENT '密码(Hash字符串)',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `salt` varchar(32) DEFAULT NULL COMMENT '盐(密码加密时使用以防止穷举暴力破解,一般由随机数生成)',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(256) DEFAULT NULL COMMENT '头像',
  `lang_key` varchar(6) DEFAULT NULL COMMENT '语言',
  `activated` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否激活',
  `activation_key` varchar(32) DEFAULT NULL COMMENT '激活码',
  `reset_key` varchar(32) DEFAULT NULL COMMENT '重置码',
  `reset_date` timestamp NULL DEFAULT NULL COMMENT '重置日期',
  `created_by` varchar(32) NOT NULL COMMENT '创建者',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '最后修改人',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
  PRIMARY KEY (`id`),
  UNIQUE KEY `SYS_USER_USERNAME_UK` (`username`),
  UNIQUE KEY `SYS_USER_PHONE_UK` (`phone`),
  UNIQUE KEY `SYS_USER_EMAIL_UK` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `salt`, `phone`, `email`, `avatar`, `lang_key`, `activated`, `activation_key`, `reset_key`, `reset_date`, `created_by`, `created_at`, `updated_by`, `updated_at`) VALUES (101, 'admin', MD5(CONCAT('tpsTiktG8VcFWD4dvhhGq','P@ssw0rd')), '管理员', 'tpsTiktG8VcFWD4dvhhGq', '16666666666', 'admin@gmail.com', NULL, NULL, 1, NULL, NULL, NULL, 'system', '2019-01-01 00:00:00', NULL, NULL);

-- ----------------------------
-- Table structure for `sys_user_role`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `SYS_USER_ROLE_UK` (`user_id`,`role_id`) USING BTREE,
  KEY `SYS_USER_ROLE_FK` (`role_id`) USING BTREE,
  CONSTRAINT `sys_user_role_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `sys_user_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户-角色中间表';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES
(10001, 101, 1001);
