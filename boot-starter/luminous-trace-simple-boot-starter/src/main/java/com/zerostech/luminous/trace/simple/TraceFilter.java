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
 *  *
 *
 */

package com.zerostech.luminous.trace.simple;

import com.zerostech.luminous.common.tracing.ITracing;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 链路跟踪过滤器.
 * <p>
 * 从HTTP请求头中提取traceId，如果不存在则自动生成。
 * 请求处理完成后自动清理ThreadLocal，防止内存泄漏。
 * </p>
 *
 * @author 迹_Jason
 */
@Slf4j
@RequiredArgsConstructor
public class TraceFilter implements Filter {

    private final ITracing tracing;
    private final String headerName;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String traceId = httpRequest.getHeader(headerName);
        if (traceId != null && !traceId.isEmpty()) {
            tracing.setTraceId(traceId);
        } else {
            tracing.getIfAbsentTraceId();
        }
        try {
            chain.doFilter(request, response);
        } finally {
            tracing.remove();
        }
    }
}
