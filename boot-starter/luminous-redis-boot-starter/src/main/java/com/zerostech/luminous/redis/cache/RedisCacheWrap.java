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

package com.zerostech.luminous.redis.cache;

import com.zerostech.luminous.common.cache.ICache;
import com.zerostech.luminous.common.cache.ICacheWrap;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 迹_Jason
 * @date 2024/10/06
 */

public class RedisCacheWrap implements ICacheWrap {
    private static final ConcurrentHashMap<String, ICache> CACHE_CONTAINER = new ConcurrentHashMap<>();

    public RedisCacheWrap(Map<String, RedisTemplate<String, String>> redisTemplates) {
        redisTemplates.forEach((k, v) ->
                CACHE_CONTAINER.put(k, new RedisCache(v)));
    }

    @Override
    public ICache instance(String key) {
        return CACHE_CONTAINER.get(key);
    }

    @Override
    public boolean exist(String key) {
        return CACHE_CONTAINER.contains(key);
    }
}
