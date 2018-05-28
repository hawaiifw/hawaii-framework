package org.hawaiiframework.logging.model;

import org.slf4j.MDC;

import java.util.Map;

/**
 * Utility to copy the Kibana Log Fields.
 */
public final class KibanaLogContext {

    /**
     * The MDC map.
     */
    private final Map<String, String> contextMap;


    /**
     * Create a new instance, copying the MDC (context map).
     */
    public KibanaLogContext() {
        this.contextMap = MDC.getCopyOfContextMap();
    }

    /**
     * Returns the copied context map.
     */
    public Map<String, String> getContextMap() {
        return contextMap;
    }

    /**
     * Registers the log fields of this {@link KibanaLogContext} into the KibanaLogFields.
     * <p>
     * See {@link KibanaLogFields#populateFromContext(KibanaLogContext)}.
     */
    public void registerKibanaLogFieldsInThisThread() {
        KibanaLogFields.populateFromContext(this);
    }
}
