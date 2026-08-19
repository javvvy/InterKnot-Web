# Interknot-Web 后端项目

基于 **Spring Cloud Alibaba** 的微服务后端系统，为「Interknot」社区网站提供用户、文章、评论、文件、聊天、搜索等核心服务能力。

## 目录

- [项目简介](#项目简介)
- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [模块详解](#模块详解)
- [核心功能](#核心功能)
- [架构设计](#架构设计)
- [环境依赖](#环境依赖)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [接口与鉴权](#接口与鉴权)
- [注意事项](#注意事项)

## 项目简介

Interknot-Web 是一个微服务架构的后端项目，采用 Maven 多模块工程组织，共包含 **10 个模块**。系统通过 **Nacos** 进行服务注册与配置管理，通过 **Spring Cloud Gateway** 统一网关对外提供 RESTful 接口，并使用 **JWT** 实现无状态身份认证。服务间通信基于 **OpenFeign + LoadBalancer** 完成。

## 技术栈

| 类别 | 技术 |
| --- | --- |
| 语言 | Java 18（部分模块声明 Java 17） |
| 基础框架 | Spring Boot 3.2.12 |
| 微服务 | Spring Cloud 2023.0.3、Spring Cloud Alibaba 2023.0.1.0 |
| 注册 / 配置中心 | Nacos |
| 流量治理 | Sentinel |
| 网关 | Spring Cloud Gateway |
| 服务调用 | OpenFeign + LoadBalancer |
| ORM | MyBatis-Plus 3.5.5、MyBatis 3.5.16 |
| 数据库 | MySQL |
| 缓存 | Redis（用户、聊天服务） |
| 安全认证 | Spring Security + JWT（jjwt） |
| 文件存储 | 阿里云 OSS |
| 消息队列 | RabbitMQ（spring-boot-starter-amqp） |
| 搜索引擎 | Elasticsearch 8.12.0（elasticsearch-java） |
| 实时通信 | WebSocket + SSE |
| AI 能力 | DeepSeek API（角色扮演通话） |
| 工具库 | Lombok、Hutool |

## 项目结构

```
Interknot-Web
├── IW-common           # 通用模块：DTO / VO / Result / JWT / 异常处理 / 常量
├── IW-api              # Feign 客户端聚合模块（服务间调用接口定义）
├── IW-gateway          # API 网关（8080）：路由转发 + JWT 全局鉴权
├── IW-user-service     # 用户服务（8083）：登录 / 注册 / 资料 / 卡片 / 头像
├── IW-article-service  # 文章服务（8081）：文章 / 草稿 / 点赞 / 阅读 / 封面
├── IW-comment-service  # 评论服务（8082）：评论 / 点赞
├── IW-file-service     # 文件服务（8084）：文件上传（OSS）
├── IW-mq-service       # 消息服务（8085）：消息队列（规划中）
├── IW-search-service   # 搜索服务（8086）：搜索（规划中）
└── IW-chat-service     # 聊天服务（8088）：敲敲会话 / 私信 / AI 通话
```

## 模块详解

### IW-common（通用模块）

被各服务依赖的基础模块，提供公共能力：

- **统一返回结构**：`Result`、`PageResult`
- **数据传输对象**：`dto/`、`vo/` 下各类实体与视图对象
- **认证工具**：`JwtUtils`（token 生成与校验）、`UserContextHolder`（用户上下文）
- **异常处理**：`BaseExceptionHandler`
- **第三方 SDK**：阿里云 OSS、jjwt、Jackson、Hutool

### IW-gateway（API 网关 · 8080）

- 统一入口，基于 Spring Cloud Gateway 路由转发至下游服务
- 内置 `JwtAuthGlobalFilter` 全局过滤器，负责 token 校验与用户信息注入（`X-User-Id`、`X-User-Role`）
- 支持公开路径 / 匿名路径白名单放行

### IW-user-service（用户服务 · 8083）

用户与认证相关功能：

- 登录 / 注册（邮箱验证码）/ token 续期（`/auth/**`）
- 个人资料查询与修改、卡片装备、头像装备（`/me/**`）
- 用户信息查询与批量查询（`/user/**`，供服务间调用）
- 集成 Spring Security + JWT + Redis + 邮件发送

### IW-article-service（文章服务 · 8081）

文章与草稿管理：

- 文章列表 / 详情 / 删除 / 阅读数 / 点赞（`/article/**`）
- 草稿创建 / 修改 / 发布（`/article`、`/article/{draftNo}/publish`）
- 封面管理（`CoverMapper`、`CoverPOJO`）

### IW-comment-service（评论服务 · 8082）

评论相关功能：

- 评论列表 / 回复列表 / 创建 / 删除（`/comments/**`）
- 评论点赞（`/comments/{commentNo}/like`、批量点赞）

### IW-file-service（文件服务 · 8084）

- 媒体文件上传（`/media/upload`），存储至阿里云 OSS
- 通过 `OssUtil` 封装 OSS 上传逻辑

### IW-chat-service（聊天服务 · 8088）

聊天与实时通信，功能最丰富的模块：

- **敲敲（Knock）会话**：会话列表、消息、已读（`/knock/**`），SSE 实时推送（`/knock/stream`）
- **DM 私信**：会话列表、创建、发送、撤回、已读（`/dm/**`），WebSocket 长连接（`/dm/socket`）
- **KK-Call AI 通话**：与 AI 角色进行流式对话（`/kk-call/**`），基于 DeepSeek API + SSE
- 集成 Redis、WebSocket、SSE

### IW-api（Feign 聚合 · 8087）

- 定义 `ArticleClient`、`CommentClient`、`UserClient` 等 Feign 客户端
- 通过 `UserInfoFeignInterceptor` 在服务间调用时透传用户上下文

### IW-mq-service（消息服务 · 8085）

- 预留的消息队列服务，当前仅搭建基础框架

### IW-search-service（搜索服务 · 8086）

- 预留的搜索服务，已引入 Elasticsearch、RabbitMQ、Seata 依赖，当前仅搭建基础框架

## 核心功能

- **用户认证**：账号密码登录、邮箱验证码注册、JWT 无状态认证、token 续期
- **用户中心**：个人资料编辑、卡片装备、头像装备
- **文章系统**：文章发布、草稿、点赞、阅读计数、封面管理
- **评论系统**：评论、回复、点赞
- **文件上传**：图片等媒体文件上传至阿里云 OSS
- **实时聊天**：敲敲会话（SSE）、DM 私信（WebSocket）、AI 角色扮演通话（DeepSeek + SSE）

## 架构设计

```
                       ┌─────────────────┐
                       │   客户端 (前端)   │
                       └────────┬────────┘
                                │ HTTP / WebSocket / SSE
                       ┌────────▼────────┐
                       │   IW-gateway    │  JWT 鉴权 + 路由转发
                       └────────┬────────┘
              ┌──────────┬──────┴───────┬──────────────┐
              ▼          ▼              ▼              ▼
     ┌────────────┐ ┌──────────┐ ┌────────────┐ ┌────────────┐
     │ user-svc   │ │article   │ │ comment    │ │ chat-svc   │ ...
     └─────┬──────┘ └────┬─────┘ └─────┬──────┘ └─────┬──────┘
           │             │             │              │
           └─────────────┴─────────────┴──────────────┘
                         │ Feign 服务调用
              ┌──────────▼──────────┐
              │  Nacos 注册/配置中心  │
              └─────────────────────┘

  中间件：MySQL / Redis / RabbitMQ / Elasticsearch / 阿里云 OSS / DeepSeek API
```

**鉴权流程**：

1. 客户端携带 `token` 请求头访问网关
2. 网关 `JwtAuthGlobalFilter` 校验 token，解析出用户 ID 与角色
3. 校验通过后，将用户信息注入 `X-User-Id`、`X-User-Role` 请求头，转发至下游服务
4. 下游服务通过 `UserContextInterceptor` 读取用户上下文

## 环境依赖

| 依赖 | 地址 / 默认配置 | 说明 |
| --- | --- | --- |
| JDK | 18 | 编译运行环境 |
| Maven | 3.6+ | 构建工具 |
| Nacos | `localhost:8848`（`nacos/nacos`） | 注册中心 + 配置中心 |
| Sentinel Dashboard | `localhost:8858` | 流量监控 |
| MySQL | `localhost:3306/interknot_web` | 业务数据库 |
| Redis | 本地默认 | 缓存 / 会话 / 验证码 |
| 阿里云 OSS | 需配置 AccessKey | 文件存储 |
| DeepSeek API | 环境变量 `DEEPSEEK_API_KEY` | AI 通话能力 |

## 快速开始

### 1. 环境准备

确保已安装 JDK 18、Maven，并启动以下中间件：

- Nacos（注册中心 + 配置中心，默认 `localhost:8848`）
- MySQL（创建数据库 `interknot_web`）
- Redis
- Sentinel Dashboard（可选）
- 阿里云 OSS（文件服务需要）
- Elasticsearch / RabbitMQ（搜索、消息服务，规划中）

### 2. 配置 Nacos

项目大部分业务配置托管在 Nacos 配置中心，需要先在 Nacos 中创建对应配置项：

- `common.yaml`：公共配置（数据源、Redis、MyBatis 等）
- `article-service.yaml`、`comment-service.yaml`、`user-service.yaml`、`file-service.yaml`、`chat-service.yaml`、`gateway.yaml`、`mq-service.yaml`、`api.yml`

### 3. 编译

```bash
cd Interknot-Web
mvn clean install -DskipTests
```

### 4. 启动服务

按依赖顺序启动各服务（网关、api 建议最后启动）：

```bash
# 基础服务
mvn spring-boot:run -pl IW-user-service
mvn spring-boot:run -pl IW-article-service
mvn spring-boot:run -pl IW-comment-service
mvn spring-boot:run -pl IW-file-service
mvn spring-boot:run -pl IW-chat-service

# 聚合与网关
mvn spring-boot:run -pl IW-api
mvn spring-boot:run -pl IW-gateway
```

或使用 IDE（IDEA）逐个运行各模块的 `*Application` 主类。

## 配置说明

- 各服务 `bootstrap.yml` 统一声明了 Nacos 注册中心与配置中心地址（`localhost:8848`，`nacos/nacos`）
- 各服务 `application.yaml` 通过 `spring.config.import: nacos:xxx.yaml` 引入 Nacos 配置
- 网关路由规则定义在 Nacos 的 `gateway.yaml` 中
- 敏感配置（如 DeepSeek API Key）通过环境变量注入：`DEEPSEEK_API_KEY`

## 接口与鉴权

统一网关入口为 `http://localhost:8080`。网关鉴权规则如下：

- **公开路径**：`/auth/**`、`/public/**`（登录、注册等）
- **匿名可读接口**：文章列表/详情/阅读、评论列表/回复列表、公开用户资料（`/me/profile/{userNo}`）
- **WebSocket 连接**：`/dm/socket`（使用一次性 ticket 鉴权）
- **其余接口**：需携带 `token` 请求头

## 注意事项

- `IW-mq-service` 与 `IW-search-service` 目前为基础框架，业务功能尚未实现
- 根目录下的 `hs_err_pid24480.log` 为 JVM 崩溃日志，可忽略或删除
- 各子模块 `pom.xml` 中声明的 `java.version` 为 17，父工程为 18，实际以本地 JDK 版本为准

## 致谢

- 感谢来自[KawaYiLab](https://github.com/KawaYiLab/InterKnot-Web)的前端源码,本项目的前端借鉴了其相关源码
- 本项目由我独自完成,制作不易,能否点个star,感激不尽
