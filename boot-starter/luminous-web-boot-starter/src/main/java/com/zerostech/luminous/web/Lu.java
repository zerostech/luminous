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

import cn.hutool.core.net.NetUtil;
import com.zerostech.luminous.common.Basic;
import com.zerostech.luminous.common.cache.ICacheWrap;
import com.zerostech.luminous.common.lock.ILockWrap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created on 2024/5/26.
 *
 * @author 迹_Jason
 */
@Slf4j
@Configuration
public class Lu {
    public static Basic basic = new Basic();
    @Value("${spring.application.name:please-setting-this}")
    private String applicationName;
    @Value(("${spring.profiles.active:default}"))
    private String profile;
    @Value("${server.port:-1}")
    private int serverPort;

    @Autowired
    private ApplicationContext injectApplicationContext;
    @Autowired
    private LuConfig injectZConfig;
    /**
     * The constant luConfig.
     */
    public static LuConfig luConfig;
    /**
     * The constant applicationContext.
     */
    public static ApplicationContext applicationContext;


    @PostConstruct
    private void init() {
        log.info("Load Auto Configuration : {}", this.getClass().getName());

        Lu.luConfig = injectZConfig;
        Lu.applicationContext = injectApplicationContext;
        Info.name = applicationName;
        Info.profile = profile;
        Info.webPort = serverPort;
        Info.instance = applicationName + "@" + Info.profile + "@" + Info.ip + ":" + serverPort;
        Basic.init(Info.name, Info.instance);

        if (Lu.applicationContext.containsBean(injectZConfig.getCache() + "Cache")) {
            Lu.basic.caches = (ICacheWrap) Lu.applicationContext.getBean(injectZConfig.getCache() + "Cache");
            Lu.basic.cache = Lu.basic.caches.instance();
        }
        if (Lu.applicationContext.containsBean(injectZConfig.getLock() + "Lock")) {
            Lu.basic.lock = (ILockWrap) Lu.applicationContext.getBean(injectZConfig.getLock() + "Lock");
        }
    }

    public static boolean isPresentLock() {
        return Lu.basic.lock != null;
    }

    public static boolean isPresentCache() {
        return Lu.basic.cache != null;
    }

    /**
     * 获取请求上下文信息.
     *
     * @return 请求上下文信息 Z context
     */
    public static LuContext context() {
        return LuContext.getContext();
    }

    /**
     * 组件基础信息.
     */
    public static class Info {
        /**
         * 应用名称.
         */
        public static String name;
        /**
         * 应用环境.
         */
        public static String profile;
        /**
         * 应用主机IP.
         */
        public static String ip;
        /**
         * 应用主机名.
         */
        public static String host;
        /**
         * 应用主机Web端口.
         */
        public static int webPort;
        /**
         * 应用实例，各组件实例唯一.
         */
        public static String instance;

        static {
            InetAddress inetAddress = NetUtil.getLocalhost();
            ip = inetAddress.getHostAddress();
            host = inetAddress.getHostName();
        }

    }

    /**
     * 常用工具.
     */
    public static class Util {

        private static final ExecutorService executorService = Executors.newCachedThreadPool();

        /**
         * 获取真实IP.
         *
         * @param request 请求信息
         * @return 真实的IP real ip
         */
        public static String getRealIP(HttpServletRequest request) {
            Map<String, String> requestHeader = new HashMap<>();
            Enumeration<String> header = request.getHeaderNames();
            while (header.hasMoreElements()) {
                String key = header.nextElement();
                requestHeader.put(key, request.getHeader(key));
            }
            return getRealIP(requestHeader, request.getRemoteAddr());
        }

        /**
         * 获取真实IP.
         *
         * @param requestHeader     请求头信息
         * @param defaultRemoteAddr 缺省的IP地址
         * @return 真实的IP real ip
         */
        public static String getRealIP(Map<String, String> requestHeader, String defaultRemoteAddr) {
            Map<String, String> formattedRequestHeader = new HashMap<>();
            requestHeader.forEach((k, v) -> formattedRequestHeader.put(k.toLowerCase(), v));
            if (formattedRequestHeader.containsKey("x-forwarded-for")
                    && formattedRequestHeader.get("x-forwarded-for") != null
                    && !formattedRequestHeader.get("x-forwarded-for").isEmpty()) {
                return formattedRequestHeader.get("x-forwarded-for");
            }
            if (formattedRequestHeader.containsKey("wl-proxy-client-ip")
                    && formattedRequestHeader.get("wl-proxy-client-ip") != null
                    && !formattedRequestHeader.get("wl-proxy-client-ip").isEmpty()) {
                return formattedRequestHeader.get("wl-proxy-client-ip");
            }
            if (formattedRequestHeader.containsKey("x-forwarded-host")
                    && formattedRequestHeader.get("x-forwarded-host") != null
                    && !formattedRequestHeader.get("x-forwarded-host").isEmpty()) {
                return formattedRequestHeader.get("x-forwarded-host");
            }
            return defaultRemoteAddr;
        }

    }
}
