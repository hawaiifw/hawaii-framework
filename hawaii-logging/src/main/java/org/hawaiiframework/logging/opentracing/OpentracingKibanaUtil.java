package org.hawaiiframework.logging.opentracing;

import org.hawaiiframework.logging.model.KibanaLogFields;

import java.util.Map;

/**
 * Utility to add opentracing data to the kibana log fields.
 */
public final class OpentracingKibanaUtil {

    /**
     * Utility constructor.
     */
    private OpentracingKibanaUtil() {
        // Do nothing.
    }

    /**
     * Add the tag, if it exists in the {@link OpentracingKibanaLogField}, to the {@link KibanaLogFields}.
     *
     * @param key   The tag's name.
     * @param value The tag's value.
     */
    public static void addTagToKibanaFields(final String key, final Object value) {
        final OpentracingKibanaLogField field = OpentracingKibanaLogField.fromKey(key);
        if (field != null) {
            KibanaLogFields.tag(field, tagValueToString(value));
        }
    }

    /**
     * Add the tags, if they exist in the {@link OpentracingKibanaLogField}, to the {@link KibanaLogFields}.
     *
     * @param tags The tags.
     */
    public static void addTagsToKibanaFields(final Map<String, Object> tags) {
        tags.forEach((key, value) -> addTagToKibanaFields(key, tagValueToString(value)));
    }

    private static String tagValueToString(final Object value) {
        if (value == null) {
            return "-";
        }
        return value.toString();
    }

}
