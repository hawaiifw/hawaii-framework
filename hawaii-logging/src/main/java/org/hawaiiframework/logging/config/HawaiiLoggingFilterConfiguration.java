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

import org.hawaiiframework.logging.config.filter.ContainerNameHttpHeaderFilterConfiguration;
import org.hawaiiframework.logging.config.filter.HawaiiLoggingFilterConfigurationProperties;
import org.hawaiiframework.logging.config.filter.KibanaLogCleanupFilterConfiguration;
import org.hawaiiframework.logging.config.filter.OpentracingResponseFilterConfiguration;
import org.hawaiiframework.logging.config.filter.RequestDurationFilterConfiguration;
import org.hawaiiframework.logging.config.filter.RequestIdFilterConfiguration;
import org.hawaiiframework.logging.config.filter.RequestResponseLogFilterConfiguration;
import org.hawaiiframework.logging.config.filter.TransactionIdFilterConfiguration;
import org.hawaiiframework.logging.config.filter.TransactionTypeFilterConfiguration;
import org.hawaiiframework.logging.config.filter.UserDetailsFilterConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration that includes all filter configurations.
 */
@Configuration
@EnableConfigurationProperties(HawaiiLoggingFilterConfigurationProperties.class)
@Import({
        ContainerNameHttpHeaderFilterConfiguration.class,
        KibanaLogCleanupFilterConfiguration.class,
        OpentracingResponseFilterConfiguration.class,
        RequestDurationFilterConfiguration.class,
        RequestIdFilterConfiguration.class,
        RequestResponseLogFilterConfiguration.class,
        TransactionIdFilterConfiguration.class,
        TransactionTypeFilterConfiguration.class,
        UserDetailsFilterConfiguration.class
})
public class HawaiiLoggingFilterConfiguration {

}
