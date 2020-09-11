/*
 Navicat MySQL Data Transfer

 Source Server         : AlwaysXu
 Source Server Type    : MySQL
 Source Server Version : 80016
 Source Host           : localhost:3306
 Source Schema         : db_orm

 Target Server Type    : MySQL
 Target Server Version : 80016
 File Encoding         : 65001

 Date: 12/09/2020 01:19:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_account
-- ----------------------------
DROP TABLE IF EXISTS `t_account`;
CREATE TABLE `t_account`  (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `balance` double(255, 0) NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_account
-- ----------------------------
INSERT INTO `t_account` VALUES (1, 'x123', '123456', 964, 'xxbb');
INSERT INTO `t_account` VALUES (2, 'x124', '123456', 3000, 'aabb');
INSERT INTO `t_account` VALUES (3, 'x125', '123456', 1336, 'zzbb');

-- ----------------------------
-- Table structure for t_airplane
-- ----------------------------
DROP TABLE IF EXISTS `t_airplane`;
CREATE TABLE `t_airplane`  (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `airplane_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `time` int(5) NULL DEFAULT NULL COMMENT '单位：分钟',
  `price` double NULL DEFAULT NULL,
  `take_off_airport_id` int(10) NULL DEFAULT NULL,
  `land_airport_id` int(10) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_airplane
-- ----------------------------
INSERT INTO `t_airplane` VALUES (1, 'by747', 120, 560.1, 1, 3);
INSERT INTO `t_airplane` VALUES (2, 'by730', 120, 344.5, 2, 3);

-- ----------------------------
-- Table structure for t_airport
-- ----------------------------
DROP TABLE IF EXISTS `t_airport`;
CREATE TABLE `t_airport`  (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `port_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `city_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_airport
-- ----------------------------
INSERT INTO `t_airport` VALUES (1, '首都机场', '北京');
INSERT INTO `t_airport` VALUES (2, '南苑机场', '北京');
INSERT INTO `t_airport` VALUES (3, '虹桥机场', '上海');

-- ----------------------------
-- Table structure for t_freeze
-- ----------------------------
DROP TABLE IF EXISTS `t_freeze`;
CREATE TABLE `t_freeze`  (
  `id` int(11) NOT NULL,
  `state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_freeze
-- ----------------------------
INSERT INTO `t_freeze` VALUES (0, '冻结');
INSERT INTO `t_freeze` VALUES (1, '正常');

-- ----------------------------
-- Table structure for t_purview
-- ----------------------------
DROP TABLE IF EXISTS `t_purview`;
CREATE TABLE `t_purview`  (
  `id` int(11) NOT NULL,
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_purview
-- ----------------------------
INSERT INTO `t_purview` VALUES (1, '管理员');
INSERT INTO `t_purview` VALUES (2, '员工');
INSERT INTO `t_purview` VALUES (3, '游客');

-- ----------------------------
-- Table structure for t_teacher
-- ----------------------------
DROP TABLE IF EXISTS `t_teacher`;
CREATE TABLE `t_teacher`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_teacher
-- ----------------------------
INSERT INTO `t_teacher` VALUES (1, 'Mrxx');
INSERT INTO `t_teacher` VALUES (2, 'Mryy');
INSERT INTO `t_teacher` VALUES (3, 'Mrzz');

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `if_freeze` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (1, 'xxbb', '123456', 1);
INSERT INTO `t_user` VALUES (2, 'guest', '123456', 0);
INSERT INTO `t_user` VALUES (3, 'aabb', '123456', 0);
INSERT INTO `t_user` VALUES (4, 'A', '123456', 0);
INSERT INTO `t_user` VALUES (5, 'B', '123456', 0);
INSERT INTO `t_user` VALUES (6, 'C', '123456', 0);
INSERT INTO `t_user` VALUES (7, 'D', '123456', 0);
INSERT INTO `t_user` VALUES (8, 'E', '123456', 0);
INSERT INTO `t_user` VALUES (9, 'F', '123456', 0);
INSERT INTO `t_user` VALUES (10, 'G', '123456', 0);
INSERT INTO `t_user` VALUES (11, 'H', '123456', 0);
INSERT INTO `t_user` VALUES (12, 'I', '123456', 0);
INSERT INTO `t_user` VALUES (13, 'J', '123456', 0);
INSERT INTO `t_user` VALUES (14, 'K', '123456', 0);
INSERT INTO `t_user` VALUES (15, 'L', '123456', 0);
INSERT INTO `t_user` VALUES (16, 'M', '123456', 0);
INSERT INTO `t_user` VALUES (17, 'N', '123456', 0);
INSERT INTO `t_user` VALUES (18, 'O', '123456', 0);
INSERT INTO `t_user` VALUES (19, 'P', '123456', 0);
INSERT INTO `t_user` VALUES (20, 'Q', '123456', 0);
INSERT INTO `t_user` VALUES (21, 'R', '123456', 0);
INSERT INTO `t_user` VALUES (22, 'S', '123456', 0);
INSERT INTO `t_user` VALUES (23, 'T', '123456', 0);

SET FOREIGN_KEY_CHECKS = 1;
