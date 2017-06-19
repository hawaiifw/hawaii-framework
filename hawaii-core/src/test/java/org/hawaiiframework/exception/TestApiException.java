package org.hawaiiframework.exception;

public class TestApiException extends ApiException {

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

    public TestApiException() {
        this(null);
    }

    public TestApiException(Throwable orig) {
        super(error, orig);
    }

}
