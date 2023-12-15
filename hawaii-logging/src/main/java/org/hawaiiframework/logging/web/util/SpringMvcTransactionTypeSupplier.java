package org.hawaiiframework.logging.web.util;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

/**
 * Spring MVC implementation of the {@link TransactionTypeSupplier}.
 *
 * @author Giuseppe Collura
 * @since 6.0.0.m4
 */
@Order(1_000)
public class SpringMvcTransactionTypeSupplier implements TransactionTypeSupplier {

    private static final Logger LOGGER = getLogger(SpringMvcTransactionTypeSupplier.class);

    private final ApplicationContext applicationContext;

    /**
     * The constructor.
     *
     * @param applicationContext The application context.
     */
    public SpringMvcTransactionTypeSupplier(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public String getTransactionType(final ResettableHttpServletRequest request) {
        HandlerMethod handler = null;

        for (HandlerMapping handlerMapping : applicationContext.getBeansOfType(HandlerMapping.class)
            .values()) {
            HandlerExecutionChain handlerExecutionChain = null;
            try {
                handlerExecutionChain = handlerMapping.getHandler(request);
            } catch (Exception e) {
                LOGGER.warn("Exception when fetching the handler");
            }
            if (handlerExecutionChain != null) {
                final var tempHandler = handlerExecutionChain.getHandler();
                handler = tempHandler instanceof HandlerMethod handlerMethod ? handlerMethod : null;
                break;
            }
        }

        if (handler == null) {
            LOGGER.debug("No handler found.");
        } else {

            final var nameMethod = handler.getMethod().getName();
            final var nameController = handler.getBeanType().getSimpleName();
            return nameController + "." + nameMethod;
        }
        return null;
    }
}
