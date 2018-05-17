package org.hawaiiframework.logging.model.kibana.enums;

import org.hawaiiframework.logging.model.kibana.interfaces.KibanaLogFieldNames;

import java.util.Arrays;

/**
 * This enum represents keys for data that is stored in the logging MDC.
 */
public enum HawaiiKibanaLogFieldNames implements KibanaLogFieldNames {

    SESSION_ID("session.id"),

    TX_ID("tx.id"),
    // Deprecated, left in place for current usage of kibana.
    TX_DURATION("tx.duration"),

    REQUEST_ID("req.id"),
    REQUEST_DURATION("req.duration"),

    CALL_ID("call.id"),
    CALL_TYPE("call.type"),
    CALL_DURATION("call.duration"),
    CALL_RESULT("call.result"),

    URI("uri"),
    METHOD("method"),
    USER("user.name"),

    HTTP_STATUS("tx.status"),

    CLIENT_IP("client.ip"),

    LOG_TYPE("log.type"),

    THREAD("thread"),
    LEVEL("level"),
    TIMESTAMP("timestamp"),
    LOG_LOCATION("log.loc"),

    MESSAGE("message");

    /**
     * The logging key for this MDC entry.
     */
    private final String fieldName;

    /**
     * @param fieldName the kibana log's field name..
     */
    HawaiiKibanaLogFieldNames(final String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @return the kibana log's field name.
     */
    public String getLogName() {
        return fieldName;
    }

    /**
     * Check if a key matches.
     *
     * @param fieldName, the key to match
     * @return true if not null and if the supplied key equals this key.
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public boolean matches(final String fieldName) {
        return fieldName != null && fieldName.equalsIgnoreCase(this.fieldName);
    }

    /**
     * Lookup method that does not throw an exception if the specified
     * key is not found.
     *
     * @param key the key to look for
     * @return the MdcKey with the given name, or null
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public static HawaiiKibanaLogFieldNames fromKey(final String key) {
        HawaiiKibanaLogFieldNames result = null;
        if (key != null) {
            result = Arrays.stream(values()).filter(fieldName -> fieldName.matches(key)).findAny().orElse(null);
        }
        return result;
    }

}
