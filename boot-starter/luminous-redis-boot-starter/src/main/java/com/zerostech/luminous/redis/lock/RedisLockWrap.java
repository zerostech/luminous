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

package com.zerostech.luminous.redis.lock;


import com.zerostech.luminous.common.lock.ILock;
import com.zerostech.luminous.common.lock.ILockWrap;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 迹_Jason
 * @date 2024/10/06
 */

public class RedisLockWrap implements ILockWrap {


    private static final ConcurrentHashMap<String, ILock> LOCK_CONTAINER = new ConcurrentHashMap<>();

    private RedisTemplate<String, String> redisTemplate;

    /**
     * Instantiates a new Redis cluster lock wrap.
     *
     * @param redisTemplate the redis template
     */
    public RedisLockWrap(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public ILock instance(String key) {
        LOCK_CONTAINER.putIfAbsent(key, new RedisLock(key, redisTemplate));
        return LOCK_CONTAINER.get(key);
    }

}
