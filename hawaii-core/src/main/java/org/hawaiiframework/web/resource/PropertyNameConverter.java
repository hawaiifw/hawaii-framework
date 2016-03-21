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

package org.hawaiiframework.web.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

/**
 * @author Marcel Overdijk
 * @since 2.0.0
 */
public class PropertyNameConverter implements Converter<String, String> {

    private final ObjectMapper objectMapper;

    public PropertyNameConverter(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "Object mapper must not be null");
        this.objectMapper = objectMapper;
    }

    /**
     * Converts the given property name (field name or error code) using the application defined
     * {@link com.fasterxml.jackson.databind.PropertyNamingStrategy} for consistent output in responses.
     * The naming strategy is defined in application.yml via the 'spring.jackson.property-naming-strategy' property.
     * <p>
     * <p>For example, if the {@link com.fasterxml.jackson.databind.PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy}
     * is defined, the following field names and error codes will be translated:
     * <ol>
     * <li>description -> description</li>
     * <li>grossPrice -> gross_price</li>
     * <li>InvalidLength -> invalid_length</li>
     * </ol>
     */
    @Override
    public String convert(String propertyName) {
        if (propertyName == null || propertyName.length() == 0) {
            return propertyName;
        }
        // retrieve the application defined property naming strategy from the object mapper's serialization config
        return objectMapper.getSerializationConfig().getPropertyNamingStrategy().nameForField(null, null, propertyName);
    }
}
