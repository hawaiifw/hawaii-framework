package org.hawaiiframework.logging.web.filter;

import org.hawaiiframework.logging.model.KibanaLogFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.hawaiiframework.logging.model.KibanaLogFieldNames.TX_TYPE;

/**
 * A filter that assigns the class name and method name to the Kibana logger for each request.
 *
 * @author Richard Kohlen
 */
public class ClassMethodNameFilter extends AbstractGenericFilterBean {

    /**
     * The Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassMethodNameFilter.class);

    /**
     * Application context (ac), ac is the context of this Spring Boot Application.
     * Ac is needed to get the appropriate handler for each request.
     */
    private final ApplicationContext applicationContext;

    /**
     * Constructor, the application context should be provided when constructing this class. This class cannot be a bean
     * because it inhered from AbstractGenericFilterBean.
     *
     * @param applicationContext the application context of the Spring Boot Application
     */
    public ClassMethodNameFilter(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {

        HandlerMethod handler = null;

        for (HandlerMapping handlerMapping : applicationContext.getBeansOfType(HandlerMapping.class).values()) {
            HandlerExecutionChain handlerExecutionChain = null;
            // Maybe nicer way of handling this error??
            try {
                handlerExecutionChain = handlerMapping.getHandler(request);
            } catch (Exception e) {
                LOGGER.debug("Exception when fetching the handler");
            }
            if (handlerExecutionChain != null) {
                handler = (HandlerMethod) handlerExecutionChain.getHandler();
                break;
            }
        }

        if (handler == null) {
            LOGGER.debug("HANDLER NOT FOUND");
        } else {

            final var nameMethod = handler.getMethod().getName();
            final var nameController = handler.getBeanType().getSimpleName();
            final var value = nameController + "." + nameMethod;

            KibanaLogFields.set(TX_TYPE, value);
            LOGGER.debug("Set '{}' with value '{};", TX_TYPE.getLogName(), value);
        }
        filterChain.doFilter(request, response);
    }


}
