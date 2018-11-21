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
@SuppressWarnings("PMD.DataClass")
public class HawaiiLoggingConfigurationProperties {

    /**
     * Configuration properties for the Kibana log filter.
     */
    @NestedConfigurationProperty
    private HttpHeaderLoggingFilterProperties kibanaLog;

    /**
     * Configuration properties for the Kibana log cleanup filter.
     */
    @NestedConfigurationProperty
    private LoggingFilterProperties kibanaLogCleanup;

    /**
     * Configuration properties for the request duration logging filter.
     */
    @NestedConfigurationProperty
    private LoggingFilterProperties requestDuration;

    /**
     * Configuration properties for the request-response logging filter.
     */
    @NestedConfigurationProperty
    private RequestResponseLogFilterConfiguration requestResponse;

    /**
     * Configuration properties for the request id logging filter.
     */
    @NestedConfigurationProperty
    private HttpHeaderLoggingFilterProperties requestId;

    /**
     * Configuration properties for the transaction id logging filter.
     */
    @NestedConfigurationProperty
    private HttpHeaderLoggingFilterProperties transactionId;

    /**
     * Configuration properties for the class method logging filter.
     */
    @NestedConfigurationProperty
    private LoggingFilterProperties classMethodName;

    /**
     * Configuration properties for the user details logging filter.
     */
    @NestedConfigurationProperty
    private LoggingFilterProperties userDetails;

    /**
     * Getter for the Kibana log filter properties.
     *
     * @return the properties
     */
    public HttpHeaderLoggingFilterProperties getKibanaLog() {
        return kibanaLog;
    }

    /**
     * Setter for the Kibana log filter properties.
     *
     * @param kibanaLog the properties
     */
    public void setKibanaLog(final HttpHeaderLoggingFilterProperties kibanaLog) {
        this.kibanaLog = kibanaLog;
    }

    /**
     * Getter for the Kibana log cleanup filter properties.
     *
     * @return the properties
     */
    public LoggingFilterProperties getKibanaLogCleanup() {
        return kibanaLogCleanup;
    }

    /**
     * Setter for the Kibana log cleanup filter properties.
     *
     * @param kibanaLogCleanup the properties
     */
    public void setKibanaLogCleanup(final LoggingFilterProperties kibanaLogCleanup) {
        this.kibanaLogCleanup = kibanaLogCleanup;
    }

    /**
     * Getter for the request duration logging filter properties.
     *
     * @return the properties
     */
    public LoggingFilterProperties getRequestDuration() {
        return requestDuration;
    }

    /**
     * Setter for the request duration logging filter properties.
     *
     * @param requestDuration the properties
     */
    public void setRequestDuration(final LoggingFilterProperties requestDuration) {
        this.requestDuration = requestDuration;
    }

    /**
     * Getter for the request-response logging filter properties.
     *
     * @return the properties
     */
    public RequestResponseLogFilterConfiguration getRequestResponse() {
        return requestResponse;
    }

    /**
     * Setter for the request-response logging filter properties.
     *
     * @param requestResponse the properties
     */
    public void setRequestResponse(final RequestResponseLogFilterConfiguration requestResponse) {
        this.requestResponse = requestResponse;
    }

    /**
     * Getter for the request id logging filter properties.
     *
     * @return the properties
     */
    public HttpHeaderLoggingFilterProperties getRequestId() {
        return requestId;
    }

    /**
     * Setter for the request id logging filter properties.
     *
     * @param requestId the properties
     */
    public void setRequestId(final HttpHeaderLoggingFilterProperties requestId) {
        this.requestId = requestId;
    }

    /**
     * Getter for the transaction id logging filter properties.
     *
     * @return the properties
     */
    public HttpHeaderLoggingFilterProperties getTransactionId() {
        return transactionId;
    }

    /**
     * Setter for the transaction id logging filter properties.
     *
     * @param transactionId the properties
     */
    public void setTransactionId(final HttpHeaderLoggingFilterProperties transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Getter for the user details logging filter properties.
     *
     * @return the properties
     */
    public LoggingFilterProperties getUserDetails() {
        return userDetails;
    }

    /**
     * Setter for the user details logging filter properties.
     *
     * @param userDetails the properties
     */
    public void setUserDetails(final LoggingFilterProperties userDetails) {
        this.userDetails = userDetails;
    }

    /**
     * Getter for the class method name filter properties.
     *
     * @return the properties
     */
    public LoggingFilterProperties getClassMethodName() {
        return classMethodName;
    }

    /**
     * Setter for the class method name filter properties.
     *
     * @param classMethodName the properties
     */
    public void setClassMethodName(final LoggingFilterProperties classMethodName) {
        this.classMethodName = classMethodName;
    }

}
