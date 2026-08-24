# Ping-Pong Microservices

基于 Spring WebFlux 的 Ping-Pong 微服务通信系统：Ping Service 定时通过 HTTP 调用 Pong Service，同时向 Kafka 发送消息；Pong Service 消费 Kafka 消息并持久化到 PostgreSQL 和 MongoDB。系统内置多层限流（FileLock 跨进程限流、Hazelcast 分布式限流、Pong 接口 1 RPS 限流），并支持按进程 PID 输出独立日志。

## Architecture

```text
  Ping Service                                    Pong Service
+------------------------+                    +------------------------+
| @Scheduled             |                    | Throttle (1 RPS, 429) |
|   |                    |                    |   |                   |
|   v                    |   HTTP "Hello"     |   v                   |
| FileLock (2 RPS)       | -----------------> | Kafka Consumer        |
|   |                    |                    |   |                   |
|   v                    |                    |   +--> PostgreSQL     |
| Kafka Producer +       |                    |   +--> MongoDB        |
| Logging (per PID)      |                    |   +--> Hazelcast      |
+------------------------+                    +------------------------+
      |                                               ^
      +------------------- Kafka ---------------------+
```

- Ping Service：`@Scheduled` 定时触发，`FileLock` 实现跨进程 2 RPS 限流，通过 Kafka Producer 发送消息，并按 PID 输出独立日志。
- Pong Service：`Throttle` 实现 1 RPS 接口限流（超限返回 429），Kafka Consumer 消费消息后写入 PostgreSQL 与 MongoDB，Hazelcast 提供分布式限流状态。

## Tech Stack

- Spring Boot 3.2
- Spring WebFlux
- Apache Kafka
- PostgreSQL
- MongoDB
- Hazelcast
- Java FileLock
- Spock + Groovy
- JaCoCo
- Logback

## How to Run

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker

### Step 1: Start Infrastructure

首次启动使用 docker compose 创建并启动 Kafka、PostgreSQL、MongoDB：

```bash
docker compose up -d
```

如果容器已经创建，可以直接启动：

```bash
docker start ping-pong-kafka ping-pong-postgres ping-pong-mongodb
```

PostgreSQL 发布到宿主机 `5433`，MongoDB 发布到 `27018`，避免与本机已有服务冲突。

### Step 2: Start Pong Service

```bash
cd pong-service
mvn spring-boot:run
```

Pong Service 监听 `8081` 端口，提供 `POST /pong`。

### Step 3: Start Ping Service

```bash
mvn package -DskipTests
java -jar ping-service/target/ping-service-1.0.0.jar
```

在项目根目录运行，日志文件会生成到 `logs/ping-${PID}.log`。

### Step 4: Multi-Instance Mode

启动 3 个 Ping Service 实例，验证 FileLock 跨进程限流：

```bash
java -jar ping-service/target/ping-service-1.0.0.jar &
java -jar ping-service/target/ping-service-1.0.0.jar &
java -jar ping-service/target/ping-service-1.0.0.jar &
```

启动 2 个 Pong Service 实例，验证 Hazelcast 分布式限流：

```bash
java -jar pong-service/target/pong-service-1.0.0.jar --pong.throttle.distributed=true --server.port=8081 &
java -jar pong-service/target/pong-service-1.0.0.jar --pong.throttle.distributed=true --server.port=8082 &
```

两个 Pong 实例会组成 Hazelcast 集群，共享 `throttle-state` 限流计数。

### Step 5: Run Tests

```bash
mvn test
```

## Git History

每个 commit 代表一个开发里程碑：

| Commit | Milestone |
| --- | --- |
| `22a4c9b` | 初始化项目骨架 |
| `4ca6903` | 实现 Ping / Pong 基础通信 |
| `c6a0dc9` / `85b480a` / `f35c06a` | 增加 Pong 服务限流 |
| `fcac392` | 集成 Kafka 消息队列 |
| `386955e` | 实现 FileLock 跨进程限流 |
| `f1aa866` | 实现双数据库持久化（PostgreSQL + MongoDB） |
| `97b8c5a` / `395f0c1` | 完成 Spock 单元测试（Groovy） |
| `4236c7c` | 实现 ping-service 独立日志（按 PID） |
| `95615ef` | 集成 Hazelcast 分布式限流 |

## Acceptance Checklist

| 验收项 | Step |
| --- | --- |
| Kafka、PostgreSQL、MongoDB 基础设施启动 | Step 1 |
| Pong Service 启动并监听 8081 | Step 2 |
| Pong 接口限流：1 秒内第 2 个请求返回 429 | Step 2 |
| Ping Service 打包并运行 | Step 3 |
| 日志按 PID 写入 `logs/ping-${PID}.log` | Step 3 |
| 3 个 Ping 实例共享 FileLock 2 RPS 限流 | Step 4 |
| 2 个 Pong 实例共享 Hazelcast 分布式限流 | Step 4 |
| `mvn test` 全部通过（Spock + JaCoCo） | Step 5 |
