package desphub.pds.backend.integrations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@Component
public class AnthropicAiClient implements AiClient {

    private static final String ANTHROPIC_VERSION = "2023-06-01";
    private static final int MAX_TOKENS = 4096;

    private final RestClient http;
    private final String apiKey;
    private final String model;

    public AnthropicAiClient(
            @Value("${desphub.ai.anthropic.api-key:}") String apiKey,
            @Value("${desphub.ai.anthropic.model:claude-haiku-4-5-20251001}") String model,
            @Value("${desphub.ai.anthropic.base-url:https://api.anthropic.com}") String baseUrl) {
        this.apiKey = apiKey;
        this.model = model;
        this.http = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public String complete(String systemPrompt, String userPrompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(SERVICE_UNAVAILABLE,
                    "Recurso de IA não configurado (defina desphub.ai.anthropic.api-key)");
        }

        Map<String, Object> body = Map.of(
                "model", model,
                "max_tokens", MAX_TOKENS,
                "system", systemPrompt,
                "messages", List.of(Map.of("role", "user", "content", userPrompt)));

        AnthropicResponse response = http.post()
                .uri("/v1/messages")
                .header("x-api-key", apiKey)
                .header("anthropic-version", ANTHROPIC_VERSION)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .exchange((req, res) -> {
                    HttpStatusCode status = res.getStatusCode();
                    if (status.isError()) {
                        String detail;
                        try {
                            detail = new String(res.getBody().readAllBytes());
                        } catch (Exception e) {
                            detail = status.toString();
                        }
                        throw new ResponseStatusException(BAD_GATEWAY, "Falha na chamada à IA: " + detail);
                    }
                    return res.bodyTo(AnthropicResponse.class);
                });

        if (response == null || response.content() == null || response.content().isEmpty()) {
            throw new ResponseStatusException(BAD_GATEWAY, "IA retornou resposta vazia");
        }

        return response.content().stream()
                .filter(c -> "text".equals(c.type()) && c.text() != null)
                .map(ContentBlock::text)
                .reduce((a, b) -> a + b)
                .orElseThrow(() -> new ResponseStatusException(BAD_GATEWAY, "IA não retornou texto"));
    }

    private record AnthropicResponse(List<ContentBlock> content) {
    }

    private record ContentBlock(String type, String text) {
    }
}
