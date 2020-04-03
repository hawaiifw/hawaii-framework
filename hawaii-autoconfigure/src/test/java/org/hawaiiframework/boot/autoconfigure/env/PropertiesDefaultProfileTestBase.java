package org.hawaiiframework.boot.autoconfigure.env;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.HashMap;
import java.util.Map;

public class PropertiesDefaultProfileTestBase {

    protected static final Map<String, Object> DEFAULT_HAWAII_PROPERTIES;

    @Autowired
    protected ConfigurableApplicationContext context;

    static {
        DEFAULT_HAWAII_PROPERTIES = new HashMap<>();
        DEFAULT_HAWAII_PROPERTIES.put("logging.level.org.springframework", "INFO");
        DEFAULT_HAWAII_PROPERTIES.put("logging.level.org.hawaiiframework", "INFO");
        DEFAULT_HAWAII_PROPERTIES.put("spring.jackson.date-format", "com.fasterxml.jackson.databind.util.ISO8601DateFormat");
        DEFAULT_HAWAII_PROPERTIES.put("spring.jackson.property-naming-strategy", "CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES");
        DEFAULT_HAWAII_PROPERTIES.put("spring.jackson.serialization.indent-output", "false");
        DEFAULT_HAWAII_PROPERTIES.put("spring.jackson.serialization.write-dates-as-timestamps", "false");
        DEFAULT_HAWAII_PROPERTIES.put("spring.jackson.serialization.write-date-timestamps-as-nanoseconds", "false");
    }
}
