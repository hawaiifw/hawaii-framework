package org.hawaiiframework.web.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class BadRequestException extends HttpException {

    public BadRequestException() {
        super(BAD_REQUEST);
    }

    public BadRequestException(String message) {
        super(message, BAD_REQUEST);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause, BAD_REQUEST);
    }

    public BadRequestException(Throwable cause) {
        super(cause, BAD_REQUEST);
    }

}