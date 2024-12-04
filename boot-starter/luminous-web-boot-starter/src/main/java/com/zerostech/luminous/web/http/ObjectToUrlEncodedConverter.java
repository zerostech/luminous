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

package com.zerostech.luminous.web.http;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * Created on 2021/11/5.
 *
 * @author 迹_Jason
 */
public class ObjectToUrlEncodedConverter<T> implements HttpMessageConverter<T> {
    private static final String ENCODING = "UTF-8";

    private final ObjectMapper mapper;

    public ObjectToUrlEncodedConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public boolean canRead(@Nullable Class<?> clazz, @Nullable MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(@Nullable Class<?> clazz, @Nullable MediaType mediaType) {
        return false;
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED);
    }

    @Override
    public  T read(@Nullable Class<? extends T> clazz, @Nullable HttpInputMessage inputMessage) throws HttpMessageNotReadableException {
        throw new NotImplementedException();
    }

    @Override
    public void write(@Nullable Object o, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws HttpMessageNotWritableException {
        String body = mapper.convertValue(o, UrlEncodedWriter.class)
                .toString();

        try {
            outputMessage.getBody().write(body.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            // if UTF-8 is not supporter then I give up
        }
    }

    private static class UrlEncodedWriter {
        private final StringBuilder out = new StringBuilder();

        @JsonAnySetter
        public void write(String name, Object property) throws UnsupportedEncodingException {
            if (out.length() > 0) {
                out.append("&");
            }

            out.append(URLEncoder.encode(name, ENCODING))
                    .append("=");

            if (property != null) {
                out.append(URLEncoder.encode(property.toString(), ENCODING));
            }
        }

        @Override
        public String toString() {
            return out.toString();
        }
    }
}
