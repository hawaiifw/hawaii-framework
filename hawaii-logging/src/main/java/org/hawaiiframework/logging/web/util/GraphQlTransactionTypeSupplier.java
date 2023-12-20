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
    private static final String MULTI_PART_MIME_TYPE = "multipart/form-data";

    private static String getPostBody(final HttpServletRequest servletRequest) throws IOException {
        return IOUtils.toString(servletRequest.getInputStream(),
            servletRequest.getCharacterEncoding());
    }

    @SuppressWarnings("checkstyle:ReturnCount")
    @Override
    public String getTransactionType(final ResettableHttpServletRequest request) throws IOException {
        if (request.getServletPath().contains(GRAPHQL_URL_IDENTIFIER)) {
            return null;
        }
        try {
            // Collect the graphQl multiPart request with.
            // request.getParts().stream().findFirst().get().getContentType()
            String contentType = request.getContentType();
            if (contentType != null && contentType.contains(MULTI_PART_MIME_TYPE)) {
                return "Graphql.multiPart";
            }

            final String body = getPostBody(request);

            final JSONObject jsonObject = new JSONObject(body);
            final String query = jsonObject.getString("query");
            final Document document = Parser.parse(query);

            final OperationDefinition operationDefinition = document.getDefinitionsOfType(
                OperationDefinition.class).get(0);

            final String operation = operationDefinition.getOperation().name().toLowerCase(Locale.getDefault());
            final String action = operationDefinition.getSelectionSet().getSelectionsOfType(Field.class).get(0)
                .getName();

            return "Graphql." + operation + "." + action;
        } finally {
            request.reset();
        }
    }
}
