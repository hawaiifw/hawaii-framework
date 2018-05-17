package org.hawaiiframework.security.sso.web.input;

import org.hawaiiframework.validation.ValidationResult;
import org.hawaiiframework.validation.Validator;
import org.springframework.stereotype.Component;

/**
 * Validator for credentials.
 */
@Component
public class CredentialsInputValidator implements Validator<CredentialsInput> {

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    @Override
    public void validate(final CredentialsInput input, final ValidationResult validationResult) {
        validationResult.rejectField("access_token", input.getAccessToken())
                .whenNull()
                .orWhen(String::isEmpty);

        validationResult.rejectField("id_token", input.getIdToken())
                .whenNull()
                .orWhen(String::isEmpty);

        validationResult.rejectField("nonce", input.getNonce())
                .whenNull()
                .orWhen(String::isEmpty);
    }
}
