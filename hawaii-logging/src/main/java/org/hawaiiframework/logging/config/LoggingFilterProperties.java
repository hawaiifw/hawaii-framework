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
package org.hawaiiframework.logging.config;

/**
 * Base class for logging filter properties.
 *
 * If a filter requires additional properties, this class should be extended to model them.
 *
 * @author Paul Klos
 * @author Wouter Eerdekens
 * @since 2.0.0
 */
@SuppressWarnings("PMD.DataClass")
public class LoggingFilterProperties {

    /**
     * Flag indicating if the filter is enabled.
     */
    private boolean enabled;

    /**
     * The order of the filter in the filter chain.
     */
    private int order;


    /**
     * Getter for the enabled flag.
     *
     * @return the enabled flag
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Setter for the enabled flag.
     *
     * @param enabled the enabled flag
     */
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Getter for the order.
     *
     * @return the order
     */
    public int getOrder() {
        return order;
    }

    /**
     * Setter for the order.
     *
     * @param order the order
     */
    public void setOrder(final int order) {
        this.order = order;
    }
}
