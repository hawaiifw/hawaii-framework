/*
 * Copyright 2015-2020 the original author or authors.
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

package org.hawaiiframework.logging.oidc;

import java.util.Arrays;
import org.hawaiiframework.logging.model.KibanaLogField;

/**
 * Kibana Log Field Names for OIDC fields.
 *
 * @author Rutger Lubbers
 * @since 3.0.0.M21
 */
public enum OidcKibanaLogFieldNames implements KibanaLogField {
  /** The subject. */
  SUBJECT("sub"),
  /** The audience. */
  AUDIENCE("aud"),
  /** The authorized party. */
  AUTHORIZED_PARTY("azp"),
  /** The user id. */
  USER_ID("user_id");

  /** The logging key for this MDC entry. */
  private final String fieldName;

  /**
   * @param fieldName the kibana log's field name..
   */
  OidcKibanaLogFieldNames(String fieldName) {
    this.fieldName = fieldName;
  }

  /**
   * @return the kibana log's field name.
   */
  @Override
  public String getLogName() {
    return fieldName;
  }

  /**
   * Lookup method that does not throw an exception if the specified key is not found.
   *
   * @param key the key to look for
   * @return the KibanaLogField with the given name, or null
   */
  @SuppressWarnings("PMD.LawOfDemeter")
  public static OidcKibanaLogFieldNames fromKey(String key) {
    OidcKibanaLogFieldNames result = null;
    if (key != null) {
      result =
          Arrays.stream(values())
              .filter(fieldName -> fieldName.matches(key))
              .findAny()
              .orElse(null);
    }
    return result;
  }
}
