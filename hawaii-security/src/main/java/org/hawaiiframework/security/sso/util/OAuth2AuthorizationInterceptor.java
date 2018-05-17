package org.hawaiiframework.security.sso.util;

import org.hawaiiframework.security.sso.model.Credentials;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * {@link ClientHttpRequestInterceptor} to apply an OAuth2 authorization header.
 */
public class OAuth2AuthorizationInterceptor implements ClientHttpRequestInterceptor {

    /**
     * The user's credentials.
     */
    private final Credentials credentials;

    /**
     * Create a new interceptor which adds am OAuth2 authorization header for the given credentials.
     *
     * @param credentials the credentials (not <code>null</code>) to use, note the credential's access token must not be null.
     */
    public OAuth2AuthorizationInterceptor(final Credentials credentials) {
        requireNonNull(credentials);
        requireNonNull(credentials.getAccessToken());
        this.credentials = credentials;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution)
            throws IOException {
        request.getHeaders().add("Authorization", "Bearer " + credentials.getAccessToken());
        return execution.execute(request, body);
    }

}
