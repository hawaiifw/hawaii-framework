package org.hawaiiframework.web.exception;

import org.hawaiiframework.exception.HawaiiException;
import org.springframework.http.HttpStatus;

import static java.util.Objects.requireNonNull;

public class HttpException extends HawaiiException {

    private final HttpStatus httpStatus;

    public HttpException(HttpStatus httpStatus) {
        this.httpStatus = requireNonNull(httpStatus);
    }

    public HttpException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = requireNonNull(httpStatus);
    }

    public HttpException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = requireNonNull(httpStatus);
    }

    public HttpException(Throwable cause, HttpStatus httpStatus) {
        super(cause);
        this.httpStatus = requireNonNull(httpStatus);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}