# Premium Finance Platform Backend Scaffold

## Stack
- Java 21
- Spring Boot 3.x
- Maven multi-module
- MySQL + MyBatis-Plus
- Redis + Redisson
- RocketMQ (switchable)
- XXL-JOB (switchable)
- JWT + RBAC

## Modules
- `platform-app`: bootstraps and assembles all modules
- `platform-common`: API response, exception handling
- `platform-infra`: MySQL/Redis/Redisson base capability
- `platform-rbac`: auth + user-role-permission API
- `platform-infra-job`: XXL-JOB integration (conditional)
- `platform-infra-mq`: RocketMQ integration (conditional)

## CI quality gate (merge criteria)
以下门禁必须全部通过，PR 才可合并：
- 编译与单测：`mvn -B -ntp clean verify`
- `platform-app` 启动冒烟（`ci` profile）：`mvn -B -ntp -pl platform-app -am -Dspring.profiles.active=ci -Dtest=PlatformApplicationSmokeTest -Dsurefire.failIfNoSpecifiedTests=false test`

CI 中 `platform-app` 使用 `ci` profile 进行最小依赖启动校验：
- MySQL 使用 H2 内存数据库（MySQL 兼容模式）
- Redis 连接改为 CI 可控配置，并关闭 Redisson 自动装配，避免依赖本地 Redis 进程

## Local bootstrap
1. Create MySQL database `premium_finance_platform`.
2. Run SQL scripts in order:
   - `sql/schema.sql`
   - `sql/seed-rbac.sql`
3. Update `platform-app/src/main/resources/application.yml` datasource and redis config.
4. Run:

```bash
mvn clean test
mvn -pl platform-app spring-boot:run
```

## Default admin
- username: `admin`
- password: `admin123`

## API docs
- Swagger: `http://localhost:8080/swagger-ui/index.html`

## Middleware switches
- `platform.job.xxl.enabled=false` (default)
- `platform.mq.rocket.enabled=false` (default)
