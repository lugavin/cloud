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
-- Table structure for `sys_role`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_auth_token` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `uid` bigint(20) NOT NULL COMMENT '登录ID',
  `client_ip` varchar(32) NOT NULL COMMENT '登录IP',
  `refresh_token` varchar(64) NOT NULL COMMENT '刷新令牌',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  `expired_at` timestamp NOT NULL COMMENT '失效日期',
  PRIMARY KEY (`id`),
  UNIQUE KEY `SYS_AUTH_TOKEN_REFRESH_TOKEN_UK` (`refresh_token`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户认证';

-- ----------------------------
-- Records of sys_auth_token
-- ----------------------------
INSERT INTO `sys_auth_token` VALUES ('10001', '101', '127.0.0.1', UUID(), NOW(), DATE_ADD(NOW(), INTERVAL 1 MONTH));