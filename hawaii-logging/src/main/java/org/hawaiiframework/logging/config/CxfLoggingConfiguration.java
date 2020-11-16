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
package org.hawaiiframework.logging.config;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the Apache CXF bus to use logging interceptors.
 */
@ConditionalOnClass(name = "org.apache.cxf.Bus")
@ConditionalOnProperty(name = "hawaii.logging.soap.enabled")
@Configuration
public class CxfLoggingConfiguration {

    /**
     * Configures the Apache CXF bus to use logging interceptors.
     * @return the Apache CXF bus.
     */
    @Bean
    public Bus busConfiguration() {
        final Bus bus = BusFactory.getDefaultBus();
        bus.getInInterceptors().add(loggingInInterceptor());
        bus.getInFaultInterceptors().add(loggingInInterceptor());
        bus.getOutInterceptors().add(loggingOutInterceptor());
        bus.getOutFaultInterceptors().add(loggingOutInterceptor());
        return bus;
    }

    private LoggingInInterceptor loggingInInterceptor() {
        final LoggingInInterceptor inInterceptor = new LoggingInInterceptor();
        inInterceptor.setPrettyLogging(true);
        return inInterceptor;
    }

    private LoggingOutInterceptor loggingOutInterceptor() {
        final LoggingOutInterceptor outInterceptor = new LoggingOutInterceptor();
        outInterceptor.setPrettyLogging(true);
        return outInterceptor;
    }
}
