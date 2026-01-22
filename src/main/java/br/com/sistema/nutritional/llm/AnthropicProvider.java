package br.com.sistema.nutritional.llm;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AnthropicProvider implements LLMProvider {
    
    @Value("${llm.anthropic.api-key:}")
    private String apiKey;
    
    @Value("${llm.anthropic.model-name:claude-3-5-sonnet-20241022}")
    private String modelName;
    
    private AnthropicChatModel model;
    
    private AnthropicChatModel getModel() {
        if (model == null) {
            log.info("🟣 Inicializando Anthropic: {}", modelName);
            model = AnthropicChatModel.builder()
                    .apiKey(apiKey)
                    .modelName(modelName)
                    .temperature(0.7)
                    .build();
        }
        return model;
    }
    
    @Override
    public String chat(String systemPrompt, String userMessage) {
        try {
            log.info("💬 Anthropic processando mensagem");
            
            String fullPrompt = systemPrompt + "\n\nUsuário: " + userMessage;
            
            ChatRequest request = ChatRequest.builder()
                    .messages(UserMessage.from(fullPrompt))
                    .build();
            
            ChatResponse response = getModel().chat(request);
            String result = response.aiMessage().text();
            
            log.info("✅ Resposta gerada: {} caracteres", result.length());
            return result;
            
        } catch (Exception e) {
            log.error("❌ Erro no Anthropic", e);
            throw new RuntimeException("Erro ao processar com Anthropic: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getProviderName() {
        return "Anthropic (" + modelName + ")";
    }
    
    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isEmpty();
    }
    
    @Override
    public ChatModel getChatModel() {
        return getModel();
    }
}