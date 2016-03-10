/*
 * Copyright 2015-2016 the original author or authors.
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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnumerableCompositePropertySource;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

/**
 * An {@link EnvironmentPostProcessor} that configures the default Hawaii properties.
 * Note these default properties are added with lowest precedence meaning properties
 * set via e.g. command line, system or application properties will take precedence.
 *
 * @author Marcel Overdijk
 * @since 2.0.0
 */
@Order(Ordered.LOWEST_PRECEDENCE)
public class HawaiiPropertyDefaultsPostProcessor implements EnvironmentPostProcessor {

    private static final String DEFAULT_HAWAII_PROPERTIES = "defaultHawaiiProperties";
    private static final String DEFAULT_HAWAII_PROPERTIES_LOCATION = "/config/hawaii-application.yml";

    private static final String HAWAII_BANNER_LOCATION_PROPERTY_VALUE = "hawaii-banner.txt";

    private YamlPropertySourceLoader propertySourceLoader = new YamlPropertySourceLoader();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // Get the active profiles.
        Set<String> profiles = new LinkedHashSet<>();
        profiles.addAll(Arrays.asList(environment.getActiveProfiles()));

        // The default profile is represented as null.
        // It is added as last as active profiles will take precedence then.
        profiles.add(null);

        EnumerableCompositePropertySource hawaiiDefaultsPropertySource = new EnumerableCompositePropertySource(DEFAULT_HAWAII_PROPERTIES);
        Resource hawaiiDefaultsResource = new ClassPathResource(DEFAULT_HAWAII_PROPERTIES_LOCATION);

        // Load default Hawaii properties
        for (String profile : profiles) {
            try {
                // Load default Hawaii properties for given profile.
                // Can return null if e.g. the given profile is not defined in the the default Hawaii properties.
                String name = DEFAULT_HAWAII_PROPERTIES + "[profile=" + (profile == null ? "" : profile) + "]";
                PropertySource<?> propertySource = propertySourceLoader.load(name, hawaiiDefaultsResource, profile);
                if (propertySource != null) {
                    hawaiiDefaultsPropertySource.add(propertySource);
                }
            } catch (IOException e) {
                // Ignore
            }
        }

        // If the banner location is not explicitly defined and the default banner does not exists,
        // then configure the banner location to use the default Hawaii banner.
        if (environment.getProperty(SpringApplication.BANNER_LOCATION_PROPERTY) == null) {
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource banner = resourceLoader.getResource(SpringApplication.BANNER_LOCATION_PROPERTY_VALUE);
            if (!banner.exists()) {
                Properties properties = new Properties();
                properties.setProperty(SpringApplication.BANNER_LOCATION_PROPERTY, HAWAII_BANNER_LOCATION_PROPERTY_VALUE);
                String name = DEFAULT_HAWAII_PROPERTIES + "[hawaiiBanner]";
                hawaiiDefaultsPropertySource.add(new PropertiesPropertySource(name, properties));
            }
        }

        // Finally add the property source at the last positions so other property sources take precedence.
        environment.getPropertySources().addLast(hawaiiDefaultsPropertySource);
    }
}
