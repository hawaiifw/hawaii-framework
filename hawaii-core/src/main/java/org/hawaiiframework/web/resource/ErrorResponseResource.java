/*
 * Copyright 2015-2017 the original author or authors.
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

package org.hawaiiframework.web.resource;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * This class represents the body of an error response.
 *
 * @author Ivan Melotte
 * @since 2.0.0
 */
public class ErrorResponseResource {

    private String method;
    private String uri;
    private String query;
    private String contentType;
    private int statusCode;
    private String statusMessage;
    private String errorMessage;
    private List<ValidationErrorResource> errors;

    /**
     * Returns the HTTP method.
     *
     * @return the HTTP method
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets the HTTP method.
     *
     * @param method the HTTP method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Returns the request URI.
     *
     * @return the request URI
     */
    public String getUri() {
        return uri;
    }

    /**
     * Sets the request URI.
     *
     * @param uri the request URI
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Returns the query string.
     *
     * @return the query string.
     */
    public String getQuery() {
        return query;
    }

    /**
     * Sets the query string.
     *
     * @param query the query string
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * Returns the request content type.
     *
     * @return the content type
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the request content type.
     *
     * @param contentType the content type.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Returns the response status code.
     *
     * @return the response status code.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the response status code.
     *
     * @param statusCode the response status code.
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Returns the response status message.
     *
     * @return the status message.
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the response status message.
     *
     * @param statusMessage the response status message
     */
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    /**
     * Returns the error message ({@code Throwable#getMessage()})
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the error message.
     *
     * @param errorMessage the error message.
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Returns the validation errors.
     *
     * @return the validation errors
     */
    public List<ValidationErrorResource> getErrors() {
        return errors;
    }

    /**
     * Sets the validation errors.
     *
     * @param errors the validation errors
     */
    public void setErrors(List<ValidationErrorResource> errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, SHORT_PREFIX_STYLE);
    }
}
