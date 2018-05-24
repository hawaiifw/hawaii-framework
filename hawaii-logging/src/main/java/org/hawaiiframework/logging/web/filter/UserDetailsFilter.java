/*
 * Copyright 2015-2018 the original author or authors.
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
package org.hawaiiframework.logging.web.filter;

import org.hawaiiframework.logging.model.KibanaLogFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.hawaiiframework.logging.model.KibanaLogFieldNames.USER;

/**
 * A filter that adds the logged in user (UserDetails) to the Kibana Log Fields.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class UserDetailsFilter extends OncePerRequestFilter {

    /**
     * The Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsFilter.class);

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
            final Object principal = auth.getPrincipal();
            LOGGER.debug("Request called by '{}'.", principal);
            if (principal instanceof UserDetails) {
                final UserDetails userDetails = (UserDetails) principal;
                final String username = userDetails.getUsername();
                KibanaLogFields.set(USER, username);
                LOGGER.debug("User has user name '{}'.", username);
            }
        }

        filterChain.doFilter(request, response);
    }

}
