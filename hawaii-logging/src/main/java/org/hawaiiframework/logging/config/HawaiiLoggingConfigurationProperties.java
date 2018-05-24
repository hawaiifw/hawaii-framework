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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * Model class that represents the Hawaii logging configuration properties.
 *
 * @author Paul Klos
 * @author Wouter Eerdekens
 * @since 2.0.0
 */
@Component
@ConfigurationProperties(prefix = "hawaii.logging.filters")
public class HawaiiLoggingConfigurationProperties {

    @NestedConfigurationProperty
    private HttpHeaderLoggingFilterProperties kibanaLog;

    @NestedConfigurationProperty
    private LoggingFilterProperties kibanaLogCleanup;

    @NestedConfigurationProperty
    private LoggingFilterProperties requestDuration;

    @NestedConfigurationProperty
    private RequestResponseLogFilterConfiguration requestResponse;

    @NestedConfigurationProperty
    private HttpHeaderLoggingFilterProperties requestId;

    @NestedConfigurationProperty
    private HttpHeaderLoggingFilterProperties transactionId;

    @NestedConfigurationProperty
    private LoggingFilterProperties userDetails;

    public HttpHeaderLoggingFilterProperties getKibanaLog() {
        return kibanaLog;
    }

    public void setKibanaLog(final HttpHeaderLoggingFilterProperties kibanaLog) {
        this.kibanaLog = kibanaLog;
    }

    public LoggingFilterProperties getKibanaLogCleanup() {
        return kibanaLogCleanup;
    }

    public void setKibanaLogCleanup(final LoggingFilterProperties kibanaLogCleanup) {
        this.kibanaLogCleanup = kibanaLogCleanup;
    }

    public LoggingFilterProperties getRequestDuration() {
        return requestDuration;
    }

    public void setRequestDuration(final LoggingFilterProperties requestDuration) {
        this.requestDuration = requestDuration;
    }

    public RequestResponseLogFilterConfiguration getRequestResponse() {
        return requestResponse;
    }

    public void setRequestResponse(final RequestResponseLogFilterConfiguration requestResponse) {
        this.requestResponse = requestResponse;
    }

    public HttpHeaderLoggingFilterProperties getRequestId() {
        return requestId;
    }

    public void setRequestId(final HttpHeaderLoggingFilterProperties requestId) {
        this.requestId = requestId;
    }

    public HttpHeaderLoggingFilterProperties getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(final HttpHeaderLoggingFilterProperties transactionId) {
        this.transactionId = transactionId;
    }

    public LoggingFilterProperties getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(final LoggingFilterProperties userDetails) {
        this.userDetails = userDetails;
    }
}
