package org.hawaiiframework.boot.autoconfigure.env;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.boot.SpringApplication.BANNER_LOCATION_PROPERTY;

public class PropertiesDefaultProfileTestBase {
    protected static final Map<String, Object> defaultHawaiiProperties;

    @Autowired
    protected ConfigurableApplicationContext context;

    static {
        defaultHawaiiProperties = new HashMap<>();
        defaultHawaiiProperties.put("logging.file", "log/hawaii.log");
        defaultHawaiiProperties.put("logging.level.org.springframework", "INFO");
        defaultHawaiiProperties.put("logging.level.org.hawaiiframework", "INFO");
        defaultHawaiiProperties.put("spring.jackson.date-format", "com.fasterxml.jackson.databind.util.ISO8601DateFormat");
        defaultHawaiiProperties.put("spring.jackson.property-naming-strategy", "CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES");
        defaultHawaiiProperties.put("spring.jackson.serialization.indent-output", "false");
        defaultHawaiiProperties.put("spring.jackson.serialization.write-dates-as-timestamps", "false");
        defaultHawaiiProperties.put("spring.jackson.serialization.write-date-timestamps-as-nanoseconds", "false");
    }
}
