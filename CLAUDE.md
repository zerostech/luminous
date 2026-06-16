# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Luminous is a **Spring Boot starter framework** (not a runnable app) by ZerosTech. It provides reusable infrastructure abstractions and auto-configured boot starters for building Spring Boot microservices. Java 17, Spring Boot 4.1.0, Spring Cloud 2025.1.2.

## Build Commands

```bash
# Full build (parent POM must be installed first since all modules inherit from it)
mvn clean install

# Build a single module
mvn clean install -pl boot-starter/luminous-web-boot-starter -am

# Run tests (Spock + JUnit 5; surefire includes **/*Test and **/*Spec)
mvn test

# Run a single test class
mvn test -pl basic/common -Dtest=SomeSpec

# Release build (generates sources + javadoc jars)
mvn clean package -Prelease
```

No linting tools are configured (no Checkstyle, SpotBugs, PMD).

## Module Structure

```
luminous-framework (root aggregator)
├── parent/                          # Dependency management (inherits spring-boot-starter-parent:4.1.0)
├── basic/common/                    # Core interfaces & models: ICache, ILock, Ids, IUser, ITracing, IFutureExecutor, Result<T>, exceptions
└── boot-starter/
    ├── luminous-web-boot-starter/   # Web/API auto-config: Lu facade, error handling, interceptors, SpringDoc/Knife4j, thread pools
    ├── luminous-redis-boot-starter/ # Redis auto-config: RedisCache, RedisLock, Snowflake-like RedisIds
    └── luminous-mybatis-boot-stater/ # MyBatis-Plus auto-config: pagination, batch insert, soft delete, audit fill, SQL escape
```

All three boot starters depend on `common`. Note: `luminous-mybatis-boot-stater` has a typo ("stater") in its artifact ID — keep as-is.

## Architecture

**Interface-implementation separation**: `common` defines abstract interfaces; each boot starter provides concrete implementations wired via Spring Boot auto-configuration using `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` (Spring Boot 3+ mechanism).

**Central facade — `Lu`** (web starter): Static singleton holding `Basic` (lock, cache, caches, tracing), `LuConfig`, `ApplicationContext`, app `Info` (name, profile, IP, host, port), and `LuContext` accessor. All access flows through `Lu`.

**Request context — `LuContext`**: Uses `TransmittableThreadLocal` (Alibaba TTL) to propagate request context (id, sourceIP, requestUri, userInfo) across thread boundaries.

**Bean discovery by name**: Cache and Lock implementations are resolved by bean name (e.g., `"redisCache"`, `"redisLock"`) via `applicationContext.getBean()`, selected by `luminous.cache`/`luminous.lock` properties.

**Conditional loading**: `@ConditionalOnExpression` with SpEL toggles features based on `luminous.cache`, `luminous.lock`, `luminous.ids` properties.

## Key Conventions

- **Jakarta EE 10**: All imports use `jakarta.*` namespace (`jakarta.servlet`, `jakarta.annotation`, `jakarta.validation`). Never use `javax.*` for Java EE APIs.
- **Unified API response**: All endpoints return `Result<T>` (success/code/msg/data/traceId). Success code is `"200"`.
- **Business error codes**: 5-char codes per Alibaba Java conventions — `A*` = input, `B*` = processing, `C*` = third-party, `E*` = unknown. Use `LuminousBizException` with `ExceptionCode`.
- **HTTP error codes**: 400 (bad request), 401 (unauth), 403 (no authority), 404 (not found), 405 (param pattern), 406 (param not found), 500 (server error).
- **Base entity**: `BasicModel<T>` provides id, creatorId/Name, modifierId/Name, createTime, modifyTime, deleted (soft delete via `@TableLogic`).
- **Auto-fill audit fields**: `MybatisPlusMetaHandler` fills creator/modifier/timestamps from `LuContext`.
- **Distributed IDs**: Snowflake-like with 6-bit serviceId + 10-bit workerId + 7-bit sequence. Worker IDs allocated via Redis ZSet heartbeat.
- **Distributed locks**: Redis SETNX with key prefix `luminous:cluster:lock:`.
- **Redis serialization**: All `RedisTemplate<String, String>` with String serializers — no JSON serialization.
- **Configuration prefix**: All properties under `luminous.*` (e.g., `luminous.basic.doc.enabled`, `luminous.mybatis.blockAttackEnabled`).
- **ErrorController**: Implements `org.springframework.boot.webmvc.error.ErrorController` interface directly. Uses injected `ObjectMapper` for JSON operations. `ErrorAttributes` is at `org.springframework.boot.webmvc.error.ErrorAttributes`, `ErrorMvcAutoConfiguration` at `org.springframework.boot.webmvc.autoconfigure.error.ErrorMvcAutoConfiguration`. `getErrorAttributes()` accepts `WebRequest` (not `HttpServletRequest`) — wrap with `new ServletWebRequest(request)`.
- **Auto-configuration registration**: Uses `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` files (not legacy `spring.factories`).
- **Spring Boot 4.1 package changes**: `spring-boot-starter-aop` → `spring-boot-starter-aspectj`; `ErrorAttributes`/`ErrorController` moved from `org.springframework.boot.web.servlet.error` to `org.springframework.boot.webmvc.error`.
- **API documentation**: SpringDoc OpenAPI 2.x (`springdoc-openapi-starter-webmvc-ui`) + Knife4j 4.x (`knife4j-openapi3-jakarta-spring-boot-starter`). `GroupedOpenApi` is at `org.springdoc.core.models.GroupedOpenApi`.

## Testing

- Spock 2.3 + JUnit 5 configured in parent POM
- Test file patterns: `**/*Test`, `**/*Spec`
- No tests currently exist in the codebase
