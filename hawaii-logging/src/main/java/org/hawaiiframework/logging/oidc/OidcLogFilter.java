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

import static org.hawaiiframework.logging.oidc.OidcKibanaLogFieldNames.AUDIENCE;
import static org.hawaiiframework.logging.oidc.OidcKibanaLogFieldNames.AUTHORIZED_PARTY;
import static org.hawaiiframework.logging.oidc.OidcKibanaLogFieldNames.SUBJECT;
import static org.hawaiiframework.logging.oidc.OidcKibanaLogFieldNames.USER_ID;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.hawaiiframework.logging.web.filter.AbstractGenericFilterBean;
import org.hawaiiframework.logging.web.filter.UserDetailsFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A filter that adds the trace id to the response header.
 *
 * @author Rutger Lubbers
 * @since 3.0.0.M21
 */
public class OidcLogFilter extends AbstractGenericFilterBean {

  /** The Logger. */
  private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsFilter.class);

  /** The prefix in the authorization header. */
  private static final String BEARER = "Bearer ";

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("PMD.LawOfDemeter")
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String header = request.getHeader("Authorization");
    if (StringUtils.isNotBlank(header) && header.startsWith(BEARER)) {
      String token = header.substring(BEARER.length());
      try {
        JWT jwt = JWTParser.parse(token);

        JWTClaimsSet claimsSet = jwt.getJWTClaimsSet();
        KibanaLogFields.tag(SUBJECT, claimsSet.getSubject());
        KibanaLogFields.tag(AUDIENCE, claimsSet.getAudience());
        KibanaLogFields.tag(AUTHORIZED_PARTY, claimsSet.getStringClaim("azp"));
        KibanaLogFields.tag(USER_ID, claimsSet.getStringClaim("user_id"));
      } catch (ParseException exception) {
        // Invalid plain JWT encoding
        LOGGER.warn("Caught exception parsing JWT.", exception);
      }
    }

    filterChain.doFilter(request, response);
  }
}
