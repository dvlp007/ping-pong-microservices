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
| `ping-service` | 定时通过 WebClient 调用 `pong-service`，并每秒向 `ping-pong-topic` 发送 Kafka 消息 |
| `pong-service` | 提供 POST `/pong` 接口，回应 "World" |

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
ping-service (8080)
  --POST "Hello" /pong--> pong-service (8081)
  <--"World"------------ pong-service
  日志: SUCCESS | Sent Hello, Pong responded: World
```

## 测试

项目使用 Spock 编写单元测试与集成测试：

```bash
mvn test
```

## Kafka 消息队列

- `ping-service` 每秒通过 `KafkaProducerService` 自动向 `ping-pong-topic` 发送消息
- `pong-service` 通过 `KafkaConsumerService` 消费该 Topic，并调用 `PongService` 处理
- Topic：`ping-pong-topic`，消费组：`pong-group`

启动 Kafka（KRaft 模式，无需 Zookeeper）：

```bash
docker compose up -d
```

## 目录结构

```text
ping-pong-microservices/
├── docker-compose.yml       # Kafka 单节点（KRaft 模式）
├── pom.xml                 # 父 POM，声明模块与公共依赖
├── ping-service/           # Ping 服务
├── pong-service/           # Pong 服务
└── README.md
```

