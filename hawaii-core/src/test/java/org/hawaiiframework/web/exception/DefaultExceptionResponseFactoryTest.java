package org.hawaiiframework.web.exception;

import org.hawaiiframework.exception.ApiError;
import org.hawaiiframework.exception.ApiException;
import org.hawaiiframework.exception.HawaiiException;
import org.hawaiiframework.validation.ValidationException;
import org.hawaiiframework.web.resource.ApiErrorResponseResource;
import org.hawaiiframework.web.resource.ErrorResponseResource;
import org.hawaiiframework.web.resource.ValidationErrorResponseResource;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultExceptionResponseFactoryTest {

    private DefaultExceptionResponseFactory exceptionResponseFactory;

    @Before
    public void setup() throws Exception {
        exceptionResponseFactory = new DefaultExceptionResponseFactory();
    }

    @Test
    public void thatNullExceptionReturnsDefaultErrorResource() throws Exception {
        ErrorResponseResource resource = exceptionResponseFactory.create(null);
        assertTrue("Not a ErrorResponseResource", resource != null);
    }

    @Test
    public void thatApiResourceIsCreatedForApiException() throws Exception {
        ErrorResponseResource resource = exceptionResponseFactory.create(new TestApiException());
        assertTrue("Not a ApiErrorResponseResource", resource instanceof ApiErrorResponseResource);
    }

    @Test
    public void thatApiResourceIsCreatedForContainedApiException() throws Exception {
        ErrorResponseResource resource = exceptionResponseFactory.create(new HawaiiException(new TestApiException()));
        assertTrue("Not a ApiErrorResponseResource", resource instanceof ApiErrorResponseResource);
    }

    @Test
    public void thatApiResourceIsCreatedForContainedApiExceptionInTwoLevels() throws Exception {
        ErrorResponseResource resource = exceptionResponseFactory.create(new HawaiiException(new IllegalStateException(new TestApiException())));
        assertTrue("Not a ApiErrorResponseResource", resource instanceof ApiErrorResponseResource);
    }

    @Test
    public void thatValidationErrorResourceIsCreatedForValidationException() throws Exception {
        ErrorResponseResource resource = exceptionResponseFactory.create(new ValidationException());
        assertTrue("Not a ValidationErrorResponseResource", resource instanceof ValidationErrorResponseResource);
    }

    @Test
    public void thatValidationErrorResourceIsCreatedForContainedValidationException() throws Exception {
        ErrorResponseResource resource = exceptionResponseFactory.create(new HawaiiException(new ValidationException()));
        assertTrue("Not a ValidationErrorResponseResource", resource instanceof ValidationErrorResponseResource);
    }

    private static class TestApiException extends ApiException {
        private static final ApiError error = new ApiError() {

            @Override
            public String getErrorCode() {
                return "100";
            }

            @Override
            public String getReason() {
                return "reason";
            }
        };

        private TestApiException() {
            this(null);
        }

        private TestApiException(Throwable orig) {
            super(error, orig);
        }
    }
}
