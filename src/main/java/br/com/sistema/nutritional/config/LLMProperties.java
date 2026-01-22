package br.com.sistema.nutritional.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "llm")
@Data
public class LLMProperties {
    
    private String provider = "gemini";
    private boolean enableFallback = false;
    
    private GeminiConfig gemini = new GeminiConfig();
    private OpenAIConfig openai = new OpenAIConfig();
    private AnthropicConfig anthropic = new AnthropicConfig();
    private OpenRouterConfig openrouter = new OpenRouterConfig();
    
    @Data
    public static class GeminiConfig {
        private String apiKey;
        private String modelName = "gemini-1.5-flash";
    }
    
    @Data
    public static class OpenAIConfig {
        private String apiKey;
        private String modelName = "gpt-4o-mini";
    }
    
    @Data
    public static class AnthropicConfig {
        private String apiKey;
        private String modelName = "claude-3-5-sonnet-20241022";
    }
    
    @Data
    public static class OpenRouterConfig {
        private String apiKey;
        private String modelName = "anthropic/claude-3.5-sonnet";
        private String baseUrl = "https://openrouter.ai/api/v1";
    }
}