package org.hawaiiframework.logging.config;

import org.hawaiiframework.logging.model.PathDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;

import java.util.List;

import static org.hawaiiframework.logging.config.HawaiiLoggingConfigurationProperties.CONFIG_PREFIX;
import static org.springframework.http.MediaType.parseMediaType;

/**
 * Properties used by {@link HawaiiLoggingConfiguration}.
 */
@ConfigurationProperties(prefix = CONFIG_PREFIX)
public class HawaiiLoggingConfigurationProperties {

    /**
     * Configuration properties prefix.
     */
    public static final String CONFIG_PREFIX = "hawaii.logging";

    //    @Value("${" + CONFIG_PREFIX + ".allowed-content-types}")
    private List<MediaType> allowedContentTypes = List.of(parseMediaType("application/json"),
            parseMediaType("application/graphql+json"),
            parseMediaType("application/hal+json"),
            parseMediaType("application/problem+json"),
            parseMediaType("application/vnd.spring-boot.actuator.v1+json"),
            parseMediaType("application/vnd.spring-boot.actuator.v3+json"),
            parseMediaType("application/vnd.spring-cloud.config-server.v2+json"),
            parseMediaType("application/x-www-form-urlencoded"),
            parseMediaType("multipart/form-data"),
            parseMediaType("text/plain"),
            parseMediaType("text/xml"));

    private List<MediaType> suppressedContentTypes = List.of(MediaType.MULTIPART_FORM_DATA);

    private List<PathDefinition> excludePaths = List.of(new PathDefinition("/actuator/*"));

    private List<String> fieldsToMask = List.of("password", "keyPassphrase", "client_secret", "secret");

    /**
     * Get the allowed content-types.
     *
     * @return the allowed content-types.
     */
    public List<MediaType> getAllowedContentTypes() {
        return allowedContentTypes;
    }

    /**
     * Set the allowed content-types.
     *
     * @param allowedContentTypes the allowed content-types.
     */
    public void setAllowedContentTypes(final List<MediaType> allowedContentTypes) {
        this.allowedContentTypes = allowedContentTypes;
    }

    /**
     * Get the fields to mask.
     *
     * @return the fields to mask.
     */
    public List<String> getFieldsToMask() {
        return fieldsToMask;
    }

    /**
     * Set the fields to mask.
     *
     * @param fieldsToMask the fields to mask.
     */
    public void setFieldsToMask(final List<String> fieldsToMask) {
        this.fieldsToMask = fieldsToMask;
    }

    /**
     * Get the paths to exclude.
     *
     * @return the paths to exclude.
     */
    public List<PathDefinition> getExcludePaths() {
        return excludePaths;
    }

    /**
     * Set the paths to exclude.
     *
     * @param excludePaths the paths to exclude.
     */
    public void setExcludePaths(final List<PathDefinition> excludePaths) {
        this.excludePaths = excludePaths;
    }

    public List<MediaType> getSuppressedContentTypes() {
        return suppressedContentTypes;
    }

    public void setSuppressedContentTypes(final List<MediaType> suppressedContentTypes) {
        this.suppressedContentTypes = suppressedContentTypes;
    }
}
