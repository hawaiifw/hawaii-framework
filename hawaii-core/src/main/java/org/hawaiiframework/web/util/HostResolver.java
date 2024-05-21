package org.hawaiiframework.web.util;

import static org.springframework.http.HttpHeaders.HOST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Utility class to get host related information.
 *
 * @since 3.0.0.M6
 */
public class HostResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(HostResolver.class);

  /**
   * Returns the host's base url consisting of the scheme and host.
   *
   * @return the base url
   */
  @SuppressWarnings({"PMD.LawOfDemeter", "PMD.UseExplicitTypes"})
  public String getBaseUrl() {
    var requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    var request = requestAttributes.getRequest();
    var scheme = request.getScheme();
    var host = request.getHeader(HOST);
    LOGGER.debug("Resolved scheme '{}' and host '{}'", scheme, host);
    return request.getScheme() + "://" + host;
  }
}
