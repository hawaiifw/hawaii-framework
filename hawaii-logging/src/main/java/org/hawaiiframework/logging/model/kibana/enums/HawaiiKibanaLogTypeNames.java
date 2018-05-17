package org.hawaiiframework.logging.model.kibana.enums;

import org.hawaiiframework.logging.model.kibana.interfaces.KibanaLogTypeNames;

/**
 * Enumeration for values of the Kibana Log Field LOG_TYPE.
 */
public enum HawaiiKibanaLogTypeNames implements KibanaLogTypeNames {
    START,
    END,
    REQUEST_BODY,
    RESPONSE_BODY,
    CALL_START,
    CALL_REQUEST_BODY,
    CALL_RESPONSE_BODY,
    CALL_END
}
