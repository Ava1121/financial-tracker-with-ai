-- 创建数据库
CREATE DATABASE IF NOT EXISTS piems DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE piems;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
  PRIMARY KEY (`id`),
  KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 分类表
CREATE TABLE IF NOT EXISTS `category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `type` TINYINT NOT NULL COMMENT '类型：1-收入，2-支出',
  `icon` VARCHAR(100) DEFAULT NULL COMMENT '分类图标',
  `user_id` BIGINT NOT NULL COMMENT '用户ID：0-系统默认分类',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
  PRIMARY KEY (`id`),
  KEY `idx_type_user_id` (`type`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- 收支记录表
CREATE TABLE IF NOT EXISTS `record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `type` TINYINT NOT NULL COMMENT '类型：1-收入，2-支出',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
  `category_id` BIGINT NOT NULL COMMENT '分类ID',
  `category_name` VARCHAR(50) NOT NULL COMMENT '分类名称（冗余字段）',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `record_time` DATETIME NOT NULL COMMENT '记录时间',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id_type` (`user_id`, `type`),
  KEY `idx_user_id_record_time` (`user_id`, `record_time`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收支记录表';

-- 插入默认分类数据
INSERT IGNORE INTO `category` (`name`, `type`, `user_id`, `create_time`, `update_time`, `status`) VALUES
('工资', 1, 0, NOW(), NOW(), 1),
('奖金', 1, 0, NOW(), NOW(), 1),
('投资收益', 1, 0, NOW(), NOW(), 1),
('其他收入', 1, 0, NOW(), NOW(), 1),
('餐饮', 2, 0, NOW(), NOW(), 1),
('交通', 2, 0, NOW(), NOW(), 1),
('购物', 2, 0, NOW(), NOW(), 1),
('娱乐', 2, 0, NOW(), NOW(), 1),
('医疗', 2, 0, NOW(), NOW(), 1),
('教育', 2, 0, NOW(), NOW(), 1),
('房租', 2, 0, NOW(), NOW(), 1),
('水电费', 2, 0, NOW(), NOW(), 1),
('其他支出', 2, 0, NOW(), NOW(), 1);
