
package com.groceryai.backend.agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.groceryai.backend.Analytics.dto.QueryPlan;
import com.groceryai.backend.Analytics.engine.QueryEngine;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;


@Service
public class QueryEngineTool {

    private final QueryEngine queryEngine;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final QueryExecutionContext queryExecutionContext;

    public QueryEngineTool(
            QueryEngine queryEngine,
            QueryExecutionContext queryExecutionContext
    ) {
        this.queryEngine = queryEngine;
        this.queryExecutionContext = queryExecutionContext;
    }

    @Tool(description = """
    Execute custom business analytics queries against the grocery database.

    Input must be a valid JSON string matching this EXACT structure:

    {
      "baseTable": "products",
      "selections": [
        {
          "field": {
            "table": "products",
            "column": "price"
          },
          "aggregation": "AVG",
          "alias": "average_price"
        }
      ],
      "filters": [],
      "groupBy": [],
      "orderBy": [
        {
          "field": "average_price",
          "direction": "DESC"
        }
      ],
      "limit": 100
    }

    IMPORTANT RULES:
    - Use only these exact property names.
    - Never use "select", "from", or "joins".
    - baseTable must contain the base table name.
    - selections must contain Selection objects.
    - Every field must be an object with "table" and "column".
    - groupBy must contain FieldRef objects, NOT strings.
    - filters must contain Filter objects.
    - orderBy must contain Sort objects with "field" and "direction".
    - Valid aggregations: COUNT, SUM, AVG, MIN, MAX.
    - Valid directions: ASC, DESC.
    - Do not provide JOIN definitions. The backend automatically determines joins from foreign keys.
    - Never generate SQL.
    
            AGGREGATION RULES:
            
            1. Never apply aggregation to descriptive text fields.
               Examples:
               - categories.category_name
               - products.product_name
               - customers.first_name
               - customers.last_name
            
            2. For descriptive fields, aggregation MUST be null.
            
            3. Use aggregation only when the user requests a calculation:
               - COUNT: count records
               - SUM: calculate total
               - AVG: calculate average
               - MIN: find minimum numeric value
               - MAX: find maximum numeric value
            
            4. When calculating metrics by category:
               - Select categories.category_name WITHOUT aggregation.
               - Select the requested numeric metric with its aggregation.
               - Add categories.category_name to groupBy.
            
            5. Never replace descriptive names with IDs when the user
               asks for names.
            
            6. Prefer joining related tables through foreign keys
               instead of retrieving individual records using CRUD tools.
            
            7. If Query Engine returns a validation error:
               - Read the error carefully.
               - Correct the QueryPlan.
               - Retry at most once.
               - Do not repeatedly call CRUD tools to work around query errors.
            
                    TEMPORAL GROUPING RULES:
            
                    When the user requests analytics grouped by date, day, month, or year,
                    you MUST use the timeGrouping field in the corresponding Selection.
            
                    Available values:
                    - "DAY" for daily grouping
                    - "MONTH" for monthly grouping
                    - "YEAR" for yearly grouping
            
                    Examples:
            
                    Monthly sales:
                    {
                      "field": {
                        "table": "sales",
                        "column": "sales_date"
                      },
                      "aggregation": null,
                      "alias": "month",
                      "timeGrouping": "MONTH"
                    }
            
                    Daily sales:
                    "timeGrouping": "DAY"
            
                    Yearly sales:
                    "timeGrouping": "YEAR"
            
                    IMPORTANT:
                    - The timeGrouping field must be included in the Selection.
                    - The groupBy field must still contain the original date FieldRef.
                    - Do not group by raw timestamps when the user requests monthly or yearly analytics.
                    - Do not omit timeGrouping for temporal grouping requests.
                    - For non-temporal fields, use null.
            
    """)
    public String executeAnalyticsQuery(String queryPlanJson) {
        System.out.println("=== QUERY ENGINE TOOL CALLED ===");
        System.out.println("Received QueryPlan JSON: " + queryPlanJson);
        try {
            QueryPlan plan = objectMapper.readValue(
                    queryPlanJson,
                    QueryPlan.class
            );

            QueryEngine.QueryResult result = queryEngine.execute(plan);
            queryExecutionContext.setLastResult(result);
            System.out.println("=== QUERY EXECUTED SUCCESSFULLY ===");
            System.out.println("QUERY RESULT:");
            System.out.println(objectMapper.writeValueAsString(result));
            return objectMapper.writeValueAsString(result);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Invalid QueryPlan JSON. Provide a valid JSON object.";
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return "Query validation failed: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "Query execution failed. Please generate a corrected QueryPlan.";
        }

    }
}
