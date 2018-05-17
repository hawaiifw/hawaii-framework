package org.hawaiiframework.security.sso.web.controller;

import org.hawaiiframework.errors.web.UnauthorizedRequestException;
import org.hawaiiframework.security.sso.model.Credentials;
import org.hawaiiframework.security.sso.service.ValidatingUserFactory;
import org.hawaiiframework.security.sso.web.input.CredentialsInput;
import org.hawaiiframework.security.sso.web.input.CredentialsInputValidator;
import org.hawaiiframework.security.sso.web.resource.TokenVerificationResourceAssembler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Controller to check the user's Access Token, ID Token and nonce.
 */
@RestController
public class TokenVerificationController {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenVerificationController.class);

    /**
     * The validating user factory.
     */
    private final ValidatingUserFactory validatingUserFactory;

    /**
     * The CredentialsInput validator.
     */
    private final CredentialsInputValidator credentialsInputValidator;

    /**
     * The TokenVerificationResource assembler.
     */
    @SuppressWarnings("PMD.LongVariable")
    private final TokenVerificationResourceAssembler tokenVerificationResourceAssembler;

    /**
     * The constructor.
     */
    @SuppressWarnings("PMD.LongVariable")
    @Autowired
    public TokenVerificationController(final ValidatingUserFactory validatingUserFactory,
            final CredentialsInputValidator credentialsInputValidator,
            final TokenVerificationResourceAssembler tokenVerificationResourceAssembler) {
        this.validatingUserFactory = requireNonNull(validatingUserFactory);
        this.credentialsInputValidator = requireNonNull(credentialsInputValidator);
        this.tokenVerificationResourceAssembler = requireNonNull(tokenVerificationResourceAssembler);
    }

    /**
     * Validates the ID Token, Access Token and nonce combination.
     */
    @RequestMapping(value = Paths.CHECK_TOKEN, method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> checkTokenValidity(@RequestBody final CredentialsInput input) {
        credentialsInputValidator.validateAndThrow(input);
        final Credentials credentials = transform(input);

        validatingUserFactory.validateUser(credentials);
        final String failureReason = credentials.getValidationFailureReason();
        if (failureReason != null) {
            final String reason = String.format("Could not verify credentials: %s.", failureReason);
            LOGGER.debug(reason);
            throw new UnauthorizedRequestException(reason);
        }

        return ResponseEntity.ok().body(tokenVerificationResourceAssembler.convert(credentials));
    }

    private Credentials transform(final CredentialsInput input) {
        final Credentials credentials = new Credentials();
        credentials.setAccessToken(input.getAccessToken());
        credentials.setIdToken(input.getIdToken());
        credentials.setNonce(input.getNonce());

        return credentials;
    }
}
