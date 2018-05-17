package org.hawaiiframework.security.sso.web.resource;

import org.hawaiiframework.converter.AbstractModelConverter;
import org.hawaiiframework.security.sso.model.Credentials;
import org.springframework.stereotype.Component;

/**
 * Transforms the user's credentials to the verification response.
 * {@inheritDoc}
 */
@Component
public class TokenVerificationResourceAssembler extends AbstractModelConverter<Credentials, TokenVerificationResource> {

    /**
     * Constructor that sets up the {@link AbstractModelConverter} for the {@link TokenVerificationResource} class.
     */
    public TokenVerificationResourceAssembler() {
        super(TokenVerificationResource.class);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    @Override
    public void convert(final Credentials source, final TokenVerificationResource target) {
        target.setUserSessionId(source.getIdToken().getUserSessionId());
    }
}
