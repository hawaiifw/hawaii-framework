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

package org.hawaiiframework.logging.config.filter;

/**
 * Extension of {@link HttpHeaderLoggingFilterProperties} with extra properties.
 */
public class ContainerNameHttpHeaderFilterProperties extends HttpHeaderLoggingFilterProperties {

    /**
     * The hostname to log.
     */
    private String hostname;

    @SuppressWarnings("PMD.CommentRequired")
    public String getHostname() {
        return hostname;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setHostname(final String hostname) {
        this.hostname = hostname;
    }
}
