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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * RestTemplate链路跟踪拦截器.
 * <p>
 * 在RestTemplate发起的HTTP请求中自动传递traceId，
 * 实现跨服务的链路跟踪。
 * </p>
 *
 * @author 迹_Jason
 */
@RequiredArgsConstructor
public class TraceRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    private final ITracing tracing;
    private final String headerName;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                         ClientHttpRequestExecution execution) throws IOException {
        String traceId = tracing.getTraceId();
        if (traceId != null && !traceId.isEmpty()) {
            request.getHeaders().set(headerName, traceId);
        }
        return execution.execute(request, body);
    }
}
