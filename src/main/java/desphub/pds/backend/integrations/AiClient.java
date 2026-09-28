package desphub.pds.backend.integrations;

public interface AiClient {
    String complete(String systemPrompt, String userPrompt);
}
