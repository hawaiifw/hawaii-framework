package org.hawaiiframework.logging.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Properties used by {@HawaiiLoggingConfiguration}.
 */
@Component
@ConfigurationProperties(prefix = "hawaii.logging")
public class HawaiiLoggingConfigurationProperties {

    private List<String> allowedContentTypes;

    private List<String> fieldsToMask;

    public List<String> getAllowedContentTypes() {
        return allowedContentTypes;
    }

    public void setAllowedContentTypes(final List<String> allowedContentTypes) {
        this.allowedContentTypes = allowedContentTypes;
    }

    public List<String> getFieldsToMask() {
        return fieldsToMask;
    }

    public void setFieldsToMask(final List<String> fieldsToMask) {
        this.fieldsToMask = fieldsToMask;
    }
}
