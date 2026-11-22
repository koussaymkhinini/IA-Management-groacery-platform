
package com.groceryai.backend.controller;

import com.groceryai.backend.Analytics.dto.QueryPlan;
import com.groceryai.backend.Analytics.engine.QueryEngine;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsQueryController {

    private final QueryEngine queryEngine;

    public AnalyticsQueryController(QueryEngine queryEngine) {
        this.queryEngine = queryEngine;
    }

    @PostMapping("/query")
    public ResponseEntity<?> executeQuery(
            @RequestBody QueryPlan plan
    ) {

        try {
            QueryEngine.QueryResult result =
                    queryEngine.execute(plan);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }
}
