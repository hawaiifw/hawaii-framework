/*
 * Copyright 2015-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hawaiiframework.logging.oidc;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import org.apache.commons.lang3.StringUtils;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.hawaiiframework.logging.web.filter.AbstractGenericFilterBean;
import org.hawaiiframework.logging.web.filter.UserDetailsFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

import static org.hawaiiframework.logging.oidc.OidcKibanaLogFieldNames.AUDIENCE;
import static org.hawaiiframework.logging.oidc.OidcKibanaLogFieldNames.AUTHORIZED_PARTY;
import static org.hawaiiframework.logging.oidc.OidcKibanaLogFieldNames.SUBJECT;
import static org.hawaiiframework.logging.oidc.OidcKibanaLogFieldNames.USER_ID;

/**
 * A filter that adds the trace id to the response header.
 *
 * @author Rutger Lubbers
 * @since 3.0.0.M21
 */
public class OidcLogFilter extends AbstractGenericFilterBean {

    /**
     * The Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsFilter.class);

    /**
     * The prefix in the authorization header.
     */
    private static final String BEARER = "Bearer ";

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {
        final String header = request.getHeader("Authorization");
        if (StringUtils.isNotBlank(header) && header.startsWith(BEARER)) {
            final String token = header.substring(BEARER.length());
            try {
                final JWT jwt = JWTParser.parse(token);

                final JWTClaimsSet claimsSet = jwt.getJWTClaimsSet();
                KibanaLogFields.tag(SUBJECT, claimsSet.getSubject());
                KibanaLogFields.tag(AUDIENCE, claimsSet.getAudience());
                KibanaLogFields.tag(AUTHORIZED_PARTY, claimsSet.getStringClaim("azp"));
                KibanaLogFields.tag(USER_ID, claimsSet.getStringClaim("user_id"));
            } catch (ParseException e) {
                // Invalid plain JWT encoding
                LOGGER.warn("Caught exception parsing JWT.", e);
            }

        }

        filterChain.doFilter(request, response);
    }

}
