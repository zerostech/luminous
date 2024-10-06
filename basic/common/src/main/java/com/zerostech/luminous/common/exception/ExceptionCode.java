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

/**
 * 参考：阿里巴巴《java开发规范》
 * 异常码定义：五位字符串， 第一位表示异常位置(A表示输入异常，B表示系统处理异常，C表示第三方异常, E未知异常)，后四位表示异常类型
 *
 * @author 迹_Jason
 * @since 2024/10/06
 */
public class ExceptionCode {
    // 未知异常，强制告警
    public static String UN_KNOW_ERROR = "E9999";

    // 参数错误，输入异常
    // 参数异常，默认不告警
    public static String PARAM_ERROR = "A0001";
    /**
     * 参数格式错误
     */
    public static String PARAM_PATTERN_ERROR = "A0002";
    /**
     * 参数未找到错误
     */
    public static String PARAM_NOT_FUND_ERROR = "A0003";
    /**
     * 请求类异常，比如：请求类型错误，参数格式错误等
     */
    public static String REQUEST_ERROR = "A0004";

    // 当前系统处理异常
    // 通用，业务处理失败，强制告警
    public static String PROCESS_ERROR = "B0001";
    // 并发控制，未获取到锁
    public static String PROCESS_LOCK_FAIL = "B0002";
    // 业务校验，数据不存在
    public static String PROCESS_CHECK_DATA_NOT_EXIST = "B0003";
    // 业务校验，数据不符合要求
    public static String PROCESS_CHECK_DATA_UN_MATCH = "B0004";
    // 业务校验，重复操作
    public static String PROCESS_CHECK_DUPLICATE = "B0005";
    /**
     * 数据已存在
     */
    public static String DATA_ALREADY_EISTED_ERROR = "B0006";
    /**
     * 没有权限
     */
    public static String NO_AUTHORITY = "B0007";

    // 调用上游系统失败
    // 上游发生异常，强制告警
    public static String THIRD_ERROR = "C0001";
    // 上游处理失败
    public static String THIRD_FAIL = "C0002";
}
