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
package org.hawaiiframework.logging.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;

/**
 * Utility class to determine the client IP address.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class ClientIpResolver {

    /**
     * The name of the request header that contains the client ip address.
     */
    @Value("${hawaii.logging.frontend-ip-header-name}")
    private String frontendIpHeader;

    /**
     * Extract the client IP address from the HttpServletRequest.
     *
     * @param request the request
     * @return the IP address
     */
    public String getClientIp(final HttpServletRequest request) {
        String address = request.getRemoteAddr();
        if (StringUtils.isNotBlank(frontendIpHeader)) {
            final String headerValue = request.getHeader(frontendIpHeader);
            if (StringUtils.isNotBlank(headerValue)) {
                address = headerValue;
            }
        }
        return address;
    }

}
