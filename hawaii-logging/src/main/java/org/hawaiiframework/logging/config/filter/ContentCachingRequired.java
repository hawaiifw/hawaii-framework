package org.hawaiiframework.logging.config.filter;

import org.hawaiiframework.logging.web.filter.ContentCachingRequestResponseFilter;
import org.hawaiiframework.logging.web.filter.RequestResponseLogFilter;
import org.hawaiiframework.logging.web.filter.TransactionTypeFilter;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import static org.springframework.context.annotation.ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN;

/**
 * A conditional that checks whether any of the included conditionals resolves to true.
 */
public class ContentCachingRequired extends AnyNestedCondition {

    /**
     * The constructor.
     */
    public ContentCachingRequired() {
        super(REGISTER_BEAN);
    }
    /**
     * Condition for {@link ContentCachingRequestResponseFilter}.
     */
    @ConditionalOnProperty(prefix = ContentCachingRequestResponseFilterConfiguration.CONFIG_PREFIX,
            name = "enabled", havingValue = "true", matchIfMissing = true)
    @SuppressWarnings({"java:S3985", "unused"})
    private static final class ContentCachingRequestResponseFilterConditional {

    }

    /**
     * Condition for {@link RequestResponseLogFilter}.
     */
    @ConditionalOnProperty(prefix = RequestResponseLogFilterConfiguration.CONFIG_PREFIX,
            name = "enabled", havingValue = "true", matchIfMissing = true)
    @SuppressWarnings({"java:S3985", "unused"})
    private static final class RequestResponseLogFilterConditional {

    }

    /**
     * Condition for {@link TransactionTypeFilter}.
     */
    @ConditionalOnProperty(prefix = TransactionTypeFilterConfiguration.CONFIG_PREFIX,
            name = "enabled", havingValue = "true", matchIfMissing = true)
    @SuppressWarnings({"java:S3985", "unused"})
    private static final class TransactionTypeFilterConditional {

    }

}
