# Ping-Pong Microservices

基于 Spring Boot 3 与 Spring WebFlux 的微服务示例项目，演示两个服务之间简单的请求-响应（Ping / Pong）调用。

## 技术栈

- Java 17
- Spring Boot 3.2.0
- Spring WebFlux（响应式 Web）
- Maven 多模块构建
- Spock + Groovy（测试）

## 模块结构

| 模块 | 说明 |
| --- | --- |
| `ping-service` | 提供 Ping 接口，并调用 `pong-service` 获取响应 |
| `pong-service` | 提供 Pong 接口，回应来自 `ping-service` 的请求 |

父 POM 通过 `modules` 聚合子模块，统一管理公共依赖版本与 Java 17 配置；子模块按需声明自己的依赖。

## 快速开始

环境要求：

- JDK 17+
- Maven 3.9+

构建项目：

```bash
mvn clean install
```

启动服务（在各自模块目录下执行）：

```bash
cd ping-service
mvn spring-boot:run
```

```bash
cd pong-service
mvn spring-boot:run
```

## 调用流程

```text
Client
  -> GET /ping        (ping-service)
  -> GET /pong        (pong-service)
  <- Pong response
```

## 测试

项目使用 Spock 编写单元测试与集成测试：

```bash
mvn test
```

## 目录结构

```text
ping-pong-microservices/
├── pom.xml                 # 父 POM，声明模块与公共依赖
├── ping-service/           # Ping 服务
├── pong-service/           # Pong 服务
└── README.md
```

## 后续计划

- 完成两个服务模块的源码与启动类
- 通过 WebClient 实现 Ping -> Pong 的服务间调用
- 补充服务健康检查与基础配置
