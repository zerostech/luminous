/*
 *
 *  * Copyright 2022. the original author or authors
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 *
 */

package com.zerostech.luminous.web.future;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author hyz
 * @since 2023/7/20
 */
@Data
@Component
@ConfigurationProperties(prefix = "luminous.future")
public class FutureExecutorProperties {

    private Map<String, ExecutorConfig> executors;


    @Data
    public static class ExecutorConfig {

        private Integer corePoolSize = 5;

        private Integer maxPoolSize = 10;
        private Long keepAliveTime = 5L;
        private TimeUnit timeUnit = TimeUnit.MINUTES;
        private Class<BlockingQueue<Runnable>> blockingQueue;
        private Class<? extends ThreadFactory> threadFactory;
        private Class<? extends RejectedExecutionHandler> handler = ThreadPoolExecutor.AbortPolicy.class;
    }
}
