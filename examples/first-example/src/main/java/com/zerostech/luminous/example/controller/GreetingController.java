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

import com.zerostech.luminous.common.LuContext;
import com.zerostech.luminous.common.response.Result;
import com.zerostech.luminous.web.Lu;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Greeting controller — demonstrates basic framework features:
 * <ul>
 *   <li>{@code Result<T>} unified response</li>
 *   <li>{@code Lu.context()} request context access</li>
 *   <li>{@code Lu.Info} application metadata</li>
 * </ul>
 */
@Tag(name = "Greeting", description = "Simple greeting endpoints")
@RestController
@RequestMapping("/api/greeting")
public class GreetingController {

    @Operation(summary = "Say hello", description = "Returns a greeting with request context info from Lu facade")
    @GetMapping("/hello")
    public Result<Map<String, Object>> hello(@RequestParam(defaultValue = "World") String name) {
        // Demonstrate Lu.context() — request context with id, sourceIP, requestUri
        LuContext ctx = Lu.context();

        Map<String, Object> data = new HashMap<>();
        data.put("message", "Hello, " + name + "!");
        data.put("requestId", ctx.getId());
        data.put("sourceIP", ctx.getSourceIP());
        data.put("requestUri", ctx.getRequestUri());

        // Demonstrate Lu.Info — application metadata
        data.put("appName", Lu.Info.name);
        data.put("appProfile", Lu.Info.profile);
        data.put("appInstance", Lu.Info.instance);

        return Result.success(data);
    }
}
