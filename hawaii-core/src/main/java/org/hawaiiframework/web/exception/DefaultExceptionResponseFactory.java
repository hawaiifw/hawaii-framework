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

package org.hawaiiframework.web.exception;

import org.hawaiiframework.exception.ApiException;
import org.hawaiiframework.exception.HawaiiException;
import org.hawaiiframework.validation.ValidationException;
import org.hawaiiframework.web.resource.ApiErrorResponseResource;
import org.hawaiiframework.web.resource.ErrorResponseResource;
import org.hawaiiframework.web.resource.MethodArgumentNotValidResponseResource;
import org.hawaiiframework.web.resource.ValidationErrorResponseResource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * Default implementation of {@link ExceptionResponseFactory}.
 *
 * <p>The default implementation creates an {@link ApiErrorResponseResource} if the exception is an
 * {@link ApiException} and a {@link ErrorResponseResource} in all other cases.
 *
 * @author Paul Klos
 * @since 2.0.0
 */
@Component
public class DefaultExceptionResponseFactory implements ExceptionResponseFactory {

  /**
   * Create the response resource.
   *
   * <p>If present, the first {@link HawaiiException} found in the cause chain of the throwable is
   * used to determine the response type. If there is no cause, or if it doesn't contain a {@link
   * HawaiiException}, the throwable itself is used.
   *
   * <p>As an example, assume throwable is some type of {@link HttpException}, caused by an {@link
   * ApiException}. In such a case, we want the error information to be derived from the {@link
   * ApiException}.
   *
   * @param throwable the throwable
   * @return the error resource
   */
  @Override
  public ErrorResponseResource create(Throwable throwable) {
    ErrorResponseResource result = null;
    if (throwable != null) {

      result = getErrorResponseResource(throwable);

      if (result == null) {
        Throwable cause = getCausingHawaiiException(throwable);
        result = getErrorResponseResource(cause);
      }
    }
    if (result == null) {
      result = new ErrorResponseResource(throwable);
    }
    return result;
  }

  /**
   * Create an instance of the correct type of error resource for the given throwable.
   *
   * @param throwable the throwable
   * @return the error resource
   */
  private static ErrorResponseResource getErrorResponseResource(Throwable throwable) {
    ErrorResponseResource result;
    if (throwable instanceof ApiException apiException) {
      result = new ApiErrorResponseResource(apiException);
    } else if (throwable
        instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
      result = new MethodArgumentNotValidResponseResource(methodArgumentNotValidException);
    } else if (throwable instanceof ValidationException validationException) {
      result = new ValidationErrorResponseResource(validationException);
    } else {
      result = null;
    }
    return result;
  }

  /**
   * Returns the first {@link HawaiiException} encountered in the chain of exception causes, or the
   * original throwable if no {@link HawaiiException} can be found.
   *
   * @param throwable the Throwable to examine, must not be <code>null</code>
   * @return a HawaiiException, or throwable
   */
  private static Throwable getCausingHawaiiException(Throwable throwable) {
    Throwable cause = getCause(throwable.getCause());
    if (cause == null) {
      cause = throwable;
    }
    return cause;
  }

  /**
   * Recursive method to find the cause of a Throwable, if that is a {@link HawaiiException}.
   *
   * @param throwable the throwable
   * @return throwable, or null
   */
  private static Throwable getCause(Throwable throwable) {
    Throwable cause;
    if (throwable == null) {
      cause = null;
    } else if (throwable instanceof HawaiiException) {
      cause = throwable;
    } else {
      cause = getCause(throwable.getCause());
    }
    return cause;
  }
}
