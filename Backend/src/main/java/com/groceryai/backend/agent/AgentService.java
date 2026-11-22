
package com.groceryai.backend.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.stereotype.Service;

import com.groceryai.backend.entity.MessageRole;
import com.groceryai.backend.service.ConversationService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groceryai.backend.Analytics.engine.QueryEngine;
import com.groceryai.backend.dto.AgentResponse;
import com.groceryai.backend.dto.ChartData;

import java.util.Locale;
import java.util.Set;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AgentService {

    private final ChatClient chatClient;
    private final AgentTools agentTools;
    private final AgentCrudTools agentCrudTools;
    private final ConversationService conversationService;
    private final QueryEngineTool queryEngineTool;
    private final QueryExecutionContext queryExecutionContext;

    private final ObjectMapper objectMapper =
            new ObjectMapper().findAndRegisterModules();

    private static final String SYSTEM_PROMPT = """
        You are Grocery AI, an intelligent assistant for analyzing
        a grocery business database.

        Your responsibilities:
        - Understand questions about grocery sales and business performance.
        - Use the available tools to retrieve actual database information.
        - Select the appropriate tool based on the user's question.
        - Explain the results clearly and accurately.
        - Present rankings and statistics in readable formats.

        Rules:
        1. Never invent statistics, names, quantities, or results.
        2. Use the available tools whenever a question requires database information.
        3. Do not assume information that is not returned by the tools.
        4. If the requested information is unavailable, explain that clearly.
        5. If the user asks an unrelated question, politely explain that you specialize in grocery business data analysis.
        6. When presenting rankings, respect the requested number of results.
        7. Use the actual values returned by the tools.
        8. Never invent database IDs when creating records.
        9. If an ID is required by the creation DTO and the user has not provided it, ask the user for the ID before preparing the operation.
        10. Never claim that a record has been created until confirmation succeeds.

        CRUD TOOL SELECTION:
        1. Use CRUD tools for requests involving individual database records.
        2. Use analytics tools for aggregate statistics, totals, trends, and rankings.
        3. For requests to list or display records, use the corresponding get/list CRUD tool.
        4. Use the appropriate filter tool when the user specifies a category, city, country, customer, employee, or product.
        5. For requests to display individual sales, use getSales or the appropriate sales filter tool, not analytics tools.
        6. Never claim that a CRUD operation succeeded before its confirmation endpoint has executed it.
        7. For create, update, and delete operations, return the exact confirmation token produced by the tool and explain how the user can confirm the operation.
        8. Never invent, modify, or fabricate confirmation tokens.
        9. Do not execute a write operation without explicit user confirmation.
        10. Respect the requested number of records and requested fields in the final answer.
        11. If a requested CRUD operation or filter is not supported by an available tool, explain that clearly instead of inventing results.
        12. Never use an analytics tool as a substitute for individual records when the user requests specific records.

        GLOBAL RESPONSE FIELD SELECTION:
        1. Apply these rules to ALL database queries and tool results, including products, customers, employees, sales, categories, cities, countries, and analytics.
        2. Identify which fields the user explicitly requests.
        3. Include only the requested fields in the final answer.
        4. Do not display unnecessary IDs, names, dates, prices, addresses, or other fields unless they are requested or essential to explain the result.
        5. If the user requests IDs and names, return only IDs and names.
        6. If the user requests all available information, include all relevant fields returned by the tools.
        7. If the user does not specify fields, choose a minimal set of relevant fields that directly answers the question.
        8. Never invent, calculate, or infer missing field values.
        9. Preserve the exact values returned by the tools.
        10. Do not omit important qualifications or limitations when they affect the correctness of the answer.
        11. Apply field selection consistently to tables, lists, summaries, and other response formats.

        IMPORTANT DATA LIMITATION:
        Historical recorded revenue is incomplete in this database.
        Many historical sales have a missing or zero total_price.
        Therefore:
        - Never describe recordedRevenue as the complete actual revenue.
        - Clearly distinguish recorded revenue from total sales and quantity sold.
        - When discussing revenue, mention the number of sales with and without recorded revenue when relevant.
        - Never calculate historical revenue using current product prices.

        RESPONSE STYLE:
        - Answer in the same language as the user's question.
        - Be clear, concise, and professional.
        - Use tables or bullet points when useful.
        - Explain what the statistics mean instead of simply repeating raw data.

            QUERY ENGINE TOOL SELECTION:
            
            1. For ordinary analytics requests, use existing analytics tools
               first when they directly answer the user's question.
            
            2. When the user explicitly requests a chart, graph, pie chart,
               bar chart, line chart or visualization, ALWAYS use
               executeAnalyticsQuery to retrieve the dataset.
            
            3. Never use existing analytics tools as a substitute for
               executeAnalyticsQuery when a chart is explicitly requested.
            
            4. Use executeAnalyticsQuery for custom combinations of tables,
               filters, aggregations, grouping, sorting or comparisons.
            
            5. Never use executeAnalyticsQuery to retrieve individual records.
               Use CRUD tools for individual records.
            
            6. Never generate SQL yourself. Always provide a structured
               QueryPlan to executeAnalyticsQuery.
            
            7. Use only tables, columns and relationships supported
               by the database schema.
            
            8. If a query fails validation, correct the QueryPlan
               using the error message.

        DATABASE SCHEMA RULES FOR QUERY ENGINE:
        1. Use exact database table and column names.
        2. Database identifiers use snake_case.
        3. Never convert column names to camelCase.
        4. Examples:
           - categories.category_name
           - products.product_name
           - products.category_id
           - sales.total_price
           - sales.sales_date
        5. Never invent table names, column names, or relationships.
        6. If a query fails validation, correct the QueryPlan using the error message.
        
            CHART AND VISUALIZATION RULES:
            
            When the user explicitly requests a chart, graph, plot or visualization,
            you MUST use executeAnalyticsQuery to retrieve the actual dataset.
            
            Do not invent, estimate or fabricate chart values.
            
            The chart must be based exclusively on the actual query result returned
            by executeAnalyticsQuery.
            
            The query result must contain appropriate columns for visualization.
            
            VISUALIZATION REQUIREMENTS:
            
                When the user requests a chart, graph, pie chart, bar chart or any
                other visualization:
            
                1. Execute the required analytics query using executeAnalyticsQuery.
                2. Never represent a requested chart using text, ASCII symbols,
                   Unicode squares or Markdown tables as a substitute.
                3. The chart must be returned separately through the chart field
                   of the API response.
                4. The chart configuration must be generated from actual query results.
                5. Continue providing a normal textual explanation in the answer field.
                
            IMPORTANT CHART RESPONSE RULES:
            
                - When the user requests a chart, generate the chart using
                  executeAnalyticsQuery as instructed.
                - Describe the chart and its findings using normal natural language.
                - NEVER include JSON in your final answer.
                - NEVER output chart configuration.
                - NEVER output chart data, labels, values or chartType fields as JSON.
                - The frontend renders the chart separately.
                - Your final answer must contain only a short textual explanation
                  of the displayed chart and its key insights.
        """;

    public AgentService(
            ChatClient.Builder chatClientBuilder,
            AgentTools agentTools,
            AgentCrudTools agentCrudTools,
            ConversationService conversationService,
            QueryEngineTool queryEngineTool,
            QueryExecutionContext queryExecutionContext) {

        this.chatClient = chatClientBuilder.build();
        this.agentTools = agentTools;
        this.agentCrudTools = agentCrudTools;
        this.conversationService = conversationService;
        this.queryEngineTool = queryEngineTool;
        this.queryExecutionContext = queryExecutionContext;
    }

    public AgentResponse ask(String message, UUID conversationId) {
        queryExecutionContext.clear();
        // Load previous conversation messages.
        List<com.groceryai.backend.entity.Message> previousMessages =
                conversationService.getMessages(conversationId);

        // Convert database messages into Spring AI messages.
        List<Message> chatHistory = new ArrayList<>();

        for (com.groceryai.backend.entity.Message previous : previousMessages) {

            if (previous.getRole() == MessageRole.USER) {
                chatHistory.add(new UserMessage(previous.getContent()));
            } else {
                chatHistory.add(new AssistantMessage(previous.getContent()));
            }
        }

        // Save the new user message.
        conversationService.saveMessage(
                conversationId,
                MessageRole.USER,
                message
        );

        // Add the current question to the conversation history.
        chatHistory.add(new UserMessage(message));

        String answer = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .messages(chatHistory.toArray(new Message[0]))
                .tools(agentTools, agentCrudTools, queryEngineTool)
                .call()
                .content();

        if (answer == null || answer.isBlank()) {
            answer = "I couldn't generate an answer. Please try again.";
        }

        ChartData chart = null;

        if (requestsChart(message)) {

            System.out.println("=== CHART REQUEST DETECTED ===");

            QueryEngine.QueryResult queryResult =
                    queryExecutionContext.getLastResult();

            if (queryResult == null) {

                System.out.println("=== NO QUERY ENGINE RESULT FOUND ===");

            } else {

                System.out.println("=== QUERY RESULT AVAILABLE ===");
                System.out.println("Rows: " + queryResult.rowCount());

                if (!queryResult.rows().isEmpty()) {
                    System.out.println(
                            "Columns: " + queryResult.rows().get(0).keySet()
                    );

                    chart = generateChart(message, queryResult);
                }
            }

            System.out.println(
                    "=== CHART GENERATED: " + (chart != null) + " ==="
            );
        }

        // Save the assistant response.
        conversationService.saveMessage(
                conversationId,
                MessageRole.ASSISTANT,
                answer
        );

        queryExecutionContext.clear();

        return new AgentResponse(
                conversationId,
                answer,
                chart
        );
    }
    private boolean requestsChart(String message) {

        String text = message.toLowerCase(Locale.ROOT);

        return text.contains("chart")
                || text.contains("graph")
                || text.contains("plot")
                || text.contains("pie")
                || text.contains("bar chart")
                || text.contains("line chart")
                || text.contains("visualization")
                || text.contains("visualisation")
                || text.contains("граф")
                || text.contains("диаграм")
                || text.contains("courbe")
                || text.contains("histogramme");
    }


    private ChartData generateChart(
            String userMessage,
            QueryEngine.QueryResult result
    ) {
        try {
            if (result.rows() == null || result.rows().isEmpty()) {
                return null;
            }

            Set<String> columns = result.rows().get(0).keySet();

            String xKey = null;
            String yKey = null;

            String dateKeywords = "date month year time";

            // First, look for a date or time column.
            for (String column : columns) {
                if (dateKeywords.contains(column.toLowerCase(Locale.ROOT))
                        || column.toLowerCase(Locale.ROOT).contains("date")
                        || column.toLowerCase(Locale.ROOT).contains("month")) {
                    xKey = column;
                    break;
                }
            }

            // If no date column exists, use a non-numeric column.
            if (xKey == null) {
                for (String column : columns) {
                    boolean numeric = result.rows().stream()
                            .anyMatch(row -> row.get(column) instanceof Number);

                    if (!numeric) {
                        xKey = column;
                        break;
                    }
                }
            }

            // Find a numeric column for the Y axis.
            for (String column : columns) {
                if (column.equals(xKey)) {
                    continue;
                }

                boolean numeric = result.rows().stream()
                        .anyMatch(row -> row.get(column) instanceof Number);

                if (numeric) {
                    yKey = column;
                    break;
                }
            }

            if (xKey == null || yKey == null) {
                System.out.println("No suitable chart columns found.");
                return null;
            }

            String request = userMessage.toLowerCase(Locale.ROOT);

            String type;

            if (request.contains("pie")
                    || request.contains("distribution")
                    || request.contains("proportion")
                    || request.contains("дол")
                    || request.contains("распредел")) {
                type = "pie";
            } else if (request.contains("line")
                    || request.contains("trend")
                    || request.contains("over time")
                    || request.contains("monthly")
                    || request.contains("daily")
                    || request.contains("weekly")
                    || request.contains("yearly")) {
                type = "line";
            } else {
                type = "bar";
            }

            String title = "Analytics Chart";

            return new ChartData(
                    type,
                    title,
                    xKey,
                    yKey,
                    result.rows()
            );

        } catch (Exception e) {
            System.err.println(
                    "Chart generation failed: " + e.getMessage()
            );
            return null;
        }
    }


    private String extractJsonObject(String response) {

        if (response == null || response.isBlank()) {
            throw new IllegalArgumentException(
                    "Empty chart configuration response"
            );
        }

        int start = response.indexOf('{');
        int end = response.lastIndexOf('}');

        if (start == -1 || end == -1 || end < start) {
            throw new IllegalArgumentException(
                    "Invalid JSON response from Gemini"
            );
        }

        return response.substring(start, end + 1);
    }
}
