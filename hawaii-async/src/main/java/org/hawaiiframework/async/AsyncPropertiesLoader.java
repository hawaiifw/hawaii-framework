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

package org.hawaiiframework.async;

import org.hawaiiframework.async.model.ExecutorConfigurationProperties;
import org.hawaiiframework.exception.HawaiiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.yaml.snakeyaml.Yaml;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

/**
 * Helper class to actually load the async configuration properties from the configuration file.
 *
 * @author Paul Klos
 * @since 2.0.0
 */
public class AsyncPropertiesLoader {
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncExecutorConfiguration.class);

    /**
     * The configuration file path.
     */
    private final String configFile;

    /**
     * Construct a loader for a configuration file.
     *
     * @param configFile the configuration file
     */
    public AsyncPropertiesLoader(final String configFile) {
        this.configFile = configFile;
    }

    /**
     * Load the properties from the configuration file.
     *
     * @return the properties
     */
    // getObject() throws Exception
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public ExecutorConfigurationProperties loadProperties() {
        try {
            return loadYamlExecutorConfigurationProperties(new String(Files.readAllBytes(Paths.get(configFile)), "UTF-8"));
        } catch (Exception e) {
            LOGGER.error("Unable to load async configuration file");
            throw new HawaiiException(e);
        }
    }

    private static ExecutorConfigurationProperties loadYamlExecutorConfigurationProperties(final String yaml) {
        Assert.state(yaml != null, "Yaml document should not be null: "
                + "either set it directly or set the resource to load it from");
        return new Yaml(
                new YamlJavaBeanPropertyConstructor(
                        ExecutorConfigurationProperties.class,
                        Collections.emptyMap()))
                .load(yaml);
    }
}
