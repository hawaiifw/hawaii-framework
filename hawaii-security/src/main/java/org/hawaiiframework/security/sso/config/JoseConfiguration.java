package org.hawaiiframework.security.sso.config;

import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.nimbusds.jose.proc.JWSVerifierFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to setup token/encryption related beans.
 */
@Configuration
public class JoseConfiguration {

    /**
     * Create a JWS verifier factory, used to validate signed JWT tokens.
     *
     * @return the factory, currently a {@link DefaultJWSVerifierFactory}
     */
    @Bean
    public JWSVerifierFactory jwsVerifierFactory() {
        return new DefaultJWSVerifierFactory();
    }

}
