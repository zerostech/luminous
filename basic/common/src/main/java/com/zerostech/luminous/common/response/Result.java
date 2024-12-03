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

package com.zerostech.luminous.common.response;

import com.zerostech.luminous.common.utils.StringHelper;
import lombok.Data;

import java.util.regex.Matcher;

/**
 * @description
 *
 * @author 迹_Jason
 *
 * @create 2024-11-26 23:29
 **/
@Data
public class Result<T> {

    public static String SUCCESS = "200";
    public static String BAD_REQUEST = "400";
    /**
     * 未认证错误
     */
    public static String NO_AUTH_ERROR = "401";
    /**
     * 无权限错误
     */
    public static String NO_AUTHORITY_ERROR = "403";
    /**
     * 请求错误
     */
    public static String HTTP_REQUEST_ERROR = "404";
    /**
     * 参数格式错误
     */
    public static String PARAM_PATTERN_ERROR = "405";
    /**
     * 参数未找到错误
     */
    public static String PARAM_NOT_FUND_ERROR = "406";
    public static String SERVER_ERROR = "500";

    private boolean success = false;
    /**
     * 返回码， 默认200 表示成功
     */
    private String code;
    /**
     * 失败提示
     */
    private String msg;
    /**
     * 返回数据
     */
    private T data;

    private String traceId;

    public Result(String code, String msg, T data, String traceId, String... args) {
        this.code = code;
        this.msg = StringHelper.replaceBraces(msg, args);
        this.data = data;
        this.traceId = traceId;
        this.success = SUCCESS.equals(code);
    }

    public Result(String code, String msg, T data, String traceId) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.traceId = traceId;
        this.success = SUCCESS.equals(code);
    }

    private String replace(String msg, String[] args) {
        if (msg == null || msg.isEmpty()) {
            return msg;
        }
        for (String arg : args) {
            msg = msg.replaceFirst("\\{\\}", Matcher.quoteReplacement(arg));
        }
        return msg;
    }

    public Result() {
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESS, null, data, null);
    }

    public static <T> Result<T> success(T data, String message) {
        return new Result<>(SUCCESS, message, data, null);
    }

    public static <T> Result<T> customize(String code, String msg, String traceId, String... args) {
        return new Result<>(code, msg, null, traceId, args);
    }

    public static <T> Result<T> badRequest(String msg, String... args) {
        return new Result<>(BAD_REQUEST, msg, null, null, args);
    }

    public static <T> Result<T> serverError(String msg, String... args) {
        return new Result<>(SERVER_ERROR, msg, null, null, args);
    }

    public static <T> Result<T> error(Result<?> resp) {
        return new Result<>(resp.getCode(), resp.getMsg(), null, resp.getTraceId());
    }

    public static <T> Result<T> noAuth(String msg, String... args) {
        return new Result<>(NO_AUTH_ERROR, msg, null, null, args);
    }

    public static <T> Result<T> noAuthority(String msg, String... args) {
        return new Result<>(NO_AUTHORITY_ERROR, msg, null, null, args);
    }
}
