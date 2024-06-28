尚庭公寓是一个公寓租赁平台项目，包含**移动端**和**后台管理系统**，其中移动端面向广大用户，提供找房、看房预约、租约管理等功能，后台管理系统面向管理员，提供公寓（房源）管理、租赁管理、用户管理等功能
## 1、技术选型

| **Java**         | **17**                          |
| ---------------- | :------------------------------ |
| **MYSQL**        | **8.0.32**                      |
| **Redis**        | **7.0.13**                      |
| **MINIO**        | **20240613225353.0.0-1.x86_64** |
| **Mybatis-Plus** | **3.5.3.1**                     |
| **knife4j**      | **4.1.0**                       |
| **Jwt**          | **0.11.2**                      |

## 2、项目结构

lease
├── common（公共模块——工具类、公用配置等）
│   ├── pom.xml
│   └── src
├── model（数据模型——与数据库相对应地实体类）
│   ├── pom.xml
│   └── src
├── web（Web模块）
│   ├── pom.xml
│   ├── web-admin（后台管理系统Web模块——包含mapper、service、controller）
│   │   ├── pom.xml
│   │   └── src
│   └── web-app（移动端Web模块——包含mapper、service、controller）
│       ├── pom.xml
│       └── src
└── pom.xml
