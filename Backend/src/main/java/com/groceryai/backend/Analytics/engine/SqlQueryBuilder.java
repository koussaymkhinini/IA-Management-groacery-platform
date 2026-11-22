
package com.groceryai.backend.Analytics.engine;

import com.groceryai.backend.Analytics.dto.QueryPlan;
import com.groceryai.backend.Analytics.metadata.DatabaseSchemaService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SqlQueryBuilder {

    private final DatabaseSchemaService schemaService;

    public SqlQueryBuilder(DatabaseSchemaService schemaService) {
        this.schemaService = schemaService;
    }

    public BuiltQuery build(QueryPlan plan) {

        Map<String, DatabaseSchemaService.TableSchema> schema =
                new HashMap<>();

        for (var table : schemaService.getSchema()) {
            schema.put(table.tableName(), table);
        }

        Set<String> requiredTables = new LinkedHashSet<>();
        requiredTables.add(plan.baseTable());

        for (var selection : plan.selections()) {
            requiredTables.add(selection.field().table());
        }

        if (plan.filters() != null) {
            for (var filter : plan.filters()) {
                requiredTables.add(filter.field().table());
            }
        }

        if (plan.groupBy() != null) {
            for (var field : plan.groupBy()) {
                requiredTables.add(field.table());
            }
        }

        List<JoinEdge> joins = new ArrayList<>();

        for (String table : requiredTables) {
            if (table.equals(plan.baseTable())) {
                continue;
            }

            List<JoinEdge> path = findJoinPath(
                    plan.baseTable(),
                    table,
                    schema
            );

            for (JoinEdge edge : path) {
                if (!joins.contains(edge)) {
                    joins.add(edge);
                }
            }
        }

        Map<String, String> aliases = new LinkedHashMap<>();
        aliases.put(plan.baseTable(), "t0");

        int aliasCounter = 1;

        for (JoinEdge edge : joins) {
            if (!aliases.containsKey(edge.fromTable())) {
                aliases.put(edge.fromTable(), "t" + aliasCounter++);
            }

            if (!aliases.containsKey(edge.toTable())) {
                aliases.put(edge.toTable(), "t" + aliasCounter++);
            }
        }

        // Build SELECT clause.
        StringBuilder sql = new StringBuilder("SELECT ");

        List<String> selections = new ArrayList<>();

        for (QueryPlan.Selection selection : plan.selections()) {

            String expression = buildSelectionExpression(
                    selection,
                    aliases
            );

            if (selection.alias() != null) {
                expression += " AS " + quote(selection.alias());
            }

            selections.add(expression);
        }

        sql.append(String.join(", ", selections));

        // Build FROM clause.
        sql.append(" FROM ")
                .append(quote(plan.baseTable()))
                .append(" ")
                .append(aliases.get(plan.baseTable()));

        // Build JOIN clauses.
        Set<String> joinedTables = new HashSet<>();
        joinedTables.add(plan.baseTable());

        List<JoinEdge> pendingJoins = new ArrayList<>(joins);

        while (!pendingJoins.isEmpty()) {

            boolean progress = false;

            Iterator<JoinEdge> iterator = pendingJoins.iterator();

            while (iterator.hasNext()) {
                JoinEdge edge = iterator.next();

                boolean fromJoined =
                        joinedTables.contains(edge.fromTable());

                boolean toJoined =
                        joinedTables.contains(edge.toTable());

                if (fromJoined && !toJoined) {

                    appendJoin(sql, edge, aliases);
                    joinedTables.add(edge.toTable());

                    iterator.remove();
                    progress = true;

                } else if (toJoined && !fromJoined) {

                    appendJoin(sql, edge, aliases);
                    joinedTables.add(edge.fromTable());

                    iterator.remove();
                    progress = true;

                } else if (fromJoined && toJoined) {

                    iterator.remove();
                    progress = true;
                }
            }

            if (!progress) {
                throw new IllegalArgumentException(
                        "Could not build a connected JOIN path"
                );
            }
        }

        // Build WHERE clause.
        List<Object> parameters = new ArrayList<>();

        if (plan.filters() != null && !plan.filters().isEmpty()) {

            List<String> conditions = new ArrayList<>();

            for (QueryPlan.Filter filter : plan.filters()) {
                conditions.add(
                        buildCondition(filter, aliases, parameters)
                );
            }

            sql.append(" WHERE ")
                    .append(String.join(" AND ", conditions));
        }

        // Build GROUP BY clause.
        if (plan.groupBy() != null && !plan.groupBy().isEmpty()) {

            List<String> groupFields = new ArrayList<>();

            for (QueryPlan.FieldRef field : plan.groupBy()) {

                QueryPlan.Selection temporalSelection = plan.selections()
                        .stream()
                        .filter(selection ->
                                selection.field().equals(field)
                                        && selection.timeGrouping() != null)
                        .findFirst()
                        .orElse(null);

                if (temporalSelection != null) {

                    groupFields.add(
                            buildTimeExpression(
                                    buildFieldExpression(field, aliases),
                                    temporalSelection.timeGrouping()
                            )
                    );

                } else {

                    groupFields.add(
                            buildFieldExpression(field, aliases)
                    );
                }
            }

            sql.append(" GROUP BY ")
                    .append(String.join(", ", groupFields));
        }

        // Build ORDER BY clause.
        if (plan.orderBy() != null && !plan.orderBy().isEmpty()) {

            Set<String> availableAliases = new HashSet<>();

            for (QueryPlan.Selection selection : plan.selections()) {
                if (selection.alias() != null) {
                    availableAliases.add(selection.alias());
                }
            }

            List<String> orderFields = new ArrayList<>();

            for (QueryPlan.Sort sort : plan.orderBy()) {

                if (!availableAliases.contains(sort.field())) {
                    throw new IllegalArgumentException(
                            "ORDER BY must reference a selected alias: "
                                    + sort.field()
                    );
                }

                orderFields.add(
                        quote(sort.field()) + " " + sort.direction().name()
                );
            }

            sql.append(" ORDER BY ")
                    .append(String.join(", ", orderFields));
        }

        // Build LIMIT clause.
        if (plan.limit() != null) {
            sql.append(" LIMIT ?");
            parameters.add(plan.limit());
        }

        return new BuiltQuery(sql.toString(), parameters);
    }

    private String buildSelectionExpression(
            QueryPlan.Selection selection,
            Map<String, String> aliases
    ) {

        String expression = buildFieldExpression(
                selection.field(),
                aliases
        );

        if (selection.timeGrouping() != null) {
            expression = buildTimeExpression(
                    expression,
                    selection.timeGrouping()
            );
        }

        if (selection.aggregation() != null) {
            expression = switch (selection.aggregation()) {
                case COUNT -> "COUNT(" + expression + ")";
                case SUM -> "SUM(" + expression + ")";
                case AVG -> "AVG(" + expression + ")";
                case MIN -> "MIN(" + expression + ")";
                case MAX -> "MAX(" + expression + ")";
            };
        }

        return expression;
    }

    private String buildTimeExpression(
            String field,
            QueryPlan.TimeGrouping grouping
    ) {

        return switch (grouping) {
            case DAY ->
                    "TO_CHAR(DATE_TRUNC('day', " + field + "), 'YYYY-MM-DD')";

            case MONTH ->
                    "TO_CHAR(DATE_TRUNC('month', " + field + "), 'YYYY-MM')";

            case YEAR ->
                    "TO_CHAR(DATE_TRUNC('year', " + field + "), 'YYYY')";
        };
    }

    private void appendJoin(
            StringBuilder sql,
            JoinEdge edge,
            Map<String, String> aliases
    ) {

        sql.append(" JOIN ")
                .append(quote(edge.toTable()))
                .append(" ")
                .append(aliases.get(edge.toTable()))
                .append(" ON ")
                .append(aliases.get(edge.fromTable()))
                .append(".")
                .append(quote(edge.fromColumn()))
                .append(" = ")
                .append(aliases.get(edge.toTable()))
                .append(".")
                .append(quote(edge.toColumn()));
    }

    private String buildFieldExpression(
            QueryPlan.FieldRef field,
            Map<String, String> aliases
    ) {

        String alias = aliases.get(field.table());

        if (alias == null) {
            throw new IllegalArgumentException(
                    "Missing table alias: " + field.table()
            );
        }

        return alias + "." + quote(field.column());
    }

    private String buildCondition(
            QueryPlan.Filter filter,
            Map<String, String> aliases,
            List<Object> parameters
    ) {

        String field = buildFieldExpression(
                filter.field(),
                aliases
        );

        return switch (filter.operator()) {

            case EQ -> {
                parameters.add(filter.value());
                yield field + " = ?";
            }

            case NE -> {
                parameters.add(filter.value());
                yield field + " <> ?";
            }

            case GT -> {
                parameters.add(filter.value());
                yield field + " > ?";
            }

            case GTE -> {
                parameters.add(filter.value());
                yield field + " >= ?";
            }

            case LT -> {
                parameters.add(filter.value());
                yield field + " < ?";
            }

            case LTE -> {
                parameters.add(filter.value());
                yield field + " <= ?";
            }

            case LIKE -> {
                parameters.add(filter.value());
                yield field + " LIKE ?";
            }

            case IS_NULL -> field + " IS NULL";

            case IS_NOT_NULL -> field + " IS NOT NULL";

            case IN -> {
                List<?> values = (List<?>) filter.value();

                if (values.isEmpty()) {
                    yield "1 = 0";
                }

                parameters.addAll(values);

                String placeholders = String.join(
                        ", ",
                        Collections.nCopies(values.size(), "?")
                );

                yield field + " IN (" + placeholders + ")";
            }
        };
    }

    private List<JoinEdge> findJoinPath(
            String start,
            String target,
            Map<String, DatabaseSchemaService.TableSchema> schema
    ) {

        Queue<String> queue = new LinkedList<>();
        Map<String, JoinEdge> previousEdge = new HashMap<>();
        Set<String> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {

            String current = queue.poll();

            if (current.equals(target)) {
                break;
            }

            for (JoinEdge edge : getNeighbors(current, schema)) {

                String next = edge.toTable();

                if (visited.add(next)) {
                    previousEdge.put(next, edge);
                    queue.add(next);
                }
            }
        }

        if (!visited.contains(target)) {
            throw new IllegalArgumentException(
                    "No foreign key path between " + start + " and " + target
            );
        }

        List<JoinEdge> path = new ArrayList<>();
        String current = target;

        while (!current.equals(start)) {

            JoinEdge edge = previousEdge.get(current);

            if (edge == null) {
                throw new IllegalStateException(
                        "Failed to reconstruct JOIN path"
                );
            }

            path.add(edge);
            current = edge.fromTable();
        }

        Collections.reverse(path);

        return path;
    }

    private List<JoinEdge> getNeighbors(
            String tableName,
            Map<String, DatabaseSchemaService.TableSchema> schema
    ) {

        List<JoinEdge> neighbors = new ArrayList<>();

        for (var table : schema.values()) {

            for (var fk : table.foreignKeys()) {

                if (table.tableName().equals(tableName)) {
                    neighbors.add(new JoinEdge(
                            table.tableName(),
                            fk.columnName(),
                            fk.referencedTable(),
                            fk.referencedColumn()
                    ));
                }

                if (fk.referencedTable().equals(tableName)) {
                    neighbors.add(new JoinEdge(
                            tableName,
                            fk.referencedColumn(),
                            table.tableName(),
                            fk.columnName()
                    ));
                }
            }
        }

        return neighbors;
    }

    private String quote(String identifier) {

        if (identifier == null ||
                !identifier.matches("[a-zA-Z][a-zA-Z0-9_]*")) {
            throw new IllegalArgumentException("Invalid SQL identifier");
        }

        return "\"" + identifier + "\"";
    }

    private record JoinEdge(
            String fromTable,
            String fromColumn,
            String toTable,
            String toColumn
    ) {}

    public record BuiltQuery(
            String sql,
            List<Object> parameters
    ) {}
}
