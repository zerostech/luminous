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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 说明： id生成器配置
 *
 * @author 迹_Jason
 * @date 2024/10/06
 */
@Component
@ConfigurationProperties(prefix = "luminous.basic.ids")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdsProperties {

    @Builder.Default
    private Boolean enabled = false;

    // 工作机器ID
    private Integer workId;

    // 工作机器id所占用的长度，最大10，默认10
    private Integer workIdLength = 10;

    // 刷新存活状态的间隔时间
    private Integer refreshAliveInterval = 5;
}
