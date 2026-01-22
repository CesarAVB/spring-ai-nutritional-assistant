package br.com.sistema.nutritional.llm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OpenRouterProvider implements LLMProvider {
    
    @Value("${llm.openrouter.api-key:}")
    private String apiKey;
    
    @Value("${llm.openrouter.model-name:anthropic/claude-3.5-sonnet}")
    private String modelName;
    
    @Value("${llm.openrouter.base-url:https://openrouter.ai/api/v1}")
    private String baseUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    // ====================================
    // Envia mensagem para OpenRouter
    // ====================================
    @Override
    public String chat(String systemPrompt, String userMessage) {
        try {
            log.info("💬 OpenRouter processando: {}", modelName);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("HTTP-Referer", "http://localhost:8081");
            headers.set("X-Title", "GitHub Assistant");
            
            Map<String, Object> body = new HashMap<>();
            body.put("model", modelName);
            body.put("messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userMessage)
            ));
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    baseUrl + "/chat/completions",
                    request,
                    Map.class
            );
            
            if (response.getBody() == null) {
                throw new RuntimeException("Resposta vazia do OpenRouter");
            }
            
            List<Map<String, Object>> choices = 
                    (List<Map<String, Object>>) response.getBody().get("choices");
            
            if (choices == null || choices.isEmpty()) {
                throw new RuntimeException("Nenhuma resposta retornada");
            }
            
            Map<String, Object> message = 
                    (Map<String, Object>) choices.get(0).get("message");
            String content = (String) message.get("content");
            
            log.info("✅ Resposta gerada");
            return content;
            
        } catch (Exception e) {
            log.error("❌ Erro no OpenRouter", e);
            throw new RuntimeException("Erro ao processar com OpenRouter: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getProviderName() {
        return "OpenRouter (" + modelName + ")";
    }
    
    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isEmpty();
    }
}