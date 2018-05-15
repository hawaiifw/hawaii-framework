package org.hawaiiframework.boot.autoconfigure.logging;

import org.hawaiiframework.logging.web.filter.RequestResponseLogFilterConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The HawaiiLoggingProperties.
 */
@ConfigurationProperties(prefix = "hawaii.logging")
public class HawaiiLoggingProperties {

    /**
     * The RequestResponseLogFilterConfiguration.
     */
    private RequestResponseLogFilterConfiguration configuration;

    /**
     * Get the configuration.
     *
     * @return The configuration.
     **/
    public RequestResponseLogFilterConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Set the configuration property.
     *
     * @param configuration The configuration property.
     **/
    public void setConfiguration(final RequestResponseLogFilterConfiguration configuration) {
        this.configuration = configuration;
    }
}
