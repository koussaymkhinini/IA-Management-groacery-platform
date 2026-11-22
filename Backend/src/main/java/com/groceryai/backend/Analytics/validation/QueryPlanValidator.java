
package com.groceryai.backend.Analytics.validation;

import com.groceryai.backend.Analytics.dto.QueryPlan;
import com.groceryai.backend.Analytics.metadata.DatabaseSchemaService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import java.util.ArrayDeque;
import java.util.Deque;

@Service
public class QueryPlanValidator {

    private static final int MAX_TABLES = 5;
    private static final int MAX_LIMIT = 1000;

    private static final Set<String> ALLOWED_TABLES = Set.of(
            "categories",
            "cities",
            "countries",
            "customers",
            "employees",
            "products",
            "sales"
    );

    private final DatabaseSchemaService schemaService;

    public QueryPlanValidator(DatabaseSchemaService schemaService) {
        this.schemaService = schemaService;
    }

    public void validate(QueryPlan plan) {

        if (plan == null) {
            throw new IllegalArgumentException("Query plan cannot be null");
        }

        if (plan.baseTable() == null ||
                !ALLOWED_TABLES.contains(plan.baseTable())) {
            throw new IllegalArgumentException("Invalid base table");
        }

        if (plan.selections() == null || plan.selections().isEmpty()) {
            throw new IllegalArgumentException("At least one selection is required");
        }

        if (plan.selections().size() > 20) {
            throw new IllegalArgumentException("Too many selected fields");
        }

        if (plan.limit() != null &&
                (plan.limit() < 1 || plan.limit() > MAX_LIMIT)) {
            throw new IllegalArgumentException("Invalid query limit");
        }

        Map<String, DatabaseSchemaService.TableSchema> schema =
                schemaService.getSchema().stream()
                        .collect(Collectors.toMap(
                                DatabaseSchemaService.TableSchema::tableName,
                                table -> table
                        ));

        Set<String> usedTables = new HashSet<>();
        usedTables.add(plan.baseTable());

        for (QueryPlan.Selection selection : plan.selections()) {
            if (selection == null || selection.field() == null) {
                throw new IllegalArgumentException("Invalid selection");
            }

            validateField(selection.field(), schema);
            usedTables.add(selection.field().table());

            if (selection.alias() != null &&
                    !selection.alias().matches("[a-zA-Z][a-zA-Z0-9_]*")) {
                throw new IllegalArgumentException("Invalid selection alias");
            }

            if (selection.aggregation() != null) {
                validateAggregation(selection, schema);
            }
        }

        if (plan.filters() != null) {
            for (QueryPlan.Filter filter : plan.filters()) {
                if (filter == null || filter.field() == null ||
                        filter.operator() == null) {
                    throw new IllegalArgumentException("Invalid filter");
                }

                validateField(filter.field(), schema);
                validateFilterValue(filter);
                usedTables.add(filter.field().table());
            }
        }

        if (plan.groupBy() != null) {
            for (QueryPlan.FieldRef field : plan.groupBy()) {
                validateField(field, schema);
                usedTables.add(field.table());
            }
        }

        if (plan.orderBy() != null) {
            for (QueryPlan.Sort sort : plan.orderBy()) {
                if (sort == null || sort.field() == null ||
                        !sort.field().matches("[a-zA-Z][a-zA-Z0-9_]*") ||
                        sort.direction() == null) {
                    throw new IllegalArgumentException("Invalid sort");
                }
            }
        }

        if (usedTables.size() > MAX_TABLES) {
            throw new IllegalArgumentException("Too many tables in query");
        }

        validateGroupBy(plan);
        validateTableConnectivity(
                plan.baseTable(),
                usedTables,
                schema
        );
    }

    private void validateField(
            QueryPlan.FieldRef field,
            Map<String, DatabaseSchemaService.TableSchema> schema
    ) {
        if (field == null ||
                field.table() == null ||
                field.column() == null) {
            throw new IllegalArgumentException("Invalid field reference");
        }

        if (!ALLOWED_TABLES.contains(field.table())) {
            throw new IllegalArgumentException("Table is not allowed");
        }

        DatabaseSchemaService.TableSchema table =
                schema.get(field.table());

        if (table == null) {
            throw new IllegalArgumentException("Table does not exist");
        }

        boolean columnExists = table.columns().stream()
                .anyMatch(column ->
                        column.columnName().equals(field.column())
                );

        if (!columnExists) {
            throw new IllegalArgumentException(
                    "Column does not exist: " +
                            field.table() + "." + field.column()
            );
        }
    }


