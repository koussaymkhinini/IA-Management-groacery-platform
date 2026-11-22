
package com.groceryai.backend.Analytics.engine;

import com.groceryai.backend.Analytics.dto.QueryPlan;
import com.groceryai.backend.Analytics.validation.QueryPlanValidator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class QueryEngine {

    private static final int QUERY_TIMEOUT_SECONDS = 30;

    private final QueryPlanValidator validator;
    private final SqlQueryBuilder sqlQueryBuilder;
    private final JdbcTemplate jdbcTemplate;

    public QueryEngine(
            QueryPlanValidator validator,
            SqlQueryBuilder sqlQueryBuilder,
            JdbcTemplate jdbcTemplate
    ) {
        this.validator = validator;
        this.sqlQueryBuilder = sqlQueryBuilder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public QueryResult execute(QueryPlan plan) {

        // Validate the query plan before generating SQL
        validator.validate(plan);

        // Require a result limit to prevent unbounded queries
        if (plan.limit() == null) {
            throw new IllegalArgumentException(
                    "A query limit is required"
            );
        }

        // Generate SQL and collect parameter values
        SqlQueryBuilder.BuiltQuery builtQuery =
                sqlQueryBuilder.build(plan);

        // Execute the generated query using prepared parameters
        List<Map<String, Object>> rows = jdbcTemplate.query(
                connection -> {

                    PreparedStatement statement =
                            connection.prepareStatement(builtQuery.sql());

                    statement.setQueryTimeout(QUERY_TIMEOUT_SECONDS);

                    List<Object> parameters = builtQuery.parameters();

                    for (int i = 0; i < parameters.size(); i++) {
                        statement.setObject(i + 1, parameters.get(i));
                    }

                    return statement;
                },
                (resultSet, rowNumber) -> mapRow(resultSet)
        );

        return new QueryResult(rows, rows.size());
    }

    // Convert the current result row into a key-value map
    private Map<String, Object> mapRow(
            java.sql.ResultSet resultSet
    ) throws java.sql.SQLException {

        ResultSetMetaData metadata = resultSet.getMetaData();

        Map<String, Object> row = new LinkedHashMap<>();

        for (int i = 1; i <= metadata.getColumnCount(); i++) {

            String columnName = metadata.getColumnLabel(i);
            Object value = resultSet.getObject(i);

            row.put(columnName, value);
        }

        return row;
    }

    public record QueryResult(
            List<Map<String, Object>> rows,
            int rowCount
    ) {}
}
