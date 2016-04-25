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
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.hawaiiframework.validation.ValidationError;
import org.springframework.util.Assert;

/**
 * @author Marcel Overdijk
 * @since 2.0.0
 */
public class ValidationErrorResourceAssembler implements ResourceAssembler<ValidationError, ValidationErrorResource> {

    private final ObjectMapper objectMapper;

    public ValidationErrorResourceAssembler() {
        this.objectMapper = null;
    }

    public ValidationErrorResourceAssembler(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "Object mapper must not be null");
        this.objectMapper = objectMapper;
    }

    @Override
    public ValidationErrorResource toResource(ValidationError validationError) {
        Assert.notNull(validationError, "Validation error must not be null");
        String field = convertProperty(validationError.getField());
        String code = convertProperty(validationError.getCode());
        ValidationErrorResource resource = new ValidationErrorResource();
        resource.setField(field);
        resource.setCode(code);
        return resource;
    }

    /**
     * Converts the given property name (field name or error code) using the application defined
     * {@link com.fasterxml.jackson.databind.PropertyNamingStrategy} for consistent output in responses.
     * The naming strategy is defined in application.yml via the 'spring.jackson.property-naming-strategy' property.
     *
     * <p>For example, if the {@link com.fasterxml.jackson.databind.PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy}
     * is defined, the following field names and error codes will be translated as following:
     * <ol>
     * <li>description -> description</li>
     * <li>price -> price</li>
     * <li>discountPrice -> discount_price</li>
     * <li>Required -> required</li>
     * <li>InvalidLength -> invalid_length</li>
     * </ol>
     */
    protected String convertProperty(String propertyName) {
        if (objectMapper == null || propertyName == null || propertyName.length() == 0) {
            return propertyName;
        }
        // retrieve the application defined property naming strategy from the object mapper's serialization config
        PropertyNamingStrategy propertyNamingStrategy = objectMapper.getSerializationConfig().getPropertyNamingStrategy();
        if (propertyNamingStrategy == null) {
            return propertyName;
        }
        return propertyNamingStrategy.nameForField(null, null, propertyName);
    }
}
