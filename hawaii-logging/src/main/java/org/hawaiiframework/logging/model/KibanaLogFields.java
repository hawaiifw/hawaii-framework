package org.hawaiiframework.logging.model;

import org.apache.commons.lang3.StringUtils;
import org.hawaiiframework.logging.model.kibana.enums.HawaiiKibanaLogCallResultTypes;
import org.hawaiiframework.logging.model.kibana.enums.HawaiiKibanaLogFieldNames;
import org.hawaiiframework.logging.model.kibana.interfaces.KibanaLogFieldNames;
import org.hawaiiframework.logging.model.kibana.interfaces.KibanaLogTypeNames;
import org.slf4j.MDC;

import static org.hawaiiframework.logging.model.kibana.enums.HawaiiKibanaLogFieldNames.CALL_RESULT;
import static org.hawaiiframework.logging.model.kibana.enums.HawaiiKibanaLogFieldNames.LOG_TYPE;

/**
 * Class that holds the extra fields used for Kibana logging.
 * <p>
 * Log lines for Kibana will contain all fields set, until the log fields are cleared by invoking {@link KibanaLogFields#clear()}.
 */
public final class KibanaLogFields {

    private KibanaLogFields() {
        // Empty utility class constructor.
    }

    /**
     * Set the field HawaiiKibanaLogTypeNames.LOG_TYPE to the given {@code value}.
     */
    public static void setLogType(final KibanaLogTypeNames value) {
        set(LOG_TYPE, value.toString());
    }

    /**
     * Set the field HawaiiKibanaLogTypeNames.LOG_TYPE to the given {@code value}.
     */
    public static void setCallResult(final HawaiiKibanaLogCallResultTypes value) {
        set(CALL_RESULT, value.toString());
    }
    /**
     * Removes the value for the field HawaiiKibanaLogTypeNames.LOG_TYPE.
     */
    public static void unsetLogType() {
        unset(LOG_TYPE);
    }

    /**
     * Sets the Kibana log field {@code field} to the {@code value}.
     */
    public static void set(final KibanaLogFieldNames field, final String value) {
        MDC.put(field.getLogName(), value);
    }

    /**
     * Retrieves the value for the {@code field}. It will return {@code null} if no value is set.
     */
    public static String get(final KibanaLogFieldNames field) {
        return getOrDefault(field, null);
    }

    /**
     * Retrieves the value for the {@code field}. It will return {@code defaultValue} if no value is set.
     */
    public static String getOrDefault(final KibanaLogFieldNames field, final String defaultValue) {
        return StringUtils.defaultIfEmpty(MDC.get(field.getLogName()), defaultValue);
    }

    /**
     * Removes the value for the field {@code field}.
     */
    public static void unset(final KibanaLogFieldNames field) {
        MDC.remove(field.getLogName());
    }

    /**
     * Removes all values set for all fields.
     */
    public static void clear() {
        MDC.clear();
    }


    /**
     * Getter for the log string.
     *
     * @return the log string
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public static String getValuesAsLogString() {
        final StringBuilder result = new StringBuilder();
        MDC.getCopyOfContextMap().forEach((key, value) -> {
            if (!key.equals(HawaiiKibanaLogFieldNames.LOG_TYPE.getLogName())) {
                result.append(' ');
                result.append(key);
                result.append("=\"");
                result.append(value);
                result.append('"');
            }
        });

        return result.toString();
    }

    /**
     * Update log fields based on the {@code contextMap}.
     */
    public static void setContextMap(final KibanaLogContextMap contextMap) {
        if (contextMap != null) {
            MDC.setContextMap(contextMap.getContextMap());
        }
    }
}
