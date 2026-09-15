package com.example.ecommerce.service;

import com.example.ecommerce.dto.AskResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class NlToSqlService {
    private static final String SQL_SYSTEM_PROMPT_TEMPLATE = """
            You are a %s query generator for an e-commerce database.

            SCHEMA:
            customers(id UUID, email, first_name, last_name, phone, created_at, updated_at)
            products(id UUID, name, description, price NUMERIC, stock INT, category, active BOOLEAN, created_at, updated_at)
            orders(id UUID, customer_id UUID -> customers.id, status, total_amount NUMERIC, shipping_address, created_at, updated_at)
            order_items(id UUID, order_id UUID -> orders.id, product_id UUID -> products.id, quantity INT, unit_price NUMERIC, created_at)

            orders.status is one of: PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED.
            products.category examples: Electronics, Books, Clothing. Prices are in EUR.
            Table and column names are lowercase snake_case exactly as listed.

            RULES:
            1. Generate exactly ONE read-only SELECT statement. Never use INSERT, UPDATE, DELETE, DROP, ALTER, CREATE, MERGE or any other statement.
            2. Respond ONLY with JSON in the form {"answer": "<sql>"} and nothing else: no markdown, no code fences, no explanation.
            3. Use explicit JOINs on the foreign keys above; when listing products only include rows where active = TRUE unless the user asks otherwise; add LIMIT 50 unless the question is an aggregate (COUNT, SUM, AVG, MIN, MAX).
            4. Use only %s SQL syntax and functions.
            """;

    private static final String SQL_USER_PROMPT_TEMPLATE = """
            
            %s
            """;

    private static final String SUMMARY_SYSTEM_PROMPT = """
            
            """;

    private static final String SUMMARY_USER_PROMPT_TEMPLATE = """
            
            Question: %s
            Results (%d rows): %s
            """;

    private final JdbcTemplate jdbcTemplate;
    private final ChatClient chatClient;
    private final String sqlSystemPrompt;

    public NlToSqlService(JdbcTemplate jdbcTemplate, ChatClient.Builder builder,
                           @Value("${nlsql.dialect}") String dialect) {
        this.jdbcTemplate = jdbcTemplate;
        this.chatClient = builder.build();
        this.sqlSystemPrompt = SQL_SYSTEM_PROMPT_TEMPLATE.formatted(dialect, dialect);
    }

    public AskResponse ask(String userQuestion) {
        return callLlm(sqlSystemPrompt, userQuestion);
    }

    private AskResponse callLlm(String systemPrompt, String userPrompt) {
        AskResponse response = chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .responseEntity(AskResponse.class)
                .getEntity();

        if (response == null) {
            throw new IllegalStateException("AI model returned an empty response");
        }

        return response;
    }
}
