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

package org.hawaiiframework.web.resource;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.hawaiiframework.converter.AbstractModelConverter;
import org.hawaiiframework.validation.ValidationError;

/**
 * @author Marcel Overdijk
 * @since 2.0.0
 */
public class ValidationErrorResourceAssembler
    extends AbstractModelConverter<ValidationError, ValidationErrorResource> {

  private final ObjectMapper objectMapper;

  /**
   * Constructor with an {@code objectMapper}.
   */
  public ValidationErrorResourceAssembler(ObjectMapper objectMapper) {
    super(ValidationErrorResource.class);
    this.objectMapper = requireNonNull(objectMapper, "'objectMapper' must not be null");
  }

  @Override
  public void convert(ValidationError validationError, ValidationErrorResource resource) {
    requireNonNull(validationError, "'validationError' must not be null");
    String field = convertProperty(validationError.getField());
    String code = convertProperty(validationError.getCode());
    resource.setField(field);
    resource.setCode(code);
  }

  /**
   * Converts the given property name (field name or error code) using the application defined
   * {@link com.fasterxml.jackson.databind.PropertyNamingStrategy} for consistent output in
   * responses. The naming strategy is defined in {@code application.yml} via the {@code
   * spring.jackson.property-naming-strategy} property.
   *
   * <p>For example, if the {@link
   * com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy} is defined, the
   * following field names and error codes will be translated as following:
   *
   * <ul>
   *   <li>description -&gt; description
   *   <li>price -&gt; price
   *   <li>discountPrice -&gt; discount_price
   *   <li>Required -&gt; required
   *   <li>InvalidLength -&gt; invalid_length
   * </ul>
   */
  protected String convertProperty(String propertyName) {
    String name;
    if (objectMapper == null || propertyName == null || propertyName.length() == 0) {
      name = propertyName;
    } else {
      // retrieve the application defined property naming strategy from the object mapper's
      // serialization config
      PropertyNamingStrategy propertyNamingStrategy =
          objectMapper.getSerializationConfig().getPropertyNamingStrategy();
      if (propertyNamingStrategy == null) {
        name = propertyName;
      } else {
        name = propertyNamingStrategy.nameForField(null, null, propertyName);
      }
    }
    return name;
  }
}
