package org.hawaiiframework.logging.model.kibana.enums;

import org.hawaiiframework.logging.model.kibana.interfaces.KibanaLogCallResultTypes;

/**
 * Enumeration of Call Results.
 */
public enum HawaiiKibanaLogCallResultTypes implements KibanaLogCallResultTypes {
    SUCCESS,
    TIME_OUT,
    BACKEND_FAILURE
}
