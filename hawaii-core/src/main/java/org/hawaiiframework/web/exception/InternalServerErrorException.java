package org.hawaiiframework.web.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class InternalServerErrorException extends HttpException {

    public InternalServerErrorException() {
        super(INTERNAL_SERVER_ERROR);
    }

    public InternalServerErrorException(String message) {
        super(message, INTERNAL_SERVER_ERROR);
    }

    public InternalServerErrorException(String message, Throwable cause) {
        super(message, cause, INTERNAL_SERVER_ERROR);
    }

    public InternalServerErrorException(Throwable cause) {
        super(cause, INTERNAL_SERVER_ERROR);
    }

}
