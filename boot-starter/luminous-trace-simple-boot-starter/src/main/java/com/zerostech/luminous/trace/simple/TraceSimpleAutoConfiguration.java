/*
 *
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
 *
 */

package com.zerostech.luminous.trace.simple;

import com.zerostech.luminous.common.tracing.ITracing;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.Filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.client.RestTemplate;

/**
 * 简单链路跟踪自动配置.
 * <p>
 * 当 {@code luminous.trace=simple} 时激活，提供基于UUID的链路跟踪实现。
 * 由 Lu.init() 按bean名称 {@code simpleTracing} 自动发现并集成到框架中。
 * </p>
 *
 * @author 迹_Jason
 */
@Slf4j
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(Filter.class)
@ConditionalOnExpression("'${luminous.trace:simple}'.equals('simple')")
@EnableConfigurationProperties(TraceSimpleProperties.class)
public class TraceSimpleAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("Load Auto Configuration : {}", this.getClass().getName());
    }

    /**
     * 简单链路跟踪实现.
     * <p>
     * Bean名称为 simpleTracing，由 Lu.init() 通过
     * {@code luminous.web.trace + "Tracing"} 模式发现。
     * </p>
     *
     * @return 简单链路跟踪实现
     */
    @Bean
    public SimpleTracing simpleTracing() {
        return new SimpleTracing();
    }

    /**
     * 链路跟踪过滤器注册.
     *
     * @param tracing    链路跟踪实现
     * @param properties 配置属性
     * @return 过滤器注册Bean
     */
    @Bean
    public FilterRegistrationBean<TraceFilter> traceFilter(ITracing tracing, TraceSimpleProperties properties) {
        FilterRegistrationBean<TraceFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TraceFilter(tracing, properties.getHeaderName()));
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        registration.setName("traceFilter");
        return registration;
    }

    /**
     * RestTemplate链路跟踪Bean后置处理器.
     * <p>
     * 自动为所有RestTemplate Bean添加链路跟踪拦截器，
     * 实现跨服务的traceId传递。
     * </p>
     *
     * @param tracing    链路跟踪实现
     * @param properties 配置属性
     * @return Bean后置处理器
     */
    @Bean
    @ConditionalOnClass(RestTemplate.class)
    public TraceRestTemplatePostProcessor traceRestTemplatePostProcessor(ITracing tracing, TraceSimpleProperties properties) {
        return new TraceRestTemplatePostProcessor(tracing, properties.getHeaderName());
    }

    /**
     * RestTemplate链路跟踪后置处理器.
     * <p>
     * 在RestTemplate Bean初始化后自动添加 {@link TraceRestTemplateInterceptor}，
     * 实现跨服务的traceId自动传递。
     * </p>
     */
    static class TraceRestTemplatePostProcessor implements BeanPostProcessor {

        private final ITracing tracing;
        private final String headerName;

        TraceRestTemplatePostProcessor(ITracing tracing, String headerName) {
            this.tracing = tracing;
            this.headerName = headerName;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof RestTemplate restTemplate) {
                restTemplate.getInterceptors().add(new TraceRestTemplateInterceptor(tracing, headerName));
            }
            return bean;
        }
    }
}
