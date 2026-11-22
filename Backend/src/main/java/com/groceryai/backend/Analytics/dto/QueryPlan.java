
package com.groceryai.backend.Analytics.dto;

import java.util.List;

public record QueryPlan(
        String baseTable,
        List<Selection> selections,
        List<Filter> filters,
        List<FieldRef> groupBy,
        List<Sort> orderBy,
        Integer limit
) {


    public record FieldRef(
            String table,
            String column
    ) {}


    public record Selection(
            FieldRef field,
            Aggregation aggregation,
            String alias
    ) {}


    public record Filter(
            FieldRef field,
            Operator operator,
            Object value
    ) {}


    public record Sort(
            String field,
            Direction direction
    ) {}

    public enum Aggregation {
        COUNT,
        SUM,
        AVG,
        MIN,
        MAX
    }

    public enum Operator {
        EQ,
        NE,
        GT,
        GTE,
        LT,
        LTE,
        LIKE,
        IN,
        IS_NULL,
        IS_NOT_NULL
    }

    public enum Direction {
        ASC,
        DESC
    }
}
