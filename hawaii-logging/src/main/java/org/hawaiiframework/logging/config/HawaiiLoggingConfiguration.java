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
package org.hawaiiframework.logging.config;

import org.hawaiiframework.logging.http.DefaultHawaiiRequestResponseLogger;
import org.hawaiiframework.logging.http.HawaiiRequestResponseLogger;
import org.hawaiiframework.logging.http.client.LoggingClientHttpRequestInterceptor;
import org.hawaiiframework.logging.util.HttpRequestResponseBodyLogUtil;
import org.hawaiiframework.logging.util.HttpRequestResponseDebugLogUtil;
import org.hawaiiframework.logging.util.HttpRequestResponseHeadersLogUtil;
import org.hawaiiframework.logging.util.PasswordMaskerUtil;
import org.hawaiiframework.sql.DataSourceProxyConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configures the logging based on the application properties.
 * <p>
 * This class creates filter beans for the enabled filters.
 *
 * @author Wouter Eerdekens
 * @author Paul Klos
 * @author Rutger Lubbers
 * @since 2.0.0
 */
@Configuration
@Import({CxfLoggingConfiguration.class, DataSourceProxyConfiguration.class, HawaiiLoggingFilterConfiguration.class,
    ScheduledConfiguration.class, StatementLoggerQueryExecutionListenerConfiguration.class})
@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
public class HawaiiLoggingConfiguration {

    /**
     * Create a {@link HawaiiLoggingConfigurationProperties} bean.
     *
     * @return the bean.
     */
    @Bean
    @RefreshScope
    public HawaiiLoggingConfigurationProperties hawaiiLoggingConfigurationProperties() {
        return new HawaiiLoggingConfigurationProperties();
    }

    /**
     * Create a {@link PasswordMaskerUtil} bean.
     *
     * @param properties the configuration properties.
     * @return the bean.
     */
    @Bean
    @RefreshScope
    public PasswordMaskerUtil passwordMaskerUtil(final HawaiiLoggingConfigurationProperties properties) {
        return new PasswordMaskerUtil(properties.getFieldsToMask());
    }

    /**
     * Create a {@link HttpRequestResponseHeadersLogUtil} bean.
     *
     * @param passwordMasker the password masker.
     * @return the bean.
     */
    @Bean
    @RefreshScope
    public HttpRequestResponseHeadersLogUtil httpRequestResponseHeadersLogUtil(final PasswordMaskerUtil passwordMasker) {
        return new HttpRequestResponseHeadersLogUtil(passwordMasker);
    }

    /**
     * Create a {@link HttpRequestResponseBodyLogUtil} bean.
     *
     * @param passwordMasker the password masker.
     * @return the bean.
     */
    @Bean
    @RefreshScope
    public HttpRequestResponseBodyLogUtil httpRequestResponseLogBodyUtil(final PasswordMaskerUtil passwordMasker) {
        return new HttpRequestResponseBodyLogUtil(passwordMasker);
    }

    /**
     * Create a {@link HttpRequestResponseDebugLogUtil} bean.
     *
     * @return the bean.
     */
    @Bean
    public HttpRequestResponseDebugLogUtil debugLogUtil() {
        return new HttpRequestResponseDebugLogUtil();
    }

    /**
     * Create a {@link LoggingClientHttpRequestInterceptor} bean.
     *
     * @param hawaiiRequestResponseLogger The response logger.
     * @return the bean.
     */
    @Bean
    @RefreshScope
    public LoggingClientHttpRequestInterceptor loggingClientHttpRequestInterceptor(
            final HawaiiRequestResponseLogger hawaiiRequestResponseLogger) {
        return new LoggingClientHttpRequestInterceptor(hawaiiRequestResponseLogger);
    }

    /**
     * Create a {@link HawaiiRequestResponseLogger} bean.
     *
     * @param headersLogUtil util for logging headers.
     * @param bodyLogUtil    util for logging request/response bodies.
     * @param debugLogUtil   util for extra debug log formatting.
     * @param mediaTypeVoter a Media Type voter.
     * @return a {@link HawaiiRequestResponseLogger} bean.
     */
    @Bean
    @RefreshScope
    @ConditionalOnMissingBean(HawaiiRequestResponseLogger.class)
    public HawaiiRequestResponseLogger hawaiiRequestResponseLogger(
            final HttpRequestResponseHeadersLogUtil headersLogUtil,
            final HttpRequestResponseBodyLogUtil bodyLogUtil,
            final HttpRequestResponseDebugLogUtil debugLogUtil,
            final MediaTypeVoter mediaTypeVoter,
            @Qualifier("bodyExcludedMediaTypeVoter") final MediaTypeVoter suppressedMediaTypeVoter) {
        return new DefaultHawaiiRequestResponseLogger(headersLogUtil, bodyLogUtil, debugLogUtil,
                mediaTypeVoter, suppressedMediaTypeVoter);
    }

    /**
     * Create a Media Type voter.
     *
     * @param hawaiiLoggingConfigurationProperties The configuration properties.
     * @return The bean.
     */
    @Bean
    @RefreshScope
    public MediaTypeVoter mediaTypeVoter(final HawaiiLoggingConfigurationProperties hawaiiLoggingConfigurationProperties) {
        return new MediaTypeVoter(hawaiiLoggingConfigurationProperties.getAllowedContentTypes(), true);
    }

    @Bean
    @RefreshScope
    public MediaTypeVoter bodyExcludedMediaTypeVoter(final HawaiiLoggingConfigurationProperties hawaiiLoggingConfigurationProperties) {
        return new MediaTypeVoter(hawaiiLoggingConfigurationProperties.getBodyExcludedContentTypes(), false);
    }

    /**
     * Create a request voter.
     *
     * @param hawaiiLoggingConfigurationProperties The configuration properties.
     * @return The bean.
     */
    @Bean
    @RefreshScope
    public RequestVoter requestVoter(final HawaiiLoggingConfigurationProperties hawaiiLoggingConfigurationProperties) {
        return new RequestVoter(hawaiiLoggingConfigurationProperties);
    }


    /**
     * Create a filter voter parameter.
     *
     * @param mediaTypeVoter The media type voter.
     * @param requestVoter   The request voter.
     * @return The bean.
     */
    @Bean
    @RefreshScope
    public FilterVoter filterVoter(final MediaTypeVoter mediaTypeVoter, final RequestVoter requestVoter) {
        return new FilterVoter(mediaTypeVoter, requestVoter);
    }
}
