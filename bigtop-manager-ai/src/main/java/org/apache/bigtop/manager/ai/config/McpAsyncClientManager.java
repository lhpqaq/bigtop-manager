package org.apache.bigtop.manager.ai.config;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.WebFluxSseClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class McpAsyncClientManager {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${spring.ai.mcp.client.enabled:false}")
    private boolean enabled;

    @Value("${spring.ai.mcp.client.base-url:http://localhost:${server.port:8080}}")
    private String clientBaseUrl;

    @Value("${spring.ai.mcp.client.sse-endpoint:/mcp/sse}")
    private String sseEndpoint;

    private McpAsyncClient mcpAsyncClient;
    private boolean initialized = false;
    private boolean disabledLogged = false;

    public synchronized McpAsyncClient getClient() {
        if (!enabled) {
            if (!disabledLogged) {
                log.info("MCP Async Client is disabled by config (spring.ai.mcp.client.enabled=false)");
                disabledLogged = true;
            }
            return null;
        }

        if (!initialized) {
            try {
                String baseUrl = resolveBaseUrl();
                log.info("Initializing MCP Async Client with baseUrl={}, sseEndpoint={}", baseUrl, sseEndpoint);
                WebClient.Builder webClientBuilder = WebClient.builder().baseUrl(baseUrl);
                WebFluxSseClientTransport transport =
                        WebFluxSseClientTransport.builder(webClientBuilder).sseEndpoint(sseEndpoint).build();

                McpAsyncClient client = McpClient.async(transport)
                        .requestTimeout(Duration.ofSeconds(30))
                        .build();
                
                client.initialize().block(Duration.ofSeconds(10));
                client.ping().block(Duration.ofSeconds(10));

                mcpAsyncClient = client;
                initialized = true;
                log.info("MCP Async Client successfully initialized to {}{}", baseUrl, sseEndpoint);
                logRegisteredTools(client);
            } catch (Exception e) {
                log.warn("Failed to initialize MCP Async Client: {}", e.getMessage());
                // Leave initialized = false to retry on next getClient() call
            }
        }
        return mcpAsyncClient;
    }

    private void logRegisteredTools(McpAsyncClient client) {
        try {
            List<String> toolNames = new ArrayList<>();
            String cursor = null;
            while (true) {
                McpSchema.ListToolsResult listToolsResult = cursor == null || cursor.isBlank()
                        ? client.listTools().block(Duration.ofSeconds(10))
                        : client.listTools(cursor).block(Duration.ofSeconds(10));
                if (listToolsResult == null || listToolsResult.tools() == null || listToolsResult.tools().isEmpty()) {
                    break;
                }

                for (McpSchema.Tool tool : listToolsResult.tools()) {
                    if (tool != null && tool.name() != null) {
                        toolNames.add(tool.name());
                    }
                }

                String nextCursor = listToolsResult.nextCursor();
                if (nextCursor == null || nextCursor.isBlank() || nextCursor.equals(cursor)) {
                    break;
                }
                cursor = nextCursor;
            }

            log.info("MCP tools discovered: count={}, names={}", toolNames.size(), toolNames);
        } catch (Exception e) {
            log.warn("Failed to list MCP tools after initialization: {}", e.getMessage());
        }
    }

    private String resolveBaseUrl() {
        if (clientBaseUrl != null && !clientBaseUrl.isBlank()) {
            return clientBaseUrl;
        }
        return "http://localhost:" + serverPort;
    }
}
