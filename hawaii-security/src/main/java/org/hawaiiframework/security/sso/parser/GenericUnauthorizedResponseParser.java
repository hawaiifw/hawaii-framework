package org.hawaiiframework.security.sso.parser;

import org.hawaiiframework.security.sso.model.GenericUnauthorizedErrorResponse;
import org.springframework.stereotype.Component;

/**
 * Parser for a generic unauthorized response from SSO.
 */
@Component
public class GenericUnauthorizedResponseParser extends BaseUnauthorizedResponseParser<GenericUnauthorizedErrorResponse> {

    /**
     * Construct an instance for {@link GenericUnauthorizedErrorResponse}.
     */
    public GenericUnauthorizedResponseParser() {
        super(GenericUnauthorizedErrorResponse.class);
    }

}
