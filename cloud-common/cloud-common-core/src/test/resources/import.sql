/*
Navicat MySQL Data Transfer

Source Server         : MySQL
Source Server Version : 50170
Source Host           : localhost:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50170
File Encoding         : 65001

Date: 2018-04-21 14:09:21
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `comment`
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `pid` bigint(20) DEFAULT NULL COMMENT '父ID',
  `nick` varchar(50) DEFAULT NULL COMMENT '昵称',
  `mail` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `link` varchar(256) DEFAULT NULL COMMENT '主页',
  `ua` varchar(32) DEFAULT NULL COMMENT '浏览器标识',
  `url` varchar(256) DEFAULT NULL COMMENT 'URL地址',
  `comment` varchar(256) DEFAULT NULL COMMENT '留言',
  `created_by` varchar(50) NOT NULL COMMENT '创建者',
  `created_at` timestamp NOT NULL COMMENT '创建日期',
  `updated_by` varchar(50) DEFAULT NULL COMMENT '最后修改人',
  `updated_at` timestamp NULL DEFAULT NULL COMMENT '最后修改日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='评论';

-- ----------------------------
-- Table structure for `counter`
-- ----------------------------
DROP TABLE IF EXISTS `counter`;
CREATE TABLE `counter` (
 `id` BIGINT(20) NOT NULL COMMENT '主键',
 `title` VARCHAR(256) DEFAULT NULL COMMENT '标题',
 `url` VARCHAR(256) DEFAULT NULL COMMENT 'URL地址',
 `time` INT(11) DEFAULT 0 COMMENT '访问次数',
 `created_by` VARCHAR(50) NOT NULL COMMENT '创建者',
 `created_at` TIMESTAMP NOT NULL COMMENT '创建日期',
 `updated_by` VARCHAR(50) DEFAULT NULL COMMENT '最后修改人',
 `updated_at` TIMESTAMP NULL DEFAULT NULL COMMENT '最后修改日期'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='访问统计' PARTITION BY RANGE (UNIX_TIMESTAMP(created_at)) (
  PARTITION P_20180101 VALUES LESS THAN(UNIX_TIMESTAMP('2018-01-01 00:00:00')),
  PARTITION P_20190101 VALUES LESS THAN(UNIX_TIMESTAMP('2019-01-01 00:00:00')),
  PARTITION P_20200301 VALUES LESS THAN(UNIX_TIMESTAMP('2020-01-01 00:00:00'))
);