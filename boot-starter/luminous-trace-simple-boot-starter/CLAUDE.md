# CLAUDE.md — luminous-trace-simple-boot-starter

## Module Overview

UUID-based simple tracing implementation of `ITracing`. Provides distributed traceId propagation across HTTP service boundaries via `X-Trace-Id` header. This is one trace mode pluggable via `luminous.web.trace` property — future implementations (e.g., SkyWalking) can be added as separate boot starters.

## Build

```bash
mvn clean install -pl boot-starter/luminous-trace-simple-boot-starter -am
```

## File Structure

```
src/main/java/com/zerostech/luminous/trace/simple/
  SimpleTracing.java                 # ITracing impl: TTL + UUID (IdUtil.simpleUUID)
  TraceSimpleProperties.java         # @ConfigurationProperties(prefix="luminous.trace.simple")
  TraceFilter.java                   # Servlet Filter: extracts/generates traceId per request
  TraceRestTemplateInterceptor.java  # ClientHttpRequestInterceptor: propagates traceId on outgoing calls
  TraceSimpleAutoConfiguration.java  # Auto-config + inner TraceRestTemplatePostProcessor
src/main/resources/META-INF/spring/
  org.springframework.boot.autoconfigure.AutoConfiguration.imports
src/main/resources/META-INF/
  additional-spring-configuration-metadata.json
```

## Architecture

### Mode Switching (follows cache/lock pattern)

- `LuConfig.trace` (default `"simple"`) controls which tracing implementation is loaded
- `Lu.init()` discovers beans by name: `trace + "Tracing"` → `"simpleTracing"`
- `@ConditionalOnExpression("'${luminous.trace:simple}'.equals('simple')")` activates this starter only when mode is `simple`
- Bean **must** be named `simpleTracing` (method name = bean name) for discovery to work
- To add SkyWalking: create `luminous-trace-skywalking-boot-starter`, name bean `skywalkingTracing`, use `@ConditionalOnExpression("'${luminous.trace}'.equals('skywalking')")`

### Request Flow

1. `TraceFilter` (order: `HIGHEST_PRECEDENCE + 10`, url: `/*`) reads `X-Trace-Id` header
   - Header present → `tracing.setTraceId(value)` (preserves upstream traceId)
   - Header absent → `tracing.getIfAbsentTraceId()` (auto-generates UUID)
2. Application code accesses traceId via `Lu.basic.tracing.getTraceId()` / `Lu.isPresentTracing()`
3. Error responses (`ExceptionControllerAdvice`, `ErrorController`) include traceId via `Lu.isPresentTracing() ? Lu.basic.tracing.getTraceId() : null`
4. Outgoing `RestTemplate` calls: `TraceRestTemplatePostProcessor` (a `BeanPostProcessor`) auto-adds `TraceRestTemplateInterceptor` to all `RestTemplate` beans, which sets `X-Trace-Id` header
5. After response: `TraceFilter.finally` calls `tracing.remove()` to clear TTL

### Thread Propagation

`SimpleTracing` uses `TransmittableThreadLocal` (same as `LuContext`). TraceId propagates to child threads when TTL wrapper decorators (`TtlRunnable`, `TtlCallable`) or TTL executor service wrappers are used. Standard `ExecutorService` without TTL wrapping will NOT propagate traceId.

## Configuration Properties

| Property | Default | Description |
|----------|---------|-------------|
| `luminous.web.trace` | `simple` | Trace mode selection (in `LuConfig`, web-boot-starter) |
| `luminous.trace.simple.enabled` | `true` | Enable/disable simple tracing |
| `luminous.trace.simple.header-name` | `X-Trace-Id` | HTTP header name for traceId propagation |

## Key Conventions

- **No dependency on `luminous-web-boot-starter`**: This module only depends on `common` and `spring-boot-starter-web`. The `Lu.basic.tracing` assignment is handled by `Lu.init()` bean discovery, not by direct reference.
- **`BeanPostProcessor` instead of `RestTemplateCustomizer`**: Spring Boot 4.1.0 removed `RestTemplateCustomizer`. Use `BeanPostProcessor` to add interceptors to `RestTemplate` beans.
- **Jakarta EE 10**: All servlet imports use `jakarta.servlet.*`.
- **Filter registration**: Uses `FilterRegistrationBean` (not `@Component` or `WebMvcConfigurer`), consistent with Spring Boot starter conventions.
- **Auto-configuration registration**: Uses `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` (not legacy `spring.factories`).
