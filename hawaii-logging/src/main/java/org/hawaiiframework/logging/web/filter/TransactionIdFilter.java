/*
 * Copyright 2015-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hawaiiframework.logging.web.filter;

import static org.hawaiiframework.logging.model.KibanaLogFieldNames.TX_ID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.hawaiiframework.logging.model.MockMvcFilter;
import org.hawaiiframework.logging.model.TransactionId;
import org.hawaiiframework.logging.util.UuidResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A filter that assigns each request a unique transaction id and output the transaction id to the
 * response header.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class TransactionIdFilter extends AbstractGenericFilterBean implements MockMvcFilter {

  /** The Logger. */
  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionIdFilter.class);

  /** The incoming Hawaii transaction id header name. */
  private final String headerName;

  /** The UUID Resolver. */
  private final UuidResolver uuidResolver = new UuidResolver();

  /**
   * Constructor.
   *
   * @param headerName the headerName to use for the Hawaii transaction id.
   */
  public TransactionIdFilter(String headerName) {
    super();
    this.headerName = headerName;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    UUID uuid = uuidResolver.resolve(request, headerName);

    TransactionId.set(uuid);
    KibanaLogFields.tag(TX_ID, TransactionId.get());

    LOGGER.debug("Set '{}' with value '{};.", TX_ID.getLogName(), uuid);

    if (!response.containsHeader(headerName)) {
      response.addHeader(headerName, TransactionId.get());
    }

    filterChain.doFilter(request, response);
  }
}
