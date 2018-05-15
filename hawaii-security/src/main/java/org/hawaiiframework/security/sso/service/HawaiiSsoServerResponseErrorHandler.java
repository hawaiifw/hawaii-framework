package org.hawaiiframework.security.sso.service;

import org.apache.commons.io.IOUtils;
import org.hawaiiframework.errors.backend.BackendHttpException;
import org.hawaiiframework.errors.sso.SmsMaxTriesExceededException;
import org.hawaiiframework.errors.sso.WrongPasswordAlmostLockedException;
import org.hawaiiframework.errors.sso.WrongPasswordException;
import org.hawaiiframework.errors.sso.WrongPasswordLockedException;
import org.hawaiiframework.errors.web.UnauthorizedRequestException;
import org.hawaiiframework.exception.HawaiiException;
import org.hawaiiframework.security.sso.model.GenericUnauthorizedErrorResponse;
import org.hawaiiframework.security.sso.parser.GenericUnauthorizedResponseParser;
import org.hawaiiframework.security.sso.parser.VerifyPasswordError;
import org.hawaiiframework.security.sso.parser.VerifyPasswordErrorDescriptionResponseParser;
import org.hawaiiframework.security.sso.parser.VerifyPasswordErrorResponse;
import org.hawaiiframework.security.sso.parser.VerifyPasswordErrorResponseParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Handle SSO Server Response Errors.
 */
@Component("HawaiiSsoServerResponseErrorHandler")
public class HawaiiSsoServerResponseErrorHandler extends DefaultResponseErrorHandler {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HawaiiSsoServerResponseErrorHandler.class);

    /**
     * The parser for verify password error responses, the error contains an enum string value.
     */
    private final VerifyPasswordErrorResponseParser passwordErrorResponseParser;

    /**
     * The parser for verify password error responses, the descriptions contains a string that needs to be parsed to an enum  value.
     */
    private final VerifyPasswordErrorDescriptionResponseParser pwdErrorDescResponseParser;


    /**
     * The parser for generic error responses.
     */
    private final GenericUnauthorizedResponseParser unauthorizedResponseParser;

    /**
     * Constructor to instantiate an object mapper.
     */
    @Autowired
    public HawaiiSsoServerResponseErrorHandler(
            final VerifyPasswordErrorResponseParser passwordErrorResponseParser,
            final VerifyPasswordErrorDescriptionResponseParser pwdErrorDescResponseParser,
            final GenericUnauthorizedResponseParser unauthorizedResponseParser) {
        super();
        this.passwordErrorResponseParser = passwordErrorResponseParser;
        this.pwdErrorDescResponseParser = pwdErrorDescResponseParser;
        this.unauthorizedResponseParser = unauthorizedResponseParser;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This handler handles some special cases if an HTTP 401 unauthorized is returned from SSO:
     * <ul>
     * <li>Verify password - cases such as blocked and almost blocked</li>
     * <li>Confirm sms token - max attempts exceeded</li>
     * <li>Other - non specified, just throw a {@link UnauthorizedRequestException}</li>
     * </ul>
     * Any other error results in a {@link BackendHttpException}.
     * </p>
     * <p>
     * The determination of the type of response is done by trying various parsers to see which one results in a valid object.
     * </p>
     */
    @Override
    public void handleError(final ClientHttpResponse response) throws IOException {
        LOGGER.error("Got error from backend: '{} {}'.", response.getStatusCode(), response.getStatusText());
        if (HttpStatus.UNAUTHORIZED == response.getStatusCode()) {
            handleUnauthorized(response);
        } else if (HttpStatus.BAD_REQUEST == response.getStatusCode()) {
            handleBadRequest(response);
        }
        throw new BackendHttpException();
    }

    private void handleBadRequest(final ClientHttpResponse response) throws IOException {
        final String body = IOUtils.toString(response.getBody(), Charset.defaultCharset());
        // First check if this is a verify password error response, in this case the description contains the error.
        final VerifyPasswordErrorResponse verifyPasswordErrorResponse = pwdErrorDescResponseParser.parse(body);
        handlePasswordErrorResponse(verifyPasswordErrorResponse);
        // Fallback to default case
        throw new BackendHttpException();
    }


    private void handlePasswordErrorResponse(final VerifyPasswordErrorResponse verifyPasswordErrorResponse) {
        if (verifyPasswordErrorResponse != null && verifyPasswordErrorResponse.getError() != null) {
            handlePasswordError(verifyPasswordErrorResponse);
        }
    }

    private void handleUnauthorized(final ClientHttpResponse response) throws IOException {
        final String body = IOUtils.toString(response.getBody(), Charset.defaultCharset());
        // First check if this is a verify password error response
        final VerifyPasswordErrorResponse verifyPasswordErrorResponse = passwordErrorResponseParser.parse(body);
        // will throw a password exception or just return.
        handlePasswordErrorResponse(verifyPasswordErrorResponse);
        // Generic unauthorized?
        final GenericUnauthorizedErrorResponse genericUnauthorizedErrorResponse =
                unauthorizedResponseParser.parse(body);
        handleGenericUnauthorized(genericUnauthorizedErrorResponse);

        // Fallback to default case
        throw new UnauthorizedRequestException();
    }

    private void handleGenericUnauthorized(final GenericUnauthorizedErrorResponse genericUnauthorizedErrorResponse) {
        if (genericUnauthorizedErrorResponse != null
                && genericUnauthorizedErrorResponse.isValid()
                && genericUnauthorizedErrorResponse.isMaxTriesExceeded()) {
            throw new SmsMaxTriesExceededException();
        }
    }

    private void handlePasswordError(final VerifyPasswordErrorResponse verifyPasswordErrorResponse) {
        final VerifyPasswordError error = verifyPasswordErrorResponse.getError();
        switch (error) {
            case BAD_CREDENTIALS:
                throw new WrongPasswordException();
            case ALMOST_LOCKED:
                throw new WrongPasswordAlmostLockedException();
            case LOCKED:
                throw new WrongPasswordLockedException();
            default:
                throw new HawaiiException("Got unknown error '" + error + "' from SSO.");
        }
    }

}
