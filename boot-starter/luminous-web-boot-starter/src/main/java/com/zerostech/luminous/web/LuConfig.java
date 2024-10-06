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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created on 2024/5/26.
 *
 * @author 迹_Jason
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "luminous.web")
public class LuConfig {

    private Basic basic = new Basic();
    @Builder.Default
    private String cache = "redis";
    @Builder.Default
    private String lock = "redis";

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Basic {
        private Doc doc = new Doc();
    }
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Doc {
        @Builder.Default
        private Boolean enabled = false;
        @Builder.Default
        private String group = "Swagger";
        private String title;
        private String description;
        private String serviceUrl;
        private String contractName;
        private String contractEmail;
        private String version;
        @Builder.Default
        private String[] docScannerPackage = {"com.zerostech"};
        @Builder.Default
        private String[] matchPath = {"/**"};
        private String license;
        private String licenseUrl;
        private String[] excludeUrls = {""};
        @Builder.Default
        private Security security = new Security();

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Security {
            @Builder.Default
            private Boolean enabled = true;
            @Builder.Default
            private String username = "admin";
            @Builder.Default
            private String password = "Admin@123";
        }
    }
}
