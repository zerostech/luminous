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

package com.zerostech.luminous.common.exception;


import com.zerostech.luminous.common.utils.StringHelper;

/**
 * 业务异常错误
 *
 * @author 迹_Jason
 * @since 2024/10/06
 */

public class LuminousBizException extends RuntimeException {

    private static final long serialVersionUID = 29482026935570542L;

    private String code;

    public LuminousBizException(String code, String message) {
        super(message);
        this.code = code;
    }

    public LuminousBizException(String code, String message, String... args) {
        ex(code, message, args);
    }

    public LuminousBizException(String code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private static void ex(String code, String message, String... args) {
        throw new LuminousBizException(code, StringHelper.replaceBraces(message, args));
    }

    public static void unknowError(String message, String... args) {
        ex(ExceptionCode.UN_KNOW_ERROR, message, args);
    }

    public static void paramError(String message, String... args) {
        ex(ExceptionCode.PARAM_ERROR, message, args);
    }

    public static void processError(String message, String... args) {
        ex(ExceptionCode.PROCESS_ERROR, message, args);
    }

    public static void processLockFail(String message, String... args) {
        ex(ExceptionCode.PROCESS_LOCK_FAIL, message, args);
    }

    public static void processCheckDataNotExist(String message, String... args) {
        ex(ExceptionCode.PROCESS_CHECK_DATA_NOT_EXIST, message, args);
    }

    public static void processCheckDataUnmatch(String message, String... args) {
        ex(ExceptionCode.PROCESS_CHECK_DATA_UN_MATCH, message, args);
    }

    public static void processCheckDuplicate(String message, String... args) {
        ex(ExceptionCode.PROCESS_CHECK_DUPLICATE, message, args);
    }

    public static void thirdError(String message, String... args) {
        ex(ExceptionCode.THIRD_ERROR, message, args);
    }

    public static void thirdFail(String message, String... args) {
        ex(ExceptionCode.THIRD_FAIL, message, args);
    }
}
