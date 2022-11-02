package org.hawaiiframework.web.util;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests for {@link HostResolver}.
 *
 * @author Marcel Overdijk
 */
public class HostResolverTest {

    private MockHttpServletRequest httpServletRequest;

    private HostResolver hostResolver;

    @Before
    public void setUp() {
        httpServletRequest = new MockHttpServletRequest();
        hostResolver = new HostResolver();
    }

    @Test
    public void testGetBaseUrl() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpServletRequest));
        httpServletRequest.addHeader(HttpHeaders.HOST, "example.com");
        httpServletRequest.setScheme("https");
        assertThat(hostResolver.getBaseUrl(), is(equalTo("https://example.com")));
    }
}
