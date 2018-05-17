package org.hawaiiframework.boot.autoconfigure.logging;

import org.hawaiiframework.logging.web.filter.RequestResponseLogFilterConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration task for Hawaii Logging.
 */
@Configuration
@ConditionalOnProperty(name = "hawaii.logging.enabled")
@ConditionalOnClass(RequestResponseLogFilterConfiguration.class)
@EnableConfigurationProperties(HawaiiLoggingProperties.class)
public class HawaiiLoggingAutoConfiguration {

}
