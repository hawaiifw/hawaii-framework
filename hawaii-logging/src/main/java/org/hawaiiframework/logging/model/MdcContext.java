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

package org.hawaiiframework.logging.model;

import org.slf4j.MDC;

import java.util.Map;

/**
 * Utility to copy the MDC from one thread to another thread.
 *
 * <p>
 * An example of the usage of this class, for a parallel stream:
 * <pre>
 *  MdcContext context = MdcContext.getCurrentMdc();
 *
 *  // stream and set context:
 *  list.parallelStream().forEach(entry -> { mdcContext.populateMdc(); ... })
 *
 * </pre>
 */
public final class MdcContext {

    /**
     * The MDC map.
     */
    private final Map<String, String> contextMap;

    /**
     * Create a new instance, copying the MDC (context map).
     */
    private MdcContext() {
        this.contextMap = MDC.getCopyOfContextMap();
    }

    /**
     * Create a new instance.
     */
    public static MdcContext getCurrentMdc() {
        return new MdcContext();
    }

    /**
     * Set the copied MDC context map into the current thread's MDC.
     */
    public void populateMdc() {
        MDC.clear();
        if (contextMap != null) {
            MDC.setContextMap(contextMap);
        }
    }
}
