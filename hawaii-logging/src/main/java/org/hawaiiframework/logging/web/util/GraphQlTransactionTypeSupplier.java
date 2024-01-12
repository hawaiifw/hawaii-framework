/*
 * Copyright 2015-2023 the original author or authors.
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
package org.hawaiiframework.logging.web.util;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.http.MediaType.parseMediaType;

import graphql.language.Document;
import graphql.language.Field;
import graphql.language.OperationDefinition;
import graphql.parser.Parser;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Locale;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.core.annotation.Order;

/**
 * GraphQl implementation for the {@link TransactionTypeSupplier}.
 *
 * @author Giuseppe Collura
 * @since 6.0.0
 */
@Order(0)
public class GraphQlTransactionTypeSupplier implements TransactionTypeSupplier {

  private static final String GRAPHQL_URL_IDENTIFIER = "graphql";

  private static String getPostBody(HttpServletRequest servletRequest) throws IOException {
    return IOUtils.toString(servletRequest.getInputStream(), servletRequest.getCharacterEncoding());
  }

  @Override
  @SuppressWarnings("checkstyle:ReturnCount")
  public String getTransactionType(ResettableHttpServletRequest request) throws IOException {
    String contentType = request.getContentType();
    if (contentType == null || !request.getServletPath().contains(GRAPHQL_URL_IDENTIFIER)) {
      return null;
    }
    try {
      // Collect the graphQl multiPart request with.
      // request.getParts().stream().findFirst().get().getContentType()
      if (MULTIPART_FORM_DATA.includes(parseMediaType(contentType))) {
        return "Graphql.multiPart";
      }

      String body = getPostBody(request);

      JSONObject jsonObject = new JSONObject(body);
      String query = jsonObject.getString("query");
      Document document = Parser.parse(query);

      OperationDefinition operationDefinition =
          document.getDefinitionsOfType(OperationDefinition.class).get(0);

      String operation = operationDefinition.getOperation().name().toLowerCase(Locale.getDefault());
      String action =
          operationDefinition.getSelectionSet().getSelectionsOfType(Field.class).get(0).getName();

      return "Graphql." + operation + "." + action;
    } finally {
      request.reset();
    }
  }
}
