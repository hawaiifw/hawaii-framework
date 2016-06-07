/*
 * Copyright 2015-2016 the original author or authors.
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

package org.hawaiiframework.web.exception;

import org.hawaiiframework.web.resource.ValidationErrorResource;

import java.util.List;

/**
 * This class represents the body of a REST error response.
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
     * Return the HTTP method.
     *
     * @return the HTTP method
     */
    public String getMethod() {
        return method;
    }

    /**
     * Set the HTTP method.
     *
     * @param method the HTTP method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Get the request URI.
     *
     * @return the request URI
     */
    public String getUri() {
        return uri;
    }

    /**
     * Set the request URI.
     *
     * @param uri the request URI
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Get the query string.
     *
     * @return the query string.
     */
    public String getQuery() {
        return query;
    }

    /**
     * Set the query string.
     *
     * @param query the query string
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * Get the request content type.
     *
     * @return the content type
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Set the request content type.
     *
     * @param contentType the content type.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Get the response status code.
     *
     * @return the responst status code.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Set the response status code.
     *
     * @param statusCode the response status code.
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Get the response status message.
     *
     * @return the status message.
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Set the response status message.
     *
     * @param statusMessage the response status message
     */
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    /**
     * Get the error message ({@code Throwable#getMessage()})
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /** Set the error message.
     *
     * @param errorMessage the error message.
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Get the validation errors.
     *
     * @return the validation errors list.
     */
    public List<ValidationErrorResource> getErrors() {
        return errors;
    }

    /**
     * Set the validation errors.
     *
     * @param errors the validation errors.
     */
    public void setErrors(List<ValidationErrorResource> errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        return "ErrorResponseResource{" +
                "method='" + method + '\'' +
                ", uri='" + uri + '\'' +
                ", query='" + query + '\'' +
                ", contentType='" + contentType + '\'' +
                ", statusCode=" + statusCode +
                ", statusMessage='" + statusMessage + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", errors=" + errors +
                '}';
    }
}