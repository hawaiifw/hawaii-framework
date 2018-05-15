package org.hawaiiframework.security.sso.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.hawaiiframework.exception.HawaiiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;

/**
 * Base parser for the response in case a call to the SSO server returns a HTTP 401 Unauthorized response.
 *
 * The SSO server returns an unauthorized response in various circumstances, and doesn't always return the same error information. This
 * base class serves as a common base to implement parsers from.
 *
 * @param <T> the type of object this parser returns
 */
public class BaseUnauthorizedResponseParser<T> {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseUnauthorizedResponseParser.class);

    /**
     * The object mapper to use for parsing.
     */
    protected final ObjectMapper objectMapper;

    /**
     * The type of object this parser creates.
     */
    private final Class<T> clazz;

    /**
     * Constructor to initialize the object mapper.
     *
     * @param clazz the parsed type
     */
    protected BaseUnauthorizedResponseParser(final Class<T> clazz) {
        this.clazz = clazz;
        objectMapper = new Jackson2ObjectMapperBuilder()
                .propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                .createXmlMapper(false)
                .build();
    }

    /**
     * Parses the body to the defined type, delegating to {@link #doParse(String)}.
     *
     * An {@link InvalidFormatException} is only logged, since this is an expected result as we don't know beforehand which
     * type of object is returned. The assumption is that another parser will take care of it.
     *
     * @param body the response body
     * @return the body parsed to an object, or null if the json format doesn't match
     * @throws HawaiiException in case of any other errors
     */
    public T parse(final String body) {
        try {
            return doParse(body);
        } catch (InvalidFormatException e) {
            LOGGER.debug("Unable to parse body '{}' to a '{}'", body, clazz.getName());
            return null;
        } catch (IOException e) {
            throw new HawaiiException("error parsing response", e);
        }
    }

    /**
     * Default implementation that uses the object mapper to parse to the declared type.
     *
     * @param body the response body
     * @return the body parsed to an object
     */
    protected T doParse(final String body) throws IOException {
        return objectMapper.readValue(body, clazz);
    }

}
