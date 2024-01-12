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

import org.hawaiiframework.exception.ApiException;

/**
 * Response handler for API errors.
 *
 * @author Paul Klos
 * @since 2.0.0
 */
@SuppressWarnings("PMD.DataClass")
public class ApiErrorResponseResource extends ErrorResponseResource {

  /** The error code. */
  private String apiErrorCode;

  /** The error reason. */
  private String apiErrorReason;

  public ApiErrorResponseResource() {
    this(null);
  }

  public ApiErrorResponseResource(ApiException apiException) {
    super(apiException);
  }

  /**
   * Getter for error code.
   *
   * @return the error code
   */
  public String getApiErrorCode() {
    return apiErrorCode;
  }

  /**
   * Setter for error code.
   *
   * @param apiErrorCode the error code
   */
  public void setApiErrorCode(String apiErrorCode) {
    this.apiErrorCode = apiErrorCode;
  }

  /**
   * Getter for reason.
   *
   * @return the reason
   */
  public String getApiErrorReason() {
    return apiErrorReason;
  }

  /**
   * Setter for reason.
   *
   * @param apiErrorReason the reason
   */
  public void setApiErrorReason(String apiErrorReason) {
    this.apiErrorReason = apiErrorReason;
  }
}
