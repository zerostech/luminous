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
package com.zerostech.luminous.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 2024/5/26.
 *
 * @author 迹_Jason
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(LuConfig.class)
public class LuStartUp {
    @Autowired
    private LuConfig luConfig;

//    @Bean
//    public RestTemplate restTemplate() {
//        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.getMessageConverters().add(new ObjectToUrlEncodedConverter(new ObjectMapper()));
//        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
//        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
//        return restTemplate;
//    }
}
