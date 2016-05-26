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

package org.hawaiiframework.boot.autoconfigure.jackson;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * @author Marcel Overdijk
 * @since 2.0.0
 */
@Configuration
public class HawaiiJacksonAutoConfiguration {

    /**
     * Returns a Jackson {@link Module} capable of serializing {@code java.time} objects
     * {@link ObjectMapper}.
     */
    @Bean
    public JavaTimeModule javaTimeModule() {
        return new JavaTimeModule();
    }

    /**
     * Returns a Jackson {@link Module} capable of serializing {@code org.json} objects with
     * {@link ObjectMapper}.
     */
    @Bean
    public JsonOrgModule jsonOrgModule() {
        return new JsonOrgModule();
    }
}
