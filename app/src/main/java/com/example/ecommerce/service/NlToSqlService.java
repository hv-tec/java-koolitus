package com.example.ecommerce.service;

import com.example.ecommerce.dto.AskResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NlToSqlService {
    private static final String SQL_SYSTEM_PROMPT_TEMPLATE = """
            You are a %s query generator for an e-commerce database.

            SCHEMA:
            customers(id UUID PK, email TEXT, first_name TEXT, last_name TEXT, phone TEXT)
            products(id UUID PK, name TEXT, price NUMERIC, stock INTEGER, category TEXT)
            orders(id UUID PK, customer_id UUID, status TEXT, total_amount NUMERIC)
            order_items(id UUID PK, order_id UUID, product_id UUID, quantity INTEGER, unit_price NUMERIC)

            RULES:
            1. Return ONLY a single raw SQL SELECT statement - no markdown or explanation.
            2. If you cannot answer from this schema, return: CANNOT_ANSWER
            3. Never generate INSERT, UPDATE, DELETE, DROP or any non-SELECT statement.
            4. Use only %s SQL syntax and functions.
            """;

    private static final String SQL_USER_PROMPT_TEMPLATE = """
            
            %s
            """;

    private static final String SUMMARY_SYSTEM_PROMPT = """
            You are a helpful assistant for an e-commerce team.
            Answer the user's question in Estonian in one or two short sentences,
            using ONLY the query results given. Do not mention SQL or table names.
            If the results are empty, say that nothing matching was found.
            Respond as JSON: {"answer": "<text>"}
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
        validateInput(userQuestion);

        // 1-2: kysimus -> SQL
        String sql = callLlm(sqlSystemPrompt, userQuestion).answer().strip();
        if (sql.equalsIgnoreCase("CANNOT_ANSWER")) {
            return new AskResponse("Sellele küsimusele ma andmebaasist vastata ei oska.");
        }

        // 3: valideeri - LLM-i valjund on sisend, mida kontrollitakse enne kaivitamist
        validateSql(sql);

        // 3: kaivita
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        // 4: tulemus -> loomulik keel
        String summaryPrompt = SUMMARY_USER_PROMPT_TEMPLATE.formatted(userQuestion, rows.size(), rows.toString());
        return callLlm(SUMMARY_SYSTEM_PROMPT, summaryPrompt);
    }

    private void validateInput(String userQuestion) {
        if (userQuestion == null || userQuestion.isBlank()) {
            throw new IllegalArgumentException("Input text missing");
        }
    }

    private void validateSql(String sql) {
        String check = sql.replaceAll("(?s)/\\*.*?\\*/", "").replaceAll("--.*", "").strip().toUpperCase();
        if (!check.startsWith("SELECT")) {
            throw new IllegalArgumentException("Only SELECT queries are allowed. Rejected: " + sql);
        }
        if (check.contains(";")) {
            throw new IllegalArgumentException("Only a single statement is allowed. Rejected: " + sql);
        }
        if (check.matches("(?s).*\\b(INSERT|UPDATE|DELETE|DROP|ALTER|CREATE|MERGE|TRUNCATE|GRANT|EXEC|CALL)\\b.*")) {
            throw new IllegalArgumentException("Query contains a forbidden SQL keyword. Rejected: " + sql);
        }
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
