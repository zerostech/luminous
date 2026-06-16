/*
 *
 *  * Copyright 2024. the original author or authors
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


package com.zerostech.luminous.web.doc;

import com.zerostech.luminous.web.LuConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 2022/9/9.
 *
 * @author 迹_Jason
 */
@Configuration
@ConditionalOnProperty(prefix = "luminous.basic.doc", name = "enabled", havingValue = "true")
public class DocAutoConfiguration {

    @Autowired
    private LuConfig config;

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder().group(config.getBasic().getDoc().getGroup())
                .pathsToMatch(config.getBasic().getDoc().getMatchPath())
                .packagesToExclude(config.getBasic().getDoc().getExcludeUrls())
                .packagesToScan(config.getBasic().getDoc().getDocScannerPackage()).build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(config.getBasic().getDoc().getTitle())
                        .version(config.getBasic().getDoc().getVersion())
                        .description(config.getBasic().getDoc().getDescription())
                        .contact(new Contact().email(config.getBasic().getDoc().getContractEmail()).name(config.getBasic().getDoc().getContractName()))
                        .termsOfService(config.getBasic().getDoc().getServiceUrl())
                        .license(new License().name(config.getBasic().getDoc().getLicense())
                                .url(config.getBasic().getDoc().getLicenseUrl())));
    }
}
