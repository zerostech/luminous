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
package com.zerostech.luminous.common;

import cn.hutool.core.util.IdUtil;
import com.zerostech.luminous.common.cache.ICache;
import com.zerostech.luminous.common.cache.ICacheWrap;
import com.zerostech.luminous.common.lock.ILockWrap;

/**
 * Created on 2024/5/26.
 *
 * @author 迹_Jason
 */

public class Basic {
    /**
     * 实例Id.
     */
    public static String instanceId = IdUtil.simpleUUID();
    private static String applicationName = "_default";
    /**
     * 分布式锁服务.
     */
    public ILockWrap lock;
    /**
     * 多实例缓存服务.
     */
    public ICacheWrap caches;
    /**
     * 默认缓存服务.
     */
    public ICache cache;


    /**
     * 初始化.
     *
     * @param appName 应用名，可用于HA的存储配置
     * @param instId  实例Id，全局唯一，用于区别不同实例
     */
    public static void init(String appName, String instId) {
        applicationName = appName;
        instanceId = instId;
    }
}
