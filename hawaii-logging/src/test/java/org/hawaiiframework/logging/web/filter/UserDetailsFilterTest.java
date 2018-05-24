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

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

public class UserDetailsFilterTest {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;
    private SecurityContext securityContext;
    private UserDetailsFilter filter;

    @Before
    public void setup() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        securityContext = mock(SecurityContext.class);
        filter = new UserDetailsFilter();
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void thatUserDetailsFilterTestSucceedsOnNullAuthentication() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(null);
        filter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void thatUserDetailsFilterTestSucceedsOnAuthenticationInstanceOfAnonymousAuthenticationToken() throws Exception {
        Authentication auth = mock(AnonymousAuthenticationToken.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        filter.doFilterInternal(request, response, filterChain);
        verify(auth, times(0)).getPrincipal();
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void thatUserDetailsFilterTestSucceedsOnAuthenticationInstanceOfAbstractAuthenticationToken() throws Exception {
        Authentication auth = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        filter.doFilterInternal(request, response, filterChain);
        verify(auth, times(1)).getPrincipal();
        verify(filterChain, times(1)).doFilter(request, response);

    }
}
