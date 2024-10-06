/*
 *
 *  * Copyright 2022. the original author or authors
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

package com.zerostech.luminous.common.utils;

import cn.hutool.core.util.ArrayUtil;

import java.util.regex.Matcher;

/**
 * Created on 2023/5/5.
 *
 * @author 迹_Jason
 */

public class StringHelper {

    public static String replaceBraces(String content, String[] args) {
        if (content == null || content.isEmpty() || ArrayUtil.isEmpty(args)) {
            return content;
        }
        for (String arg : args) {
            if (arg == null || arg.isEmpty()) {
                continue;
            }
            content = content.replaceFirst("\\{\\}", Matcher.quoteReplacement(arg));
        }
        return content;
    }
}
