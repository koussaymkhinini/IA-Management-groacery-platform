package com.groceryai.backend.agent;

import com.groceryai.backend.Analytics.engine.QueryEngine;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class QueryExecutionContext {

    private QueryEngine.QueryResult lastResult;

    public void setLastResult(QueryEngine.QueryResult result) {
        this.lastResult = result;
    }

    public QueryEngine.QueryResult getLastResult() {
        return lastResult;
    }

    public void clear() {
        lastResult = null;
    }
}