package org.hawaiiframework.web.exception;

import org.hawaiiframework.web.resource.ValidationErrorResource;

import java.util.List;

public class ErrorResponseResource {

    private String method;
    private String uri;
    private String query;
    private String contentType;
    private int statusCode;
    private String statusMessage;
    private String errorMessage;
    private List<ValidationErrorResource> errors;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<ValidationErrorResource> getErrors() {
        return errors;
    }

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