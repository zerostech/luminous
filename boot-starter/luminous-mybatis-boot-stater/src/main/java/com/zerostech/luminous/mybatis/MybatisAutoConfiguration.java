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

package com.zerostech.luminous.mybatis;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.zerostech.luminous.mybatis.expand.CustomizationSqlInjector;
import com.zerostech.luminous.mybatis.interceptor.EscapeSqlParameterInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Slf4j
@Configuration
@EnableConfigurationProperties(value = {MybatisProperties.class})
public class MybatisAutoConfiguration {

    @Autowired
    private MybatisProperties mybatisProperties;

    @PostConstruct
    public void init() {
        log.info("Load Auto Configuration : {}", this.getClass().getName());
    }

    @Bean
    public MybatisPlusInterceptor paginationInterceptor() {
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        paginationInterceptor.setMaxLimit(mybatisProperties.getPagination().getMaxLimit());
        paginationInterceptor.setOverflow(mybatisProperties.getPagination().isOverflow());
        paginationInterceptor.setDbType(mybatisProperties.getPagination().getDbType() != null ? DbType.getDbType(mybatisProperties.getPagination().getDbType()) : null);
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(paginationInterceptor);
        if (mybatisProperties.isBlockAttackEnabled()) {
            interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        }
        return interceptor;

    }

    @Bean
    public CustomizationSqlInjector customSqlInjector() {
        return new CustomizationSqlInjector();
    }

    @Bean
    @ConditionalOnExpression("${luminous.mybatis.escapeSqlParameterEnabled:false}")
    public String myInterceptor(SqlSessionFactory sqlSessionFactory) {
        //实例化插件
        EscapeSqlParameterInterceptor sqlInterceptor = new EscapeSqlParameterInterceptor();
        sqlSessionFactory.getConfiguration().addInterceptor(sqlInterceptor);
        return "interceptor";
    }
}
