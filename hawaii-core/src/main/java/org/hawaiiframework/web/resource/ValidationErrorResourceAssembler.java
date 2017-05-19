/*
 * Copyright 2015-2017 the original author or authors.
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
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.hawaiiframework.validation.ValidationError;

import static java.util.Objects.requireNonNull;

/**
 * @author Marcel Overdijk
 * @since 2.0.0
 */
public class ValidationErrorResourceAssembler extends AbstractResourceAssembler<ValidationError, ValidationErrorResource> {

    private final ObjectMapper objectMapper;

    public ValidationErrorResourceAssembler(final ObjectMapper objectMapper) {
        super(ValidationErrorResource.class);
        this.objectMapper = requireNonNull(objectMapper, "'objectMapper' must not be null");
    }

    @Override
    public void toResource(final ValidationError validationError, final ValidationErrorResource resource) {
        requireNonNull(validationError, "'validationError' must not be null");
        final String field = convertProperty(validationError.getField());
        final String code = convertProperty(validationError.getCode());
        resource.setField(field);
        resource.setCode(code);
    }

    /**
     * Converts the given property name (field name or error code) using the application defined
     * {@link com.fasterxml.jackson.databind.PropertyNamingStrategy} for consistent output in responses. The naming strategy is defined in
     * {@code application.yml} via the {@code spring.jackson.property-naming-strategy} property.
     * <p>
     * For example, if the {@link com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy} is defined,
     * the following field names and error codes will be translated as following:
     * <ol>
     * <li>description -> description</li>
     * <li>price -> price</li>
     * <li>discountPrice -> discount_price</li>
     * <li>Required -> required</li>
     * <li>InvalidLength -> invalid_length</li>
     * </ol>
     */
    protected String convertProperty(final String propertyName) {
        final String name;
        if (objectMapper == null || propertyName == null || propertyName.length() == 0) {
            name = propertyName;
        } else {
            // retrieve the application defined property naming strategy from the object mapper's serialization config
            final PropertyNamingStrategy propertyNamingStrategy = objectMapper.getSerializationConfig().getPropertyNamingStrategy();
            if (propertyNamingStrategy == null) {
                name = propertyName;
            } else {
                name = propertyNamingStrategy.nameForField(null, null, propertyName);
            }
        }
        return name;
    }
}
