/*
 * Copyright 2015-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hawaiiframework.boot.autoconfigure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hawaiiframework.web.exception.DefaultExceptionResponseFactory;
import org.hawaiiframework.web.exception.ExceptionResponseFactory;
import org.hawaiiframework.web.exception.HawaiiResponseEntityExceptionHandler;
import org.hawaiiframework.web.resource.ValidationErrorResourceAssembler;
import org.hawaiiframework.web.util.HostResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Marcel Overdijk
 * @since 2.0.0
 */
@Configuration
@ConditionalOnWebApplication
public class HawaiiRestAutoConfiguration {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Create a validation error resource assembler.
     *
     * @return The validation error resource assembler bean.
     */
    @Bean
    public ValidationErrorResourceAssembler validationErrorResourceAssembler() {
        return new ValidationErrorResourceAssembler(objectMapper);
    }

    /**
     * Create an exception response factory.
     *
     * @return The exception response factory bean.
     */
    @Bean
    public ExceptionResponseFactory exceptionResponseFactory() {
        return new DefaultExceptionResponseFactory();
    }

    /**
     * Create a Hawaii response exception handler.
     *
     * @return The Hawaii response exception handler bean.
     */
    @Bean
    public HawaiiResponseEntityExceptionHandler hawaiiResponseEntityExceptionHandler() {
        return new HawaiiResponseEntityExceptionHandler(validationErrorResourceAssembler(), exceptionResponseFactory());
    }

    /**
     * Create a host resolver bean.
     *
     * @return The host resolver bean.
     */
    @Bean
    public HostResolver hostResolver() {
        return new HostResolver();
    }
}
