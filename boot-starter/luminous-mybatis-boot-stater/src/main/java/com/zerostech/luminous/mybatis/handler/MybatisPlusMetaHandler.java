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

package com.zerostech.luminous.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.zerostech.luminous.common.LuContext;
import com.zerostech.luminous.common.user.UserInfo;
import com.zerostech.luminous.mybatis.model.BasicModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;

/**
 * @description
 *
 * @author 迹_Jason
 *
 * @create 2024-12-03 22:17
 **/
@Slf4j
public class MybatisPlusMetaHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        if (log.isDebugEnabled()) {
            log.debug("Execute the mybatis plus 【insertFill】");
        }
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "modifyTime", Date.class, new Date());
        UserInfo userInfo= LuContext.getContext().getUserInfo();
        if (userInfo != null && userInfo.getUserId() != null) {
            this.strictInsertFill(metaObject, "creatorId", Long.class, userInfo.getUserId());
            this.strictInsertFill(metaObject, "modifierId", Long.class, userInfo.getUserId());
            this.strictInsertFill(metaObject, "creatorName", String.class, userInfo.getUsername());
            this.strictInsertFill(metaObject, "modifierName", String.class,  userInfo.getUsername());
        }
        this.strictInsertFill(metaObject, "deleted", Integer.class, BasicModel.NORMAL);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (log.isDebugEnabled()) {
            log.debug("Execute the mybatis plus 【updateFill】");
        }
        this.setFieldValByName("modifyTime", new Date(), metaObject);
        UserInfo userInfo= LuContext.getContext().getUserInfo();
        if (userInfo != null && userInfo.getUserId() != null) {
            this.setFieldValByName("modifierId", userInfo.getUserId(), metaObject);
            this.setFieldValByName("modifierName", userInfo.getUsername(), metaObject);
        }
    }
}
