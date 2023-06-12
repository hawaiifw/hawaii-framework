package org.hawaiiframework.repository;

import org.hawaiiframework.sql.SqlQueryResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.sql.DataSource;
import java.util.List;

/**
 * Base class for JDBC repositories.
 * <p>
 * Contains the SQL query resolver and methods to obtain Spring JDBC templates.
 */
public class BaseJdbcRepository {

    /**
     * The String "Could not find a result for '{}'." appears 3 times in the file. [MultipleStringLiterals].
     */
    private static final String COULD_NOT_FIND_A_RESULT_FOR = "Could not find a result for '{}'.";

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseJdbcRepository.class);

    /**
     * The SQL query resolver.
     */
    private final SqlQueryResolver queryResolver;

    /**
     * The JDBC Template that sub-classes can use.
     */
    private final JdbcTemplate aJdbcTemplate;

    /**
     * The JDBC Template with named query parameters that sub-classes can use.
     */
    private final NamedParameterJdbcTemplate aNamedParameterJdbcTemplate;

    /**
     * Constructor with a query resolver and a data source.
     *
     * @param queryResolver The SQL query resolver.
     * @param dataSource    The datasource to use.
     */
    public BaseJdbcRepository(final SqlQueryResolver queryResolver, final DataSource dataSource) {
        this.queryResolver = queryResolver;
        this.aJdbcTemplate = new JdbcTemplate(dataSource);
        this.aNamedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * Use the {@link SqlQueryResolver} to resolve the given query.
     *
     * @param sqlQueryName the name of the query to resolve from the path.
     * @return the query string.
     */
    protected String resolveQuery(final String sqlQueryName) {
        LOGGER.debug("Resolving query '{}'.", sqlQueryName);
        return queryResolver.resolveSqlQuery(sqlQueryName);
    }

    /**
     * @return the JDBC template.
     */
    protected JdbcTemplate jdbcTemplate() {
        return aJdbcTemplate;
    }

    /**
     * @return the named parameter template.
     */
    protected NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        return aNamedParameterJdbcTemplate;
    }

    /**
     * Method to handle an EmptyResultDataAccessException using a row mapper.
     *
     * @param query      query to use.
     * @param parameters parameters to add to the query.
     * @param rowMapper  the row mapper to use.
     * @param <T>        the type to return.
     * @return the requested type or null in case of an EmptyResultDataAccessException.
     */
    protected <T> T getOpt(final String query, final SqlParameterSource parameters, final RowMapper<T> rowMapper) {
        try {
            return aNamedParameterJdbcTemplate.queryForObject(resolveQuery(query), parameters, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            LOGGER.debug(COULD_NOT_FIND_A_RESULT_FOR, query);
        }
        return null;
    }

    /**
     * Method to handle an EmptyResultDataAccessException using a type.
     *
     * @param query        query to use.
     * @param parameters   parameters to add to the query.
     * @param requiredType the required type for the query.
     * @param <T>          the type to return.
     * @return the requested type or null in case of an EmptyResultDataAccessException.
     */
    protected <T> T getOpt(final String query, final SqlParameterSource parameters, final Class<T> requiredType) {
        try {
            return aNamedParameterJdbcTemplate.queryForObject(resolveQuery(query), parameters, requiredType);
        } catch (EmptyResultDataAccessException e) {
            LOGGER.debug(COULD_NOT_FIND_A_RESULT_FOR, query);
        }
        return null;
    }

    /**
     * Method to handle an EmptyResultDataAccessException using a row mapper.
     *
     * @param query      query to use.
     * @param parameters parameters to add to the query.
     * @param extractor  result set extractor to use.
     * @param <T>        the type to return.
     * @return the requested type or null in case of an EmptyResultDataAccessException.
     */
    protected <T> T getOpt(final String query, final SqlParameterSource parameters, final ResultSetExtractor<T> extractor) {
        try {
            return aNamedParameterJdbcTemplate.query(resolveQuery(query), parameters, extractor);
        } catch (EmptyResultDataAccessException e) {
            LOGGER.debug(COULD_NOT_FIND_A_RESULT_FOR, query);
        }
        return null;
    }

    /**
     * Throws an EmptyResultDataAccessException in case the result set is empty.
     *
     * @param query      query to use.
     * @param rowMapper  the row mapper to use.
     * @param <T>        the type to return.
     * @return the requested type or an EmptyResultDataAccessException.
     */
    protected <T> T get(final String query, final RowMapper<T> rowMapper) {
        return get(query, EmptySqlParameterSource.INSTANCE, rowMapper);
    }

    /**
     * Throws an EmptyResultDataAccessException in case the result set is empty.
     *
     * @param query      query to use.
     * @param parameters parameters to add to the query.
     * @param rowMapper  the row mapper to use.
     * @param <T>        the type to return.
     * @return the requested type or an EmptyResultDataAccessException.
     */
    protected <T> T get(final String query, final SqlParameterSource parameters, final RowMapper<T> rowMapper) {
        try {
            return aNamedParameterJdbcTemplate.queryForObject(resolveQuery(query), parameters, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            LOGGER.debug(COULD_NOT_FIND_A_RESULT_FOR, query);
            throw e;
        }
    }

    /**
     * Perform the 'update' query (insert, update, delete).
     *
     * @param query      query to use.
     * @param parameters parameters to add to the query.
     * @return the number of rows affected
     * @throws DataAccessException if there is any problem issuing the update
     */
    protected int update(final String query, final SqlParameterSource parameters) {
        try {
            return aNamedParameterJdbcTemplate.update(resolveQuery(query), parameters);
        } catch (DataAccessException e) {
            LOGGER.debug("Error performing '{}'.", query);
            throw e;
        }
    }

    /**
     * Throws an EmptyResultDataAccessException in case the result set is empty.
     *
     * @param query      query to use.
     * @param parameters parameters to add to the query.
     * @param rowMapper  the row mapper to use.
     * @param <T>        the type to return.
     * @return the requested type or an EmptyResultDataAccessException.
     */
    protected <T> List<T> getList(final String query, final SqlParameterSource parameters, final RowMapper<T> rowMapper) {
        try {
            return aNamedParameterJdbcTemplate.query(resolveQuery(query), parameters, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            LOGGER.debug(COULD_NOT_FIND_A_RESULT_FOR, query);
            throw e;
        }
    }

}
