/*
 *
 *  *  Copyright 2024. the original author or authors
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 *
 */

package com.zerostech.luminous.example.controller;

import com.zerostech.luminous.common.exception.LuminousBizException;
import com.zerostech.luminous.common.future.IFutureExecutor;
import com.zerostech.luminous.common.lock.ILock;
import com.zerostech.luminous.common.response.Result;
import com.zerostech.luminous.web.Lu;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Demo controller — demonstrates advanced framework features:
 * <ul>
 *   <li>{@code IFutureExecutor} — async parallel processing</li>
 *   <li>{@code Lu.basic.cache} — Redis cache operations</li>
 *   <li>{@code Lu.basic.lock} — distributed lock operations</li>
 *   <li>{@code LuminousBizException} — all 4 error code categories (A/B/C/E)</li>
 *   <li>{@code Lu.Info} — application metadata and feature detection</li>
 * </ul>
 */
@Slf4j
@Tag(name = "Demo", description = "Framework feature demonstrations")
@RestController
@RequestMapping("/api/demo")
@RequiredArgsConstructor
public class DemoController {

    private final IFutureExecutor futureExecutor;

    @Operation(summary = "Async parallel processing", description = "Uses IFutureExecutor to run 3 tasks in parallel and collect results")
    @GetMapping("/async")
    public Result<Map<String, Object>> asyncDemo() {
        Map<String, Supplier<?>> tasks = new LinkedHashMap<>();
        tasks.put("task1", () -> {
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            return "result-from-task1";
        });
        tasks.put("task2", () -> {
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            return "result-from-task2";
        });
        tasks.put("task3", () -> {
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            return "result-from-task3";
        });

        Map<String, ?> results = futureExecutor.execute(tasks);
        return Result.success(new HashMap<>(results));
    }

    @Operation(summary = "Cache operations", description = "Demonstrates cache get/set with TTL via Lu.basic.cache (requires Redis)")
    @GetMapping("/cache")
    public Result<Map<String, Object>> cacheDemo(@RequestParam String key) {
        Map<String, Object> data = new HashMap<>();

        if (Lu.isPresentCache()) {
            String value = Lu.basic.cache.get(key);
            data.put("exists", value != null);
            if (value == null) {
                String cachedValue = "cached-value-" + System.currentTimeMillis();
                Lu.basic.cache.setex(key, cachedValue, 60);
                data.put("action", "SET");
                data.put("value", cachedValue);
            } else {
                data.put("action", "GET");
                data.put("value", value);
            }
            data.put("ttl", Lu.basic.cache.ttl(key));
        } else {
            data.put("message", "Cache not available — start with dev profile to enable Redis");
        }

        return Result.success(data);
    }

    @Operation(summary = "Distributed lock", description = "Demonstrates lock acquire/release via Lu.basic.lock (requires Redis)")
    @GetMapping("/lock")
    public Result<Map<String, Object>> lockDemo(@RequestParam String resource) {
        Map<String, Object> data = new HashMap<>();

        if (Lu.isPresentLock()) {
            try {
                ILock lock = Lu.basic.lock.instance(resource);
                Boolean acquired = lock.tryLock(3000, 5000);
                data.put("acquired", acquired);
                if (Boolean.TRUE.equals(acquired)) {
                    data.put("message", "Lock acquired successfully");
                    lock.unLock();
                    data.put("released", true);
                } else {
                    data.put("message", "Failed to acquire lock within timeout");
                }
            } catch (Exception e) {
                data.put("error", e.getMessage());
            }
        } else {
            data.put("message", "Lock not available — start with dev profile to enable Redis");
        }

        return Result.success(data);
    }

    @Operation(summary = "Trigger param error (A-code)", description = "Throws LuminousBizException with code A0001")
    @GetMapping("/error/param")
    public Result<Void> paramError() {
        LuminousBizException.paramError("Invalid parameter: {} is required", "email");
        return Result.success(); // unreachable — exception is thrown above
    }

    @Operation(summary = "Trigger process error (B-code)", description = "Throws LuminousBizException with code B0001")
    @GetMapping("/error/process")
    public Result<Void> processError() {
        LuminousBizException.processError("Order processing failed: {}", "inventory check timeout");
        return Result.success(); // unreachable
    }

    @Operation(summary = "Trigger third-party error (C-code)", description = "Throws LuminousBizException with code C0001")
    @GetMapping("/error/third")
    public Result<Void> thirdError() {
        LuminousBizException.thirdError("Payment gateway error: {}", "connection refused");
        return Result.success(); // unreachable
    }

    @Operation(summary = "Trigger unknown error (E-code)", description = "Throws LuminousBizException with code E9999")
    @GetMapping("/error/unknown")
    public Result<Void> unknownError() {
        LuminousBizException.unknowError("Unexpected system failure: {}", "null pointer in module X");
        return Result.success(); // unreachable
    }

    @Operation(summary = "Application info", description = "Shows Lu.Info metadata and feature availability")
    @GetMapping("/app-info")
    public Result<Map<String, Object>> appInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("name", Lu.Info.name);
        info.put("profile", Lu.Info.profile);
        info.put("ip", Lu.Info.ip);
        info.put("host", Lu.Info.host);
        info.put("webPort", Lu.Info.webPort);
        info.put("instance", Lu.Info.instance);
        info.put("cacheAvailable", Lu.isPresentCache());
        info.put("lockAvailable", Lu.isPresentLock());
        info.put("tracingAvailable", Lu.isPresentTracing());
        return Result.success(info);
    }
}
