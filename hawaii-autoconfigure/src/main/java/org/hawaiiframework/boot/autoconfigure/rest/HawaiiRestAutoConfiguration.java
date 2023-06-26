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
import org.hawaiiframework.converter.ModelConverter;
import org.hawaiiframework.validation.ValidationError;
import org.hawaiiframework.web.exception.ApiErrorResponseEnricher;
import org.hawaiiframework.web.exception.DefaultExceptionResponseFactory;
import org.hawaiiframework.web.exception.ErrorResponseEnricher;
import org.hawaiiframework.web.exception.ErrorResponseEntityBuilder;
import org.hawaiiframework.web.exception.ErrorResponseStatusEnricher;
import org.hawaiiframework.web.exception.ExceptionResponseFactory;
import org.hawaiiframework.web.exception.HawaiiResponseEntityExceptionHandler;
import org.hawaiiframework.web.exception.MethodArgumentNotValidResponseEnricher;
import org.hawaiiframework.web.exception.RequestInfoErrorResponseEnricher;
import org.hawaiiframework.web.exception.SpringSecurityResponseEntityExceptionHandler;
import org.hawaiiframework.web.exception.ValidationErrorResponseEnricher;
import org.hawaiiframework.web.resource.ObjectErrorResourceAssembler;
import org.hawaiiframework.web.resource.ValidationErrorResource;
import org.hawaiiframework.web.resource.ValidationErrorResourceAssembler;
import org.hawaiiframework.web.util.HostResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * @author Marcel Overdijk
 * @since 2.0.0
 */
@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
@Configuration
@ConditionalOnWebApplication
public class HawaiiRestAutoConfiguration {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Create a host resolver bean.
     *
     * @return The host resolver bean.
     */
    @Bean
    public HostResolver hostResolver() {
        return new HostResolver();
    }

    /**
     * Create an object error resource assembler.
     *
     * @return The object error resource assembler bean.
     */
    @Bean
    public ObjectErrorResourceAssembler objectErrorResourceAssembler() {
        return new ObjectErrorResourceAssembler(objectMapper);
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

    @Bean
    public ErrorResponseEntityBuilder errorResponseEntityBuilder(final List<ErrorResponseEnricher> errorResponseEnrichers) {
        return new ErrorResponseEntityBuilder(exceptionResponseFactory(), errorResponseEnrichers);
    }

    /**
     * Create a Hawaii response exception handler.
     *
     * @return The Hawaii response exception handler bean.
     */
    @Bean
    public HawaiiResponseEntityExceptionHandler hawaiiResponseEntityExceptionHandler(
            final ErrorResponseEntityBuilder errorResponseEntityBuilder) {
        return new HawaiiResponseEntityExceptionHandler(errorResponseEntityBuilder);
    }
    /**
     * Create a Spring Security response exception handler.
     *
     * @return The Spring Security response exception handler bean.
     */
    @ConditionalOnClass(AccessDeniedException.class)
    @Bean
    public SpringSecurityResponseEntityExceptionHandler springSecurityResponseEntityExceptionHandler(
            final ErrorResponseEntityBuilder errorResponseEntityBuilder) {
        return new SpringSecurityResponseEntityExceptionHandler(errorResponseEntityBuilder);
    }
    /**
     * Create an error response status enricher.
     *
     * @return The error response status enricher bean.
     */
    @Bean
    public ErrorResponseStatusEnricher errorResponseStatusEnricher() {
        return new ErrorResponseStatusEnricher();
    }

    /**
     * Create a request info error response enricher.
     *
     * @return The request info error response enricher bean.
     */
    @Bean
    public RequestInfoErrorResponseEnricher requestInfoErrorResponseEnricher() {
        return new RequestInfoErrorResponseEnricher();
    }

    /**
     * Create a method argument not valid response enricher.
     *
     * @return The method argument not valid response enricher bean.
     */
    @Bean
    public MethodArgumentNotValidResponseEnricher methodArgumentNotValidResponseEnricher(
            final ModelConverter<ObjectError, ValidationErrorResource> objectErrorResourceAssembler) {
        return new MethodArgumentNotValidResponseEnricher(objectErrorResourceAssembler);
    }

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
     * Create a validation error response enricher.
     *
     * @return The validation error response enricher bean.
     */
    @Bean
    public ValidationErrorResponseEnricher validationErrorResponseEnricher(
            final ModelConverter<ValidationError, ValidationErrorResource> validationErrorResourceAssembler) {
        return new ValidationErrorResponseEnricher(validationErrorResourceAssembler);
    }

    /**
     * Create an api error response enricher.
     *
     * @return The api error response enricher bean.
     */
    @Bean
    public ApiErrorResponseEnricher apiErrorResponseEnricher() {
        return new ApiErrorResponseEnricher();
    }
}
