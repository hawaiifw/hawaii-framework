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

package org.hawaiiframework.web.input;

/**
 * Interface for components that convert input data into a domain object.
 * <p>
 * The input should be a type that is only used in the REST layer of the application and which represents the data structure to be send by
 * the consumer. This is typically a POJO containing Jackson annotations if needed.
 *
 * @param <T> the type of the input object
 * @param <D> the type of the domain object
 * @author Wouter Eerdekens
 * @author Marcel Overdijk
 * @since 2.0.0
 */
public interface InputConverter<T, D> {

    /**
     * Converts the given input into a domain object.
     */
    D convert(T input);
}
