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

package org.hawaiiframework.boot.autoconfigure.env;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link HawaiiPropertyDefaultsPostProcessor}.
 *
 * @author Marcel Overdijk
 */
public class HawaiiPropertyDefaultsPostProcessorTests {

    private static final Map<String, Object> defaultHawaiiProperties;
    private static final Map<String, Object> defaultHawaiiDevProperties;
    private static final Map<String, Object> defaultHawaiiTestProperties;
    private static final Map<String, Object> defaultHawaiiProdProperties;

    static {
        defaultHawaiiProperties = new HashMap<>();
        defaultHawaiiProperties.put("banner.location", "hawaii-banner.txt");
        defaultHawaiiProperties.put("logging.file", "log/hawaii.log");
        defaultHawaiiProperties.put("logging.level.org.springframework", "INFO");
        defaultHawaiiProperties.put("logging.level.org.hawaiiframework", "INFO");
        defaultHawaiiProperties.put("spring.jackson.date-format", "com.fasterxml.jackson.databind.util.ISO8601DateFormat");
        defaultHawaiiProperties.put("spring.jackson.property-naming-strategy", "CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES");
        defaultHawaiiProperties.put("spring.jackson.serialization.indent-output", "false");
        defaultHawaiiProperties.put("spring.jackson.serialization.write-dates-as-timestamps", "false");
        defaultHawaiiProperties.put("spring.jackson.serialization.write-date-timestamps-as-nanoseconds", "false");

        defaultHawaiiDevProperties = new HashMap<>(defaultHawaiiProperties);
        defaultHawaiiDevProperties.put("logging.level.org.hawaiiframework", "DEBUG");
        defaultHawaiiDevProperties.put("spring.jackson.serialization.indent-output", "true");

        defaultHawaiiTestProperties = new HashMap<>(defaultHawaiiProperties);

        defaultHawaiiProdProperties = new HashMap<>(defaultHawaiiProperties);
    }

    private HawaiiPropertyDefaultsPostProcessor processor;
    private ConfigurableApplicationContext context;

    @Before
    public void setUp() {
        this.processor = new HawaiiPropertyDefaultsPostProcessor();
        this.context = new AnnotationConfigApplicationContext();
    }

    @Test
    public void testDefaultHawaiiProperties() {
        processor.postProcessEnvironment(context.getEnvironment(), null);
        Map<String, Object> expected = new HashMap<>(defaultHawaiiProperties);
        expected.forEach((key, value) -> {
            assertThat(getEnvProperty(key), is(equalTo(value)));
        });
    }

    @Test
    public void testDefaultHawaiiPropertiesDevProfile() {
        context.getEnvironment().addActiveProfile("dev"); // set dev profile active
        processor.postProcessEnvironment(context.getEnvironment(), null);
        Map<String, Object> expected = new HashMap<>(defaultHawaiiDevProperties);
        expected.forEach((key, value) -> {
            assertThat(getEnvProperty(key), is(equalTo(value)));
        });
    }

    @Test
    public void testDefaultHawaiiPropertiesTestProfile() {
        context.getEnvironment().addActiveProfile("test"); // set test profile active
        processor.postProcessEnvironment(context.getEnvironment(), null);
        Map<String, Object> expected = new HashMap<>(defaultHawaiiTestProperties);
        expected.forEach((key, value) -> {
            assertThat(getEnvProperty(key), is(equalTo(value)));
        });
    }

    @Test
    public void testDefaultHawaiiPropertiesProdProfile() {
        context.getEnvironment().addActiveProfile("prod"); // set prod profile active
        processor.postProcessEnvironment(context.getEnvironment(), null);
        Map<String, Object> expected = new HashMap<>(defaultHawaiiProdProperties);
        expected.forEach((key, value) -> {
            assertThat(getEnvProperty(key), is(equalTo(value)));
        });
    }

    @Test
    public void testCustomBannerLocation() {
        EnvironmentTestUtils.addEnvironment(context, "banner.location: classpath:my-banner.txt");
        processor.postProcessEnvironment(context.getEnvironment(), null);
        assertThat(getEnvProperty("banner.location"), is(equalTo("classpath:my-banner.txt")));
    }

    private String getEnvProperty(String key) {
        return context.getEnvironment().getProperty(key);
    }
}
