package org.hawaiiframework.logging.model;

import org.slf4j.MDC;

import java.util.Map;

/**
 * Utility to copy the Kibana Log Fields.
 */
public final class KibanaLogContextMap {

    /**
     * The MDC map.
     */
    private final Map<String, String> contextMap;


    /**
     * Create a new instance, copying the MDC (context map).
     */
    public KibanaLogContextMap() {
        this.contextMap = MDC.getCopyOfContextMap();
    }

    /**
     * Returns the copied context map.
     */
    public Map<String, String> getContextMap() {
        return contextMap;
    }
}
