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
package com.zerostech.luminous.redis.ids;


/**
 * 说明：ID生成异常
 *
 * @author 迹_Jason
 * @date 2024/10/06
 */
public class IdsException extends RuntimeException {

    private static final long serialVersionUID = -5120954691727732419L;

    public IdsException(final String msg) {
        super(msg);
    }

    public IdsException(final Exception e) {
        super(e);
    }

}
