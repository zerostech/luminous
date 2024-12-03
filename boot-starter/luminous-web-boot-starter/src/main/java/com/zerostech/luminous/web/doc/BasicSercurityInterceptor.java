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
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created on 2022/10/10.
 *
 * @author 迹_Jason
 */

public class BasicSercurityInterceptor extends BasicFilter implements HandlerInterceptor {

    private final LuConfig config;

    public BasicSercurityInterceptor(LuConfig config) {
        this.config = config;
    }

    /***
     * basic auth验证
     */
    public static final String SwaggerBootstrapUiBasicAuthSession = "SwaggerBootstrapUiBasicAuthSession";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LuConfig.Doc doc = config.getBasic().getDoc();
        //针对swagger资源请求过滤
        if (doc.getEnabled()) {
            if (match(request.getRequestURI())) {
                //判断Session中是否存在
                Object swaggerSessionValue = request.getSession().getAttribute(SwaggerBootstrapUiBasicAuthSession);
                if (swaggerSessionValue != null) {
                    return HandlerInterceptor.super.preHandle(request, response, handler);
                } else {
                    //匹配到,判断auth
                    //获取请求头Authorization
                    String auth = request.getHeader("Authorization");
                    if (auth == null || "".equals(auth)) {
                        writeForbiddenCode(response);
                        return false;
                    }
                    String userAndPass = decodeBase64(auth.substring(6));
                    String[] upArr = userAndPass.split(":");
                    if (upArr.length != 2) {
                        writeForbiddenCode(response);
                    } else {
                        String iptUser = upArr[0];
                        String iptPass = upArr[1];
                        //匹配服务端用户名及密码
                        if (iptUser.equals(doc.getSecurity().getUsername()) && iptPass.equals(doc.getSecurity().getPassword())) {
                            request.getSession().setAttribute(SwaggerBootstrapUiBasicAuthSession, doc.getSecurity().getUsername());
                            return HandlerInterceptor.super.preHandle(request, response, handler);
                        } else {
                            writeForbiddenCode(response);
                            return false;
                        }
                    }
                }
            } else {
                return HandlerInterceptor.super.preHandle(request, response, handler);
            }
        } else {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }


        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    private void writeForbiddenCode(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setStatus(401);
        httpServletResponse.setHeader("WWW-Authenticate", "Basic realm=\"input Swagger Basic userName & password \"");
        httpServletResponse.getWriter().write("You do not have permission to access this resource");
    }
}