    private void validateAggregation(
            QueryPlan.Selection selection,
            Map<String, DatabaseSchemaService.TableSchema> schema
    ) {
        DatabaseSchemaService.TableSchema table =
                schema.get(selection.field().table());

        DatabaseSchemaService.ColumnSchema column =
                table.columns().stream()
                        .filter(c -> c.columnName().equals(
                                selection.field().column()
                        ))
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Aggregation column does not exist"
                                )
                        );

        if (selection.aggregation() == QueryPlan.Aggregation.COUNT) {
            return;
        }

        int type = column.dataType();

        boolean numeric = switch (type) {
            case java.sql.Types.INTEGER,
                 java.sql.Types.BIGINT,
                 java.sql.Types.SMALLINT,
                 java.sql.Types.NUMERIC,
                 java.sql.Types.DECIMAL,
                 java.sql.Types.FLOAT,
                 java.sql.Types.REAL,
                 java.sql.Types.DOUBLE -> true;
            default -> false;
        };

        if (!numeric) {
            throw new IllegalArgumentException(
                    selection.aggregation() +
                            " requires a numeric column: " +
                            selection.field().table() + "." +
                            selection.field().column()
            );
        }
    }


    private void validateFilterValue(QueryPlan.Filter filter) {
        switch (filter.operator()) {
            case IS_NULL, IS_NOT_NULL -> {
                if (filter.value() != null) {
                    throw new IllegalArgumentException(
                            "NULL operators must not have a value"
                    );
                }
            }
            case IN -> {
                if (!(filter.value() instanceof List<?>)) {
                    throw new IllegalArgumentException(
                            "IN operator requires a list"
                    );
                }
            }
            default -> {
                if (filter.value() == null) {
                    throw new IllegalArgumentException(
                            "Filter value cannot be null"
                    );
                }
            }
        }
    }


    private void validateGroupBy(QueryPlan plan) {
        boolean hasAggregation = plan.selections().stream()
                .anyMatch(selection -> selection.aggregation() != null);

        Set<QueryPlan.FieldRef> groupByFields =
                new HashSet<>(plan.groupBy() == null
                        ? List.of()
                        : plan.groupBy());

        if (plan.groupBy() != null &&
                groupByFields.size() != plan.groupBy().size()) {
            throw new IllegalArgumentException(
                    "Duplicate GROUP BY fields are not allowed"
            );
        }

        for (QueryPlan.Selection selection : plan.selections()) {
            if (selection.aggregation() == null) {
                QueryPlan.FieldRef field = selection.field();

                if (hasAggregation && !groupByFields.contains(field)) {
                    throw new IllegalArgumentException(
                            "Non-aggregated field must be included in GROUP BY: " +
                                    field.table() + "." + field.column()
                    );
                }

                if (!groupByFields.isEmpty() &&
                        !groupByFields.contains(field)) {
                    throw new IllegalArgumentException(
                            "Selected field must be included in GROUP BY"
                    );
                }
            }
        }
    }


    private void validateTableConnectivity(
            String baseTable,
            Set<String> usedTables,
            Map<String, DatabaseSchemaService.TableSchema> schema
    ) {
        Set<String> visited = new HashSet<>();
        Deque<String> queue = new ArrayDeque<>();

        queue.add(baseTable);
        visited.add(baseTable);

        while (!queue.isEmpty()) {
            String currentTable = queue.poll();

            for (DatabaseSchemaService.TableSchema table : schema.values()) {
                for (DatabaseSchemaService.ForeignKeySchema fk : table.foreignKeys()) {

                    String neighbor = null;

                    if (table.tableName().equals(currentTable)) {
                        neighbor = fk.referencedTable();
                    } else if (fk.referencedTable().equals(currentTable)) {
                        neighbor = table.tableName();
                    }

                    if (neighbor != null && visited.add(neighbor)) {
                        queue.add(neighbor);
                    }
                }
            }
        }

        Set<String> disconnectedTables = new HashSet<>(usedTables);
        disconnectedTables.removeAll(visited);

        if (!disconnectedTables.isEmpty()) {
            throw new IllegalArgumentException(
                    "Tables are not connected through foreign keys: " +
                            disconnectedTables
            );
        }
    }


}
