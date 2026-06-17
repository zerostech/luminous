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

import cn.hutool.core.util.IdUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.zerostech.luminous.common.tracing.ITracing;

/**
 * 基于UUID的简单链路跟踪实现.
 * <p>
 * 使用 {@link TransmittableThreadLocal} 存储traceId，支持跨线程传播。
 * 使用 {@link IdUtil#simpleUUID()} 生成32位无连字符的UUID作为traceId。
 * </p>
 *
 * @author 迹_Jason
 */
public class SimpleTracing implements ITracing {

    private static final TransmittableThreadLocal<String> TRACE_ID = new TransmittableThreadLocal<>();

    @Override
    public String getTraceId() {
        return TRACE_ID.get();
    }

    @Override
    public void setTraceId(String traceId) {
        TRACE_ID.set(traceId);
    }

    @Override
    public String getIfAbsentTraceId() {
        String traceId = TRACE_ID.get();
        if (traceId == null) {
            traceId = generateTraceId();
            TRACE_ID.set(traceId);
        }
        return traceId;
    }

    @Override
    public void remove() {
        TRACE_ID.remove();
    }

    @Override
    public String generateTraceId() {
        return IdUtil.simpleUUID();
    }
}
