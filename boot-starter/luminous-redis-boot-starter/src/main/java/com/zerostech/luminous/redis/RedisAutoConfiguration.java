/*
 *
 *  *
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
 *  *
 *
 */

package com.zerostech.luminous.redis;

import com.zerostech.luminous.redis.cache.RedisCacheWrap;
import com.zerostech.luminous.redis.ids.IdsProperties;
import com.zerostech.luminous.redis.ids.RedisIds;
import com.zerostech.luminous.redis.ids.WorkerIdComponent;
import com.zerostech.luminous.redis.lock.RedisLockWrap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @description
 *
 * @author 迹_Jason
 *
 * @create 2024-10-06 18:21
 **/
@Slf4j
@Configuration
@ConditionalOnClass(RedisTemplate.class)
@EnableConfigurationProperties(value = {IdsProperties.class})
@ConditionalOnExpression("#{'${luminous.cache}'=='redis' " + "|| '${luminous.ids}'=='redis'" + "|| '${luminous.lock}'=='redis'}")
public class RedisAutoConfiguration {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private IdsProperties idsProperties;

    private static final Map<String, RedisTemplate<String, String>> REDIS_TEMPLATES = new HashMap<>();

    @PostConstruct
    public void init() {
        log.info("Load Auto Configuration : {}", this.getClass().getName());
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer);
        REDIS_TEMPLATES.put("", redisTemplate);
    }

    /**
     * Redis cluster cache redis cluster cache.
     *
     * @return the redis cluster cache
     */
    @Bean
    @ConditionalOnExpression("'${zebra.cache}'.equals('redis')")
    public RedisCacheWrap redisCache() {
        return new RedisCacheWrap(REDIS_TEMPLATES);
    }

    /**
     * Redis cluster lock redis cluster lock wrap.
     *
     * @param redisTemplate the redis template
     * @return the redis cluster lock wrap
     */
    @Bean
    @ConditionalOnExpression("'${zebra.lock}'.equals('redis')")
    public RedisLockWrap redisLock(RedisTemplate<String, String> redisTemplate) {
        return new RedisLockWrap(redisTemplate);
    }

    @Bean
    @ConditionalOnExpression("'${luminous.ids}'.equals('redis') and ${luminous.basic.ids.enabled:true}")
    public RedisIds redisIds() {
        return new RedisIds(idsProperties);
    }

    @Bean
    @ConditionalOnExpression("'${luminous.ids}'.equals('redis') and ${luminous.basic.ids.enabled:true}")
    public ScheduledThreadPoolExecutor scheduledThreadPoolExecutor() {
        return new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());
    }

    @Bean
    @ConditionalOnExpression("'${luminous.ids}'.equals('redis') and ${luminous.basic.ids.enabled:true}")
    public WorkerIdComponent workerIdComponent() {
        return new WorkerIdComponent();
    }
}
