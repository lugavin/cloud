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
-- Table structure for `distlock`
-- ----------------------------
DROP TABLE IF EXISTS `distlock`;
CREATE TABLE `distlock` (
	`id` bigint(20) NOT NULL COMMENT '主键',
	`method_name` VARCHAR(64) NOT NULL COMMENT '锁定的方法名',
	`updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '保存数据时间(自动生成)',
	PRIMARY KEY (`id`),
	UNIQUE INDEX `uidx_method_name`(`method_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='基于数据库实现的分布式锁';

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
 `created_at` DATETIME NOT NULL COMMENT '创建日期',
 `updated_by` VARCHAR(50) DEFAULT NULL COMMENT '最后修改人',
 `updated_at` DATETIME DEFAULT NULL COMMENT '最后修改日期'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='访问统计' PARTITION BY RANGE (TO_DAYS(created_at))(
  PARTITION P_20180101 VALUES LESS THAN(TO_DAYS('2018-01-01')),
  PARTITION P_20190101 VALUES LESS THAN(TO_DAYS('2019-01-01')),
  PARTITION P_20200301 VALUES LESS THAN(TO_DAYS('2020-01-01'))
);
DROP TABLE IF EXISTS `counter_his`;
CREATE TABLE counter_his LIKE counter;
ALTER TABLE counter_his COMMENT '访问统计历史表';
DROP TABLE IF EXISTS `counter_tmp`;
CREATE TABLE counter_tmp LIKE counter;
ALTER TABLE counter_tmp REMOVE PARTITIONING;
ALTER TABLE counter_tmp COMMENT '访问统计临时表(用于分区交换数据)';
-- INSERT INTO counter SELECT 20170101, 'Gavin\'s Blog', 'https://lugavin.github.io', 1, 'admin', '2017-01-01 00:00:00', null, null;
-- EXPLAIN PARTITIONS SELECT COUNT(1) FROM counter WHERE created_at < '2018-01-01';
