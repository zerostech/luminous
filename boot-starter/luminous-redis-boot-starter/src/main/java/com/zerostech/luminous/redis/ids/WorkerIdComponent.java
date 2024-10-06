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
package com.zerostech.luminous.redis.ids;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 说明：
 *
 * @author 迹_Jason
 * @date 2024/10/06
 */
@Slf4j
public class WorkerIdComponent {

    @Autowired
    private IdsProperties idsProperties;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    /**
     * id生成器workerId对应key
     */
    public static final String ID_GEN_WORKER_ID_KEY = "ID_GEN_WORKER_ID";

    /**
     * id生成器workerId池
     */
    public static final String ID_GEN_WORKER_ID_POOL_KEY = "ID_GEN_WORKER_ID_POOL";

    /**
     * 获取workerId
     */
    @PostConstruct
    public void getWorkerId() {
        try {
            log.info("Get workerId [START]");
            Long workerId = redisTemplate.opsForValue().increment(ID_GEN_WORKER_ID_KEY);
            if (Objects.isNull(workerId)) {
                return;
            }
            idsProperties.setWorkId(workerId.intValue());
            if (workerId > 1L << idsProperties.getWorkIdLength()) {
                long endScore = LocalDateTime.now().minusMinutes(idsProperties.getRefreshAliveInterval())
                        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                Set<String> set = redisTemplate.opsForZSet().rangeByScore(ID_GEN_WORKER_ID_POOL_KEY, 0, endScore, 0, 1);
                if (!CollectionUtils.isEmpty(set)) {
                    set.stream().findFirst().ifPresent(s -> idsProperties.setWorkId(Integer.parseInt(s)));
                } else {
                    idsProperties.setWorkId(0);
                }
            }
            Date startTimeStamp = new Date();
            log.info("Get workerId [END]  workerId:{}", idsProperties.getWorkId());
            redisTemplate.opsForZSet().add(ID_GEN_WORKER_ID_POOL_KEY,
                    String.valueOf(idsProperties.getWorkId()), startTimeStamp.getTime());
        } catch (Exception e) {
            log.error("Get workerId error! message:{}", e.getMessage(), e);
        }
    }

    /**
     * 定期更新workerId，避免失效
     */
    @PostConstruct
    public void updateWorkerId() {
        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
            log.info("Flush workerId [START]");
            if (Objects.nonNull(idsProperties.getWorkId())) {
                log.info("Flush workerId! The workerId isn't null workerId:{}", idsProperties.getWorkId());
                redisTemplate.opsForZSet().add(ID_GEN_WORKER_ID_POOL_KEY,
                        String.valueOf(idsProperties.getWorkId()), new Date().getTime());
            }
            log.info("Flush workerId [END]");
        }, idsProperties.getRefreshAliveInterval(), idsProperties.getRefreshAliveInterval(), TimeUnit.MINUTES);
    }

}
