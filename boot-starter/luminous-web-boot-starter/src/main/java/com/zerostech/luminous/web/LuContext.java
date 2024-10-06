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

import cn.hutool.core.util.IdUtil;
import cn.hutool.system.UserInfo;
import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * Created on 2024/5/26.
 *
 * @author 迹_Jason
 */

public class LuContext {
    private static final TransmittableThreadLocal<LuContext> CONTEXT = new TransmittableThreadLocal<>();
    /**
     * 当次请求的ID.
     */
    private String id;
    /**
     * 请求来源IP.
     */
    private String sourceIP;
    /**
     * 请求最初的URL.
     */
    private String requestUri;

    /**
     * 用户id
     */
    private UserInfo userInfo;

    /**
     * Gets context.
     *
     * @return the context
     */
    public static LuContext getContext() {
        LuContext cxt = CONTEXT.get();
        if (cxt == null) {
            cxt = new LuContext();
            cxt.id = IdUtil.simpleUUID();
            cxt.sourceIP = Lu.Info.ip;
            cxt.requestUri = "";
            cxt.userInfo = new UserInfo();
            setContext(cxt);
        }
        return cxt;
    }

    public static void removeContext() {
        CONTEXT.remove();
    }

    /**
     * Sets context.
     *
     * @param context the context
     */
    public static void setContext(LuContext context) {
        CONTEXT.set(context);
    }

    /**
     * Exist.
     *
     * @return <b>true</b> if existed
     */
    public static boolean exist() {
        return CONTEXT.get() != null;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets source ip.
     *
     * @return the source ip
     */
    public String getSourceIP() {
        return sourceIP;
    }

    /**
     * Sets source ip.
     *
     * @param sourceIP the source ip
     */
    public void setSourceIP(String sourceIP) {
        this.sourceIP = sourceIP;
    }

    /**
     * Gets request uri.
     *
     * @return the request uri
     */
    public String getRequestUri() {
        return requestUri;
    }

    /**
     * Sets request uri.
     *
     * @param requestUri the request uri
     */
    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    /**
     * 获取用户信息
     *
     * @return {@link UserInfo}
     */
    public UserInfo getUserInfo() {
        return userInfo;
    }

    /**
     * 设置用户信息
     *
     * @param userInfo 用户信息
     */
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
