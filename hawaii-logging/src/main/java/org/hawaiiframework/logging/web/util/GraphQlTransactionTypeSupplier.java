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
 * @since 6.0.0.m4
 */
@Order(0)
public class GraphQlTransactionTypeSupplier implements TransactionTypeSupplier {

    private static final String GRAPHQL_URL_IDENTIFIER = "graphql";

    private static String getPostBody(final HttpServletRequest servletRequest) throws IOException {
        return IOUtils.toString(servletRequest.getInputStream(),
            servletRequest.getCharacterEncoding());
    }

    @Override
    public String getTransactionType(final ResettableHttpServletRequest request) throws IOException {
        if (!request.getServletPath().contains(GRAPHQL_URL_IDENTIFIER)) {
            return null;
        }
        try {
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
