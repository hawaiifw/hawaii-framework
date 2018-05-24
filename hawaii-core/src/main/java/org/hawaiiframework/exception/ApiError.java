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

package org.hawaiiframework.exception;

/**
 * Interface that defines an API error of the application.
 *
 * An API error is a well-defined error situation with a unique error code.
 *
 * An application built on Hawaii Framework might implement this using an enum.
 *
 * @author Paul Klos
 * @since 2.0.0
 */
public interface ApiError {

    /**
     * Getter for the error code.
     *
     * @return the error code
     */
    String getErrorCode();

    /**
     * Get the error reason.
     *
     * @return the reason
     */
    String getReason();

}
