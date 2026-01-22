package br.com.sistema.nutritional.llm;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OpenAIProvider implements LLMProvider {
    
    @Value("${llm.openai.api-key:}")
    private String apiKey;
    
    @Value("${llm.openai.model-name:gpt-4o-mini}")
    private String modelName;
    
    private OpenAiChatModel model;
    
    private OpenAiChatModel getModel() {
        if (model == null) {
            log.info("🟢 Inicializando OpenAI: {}", modelName);
            model = OpenAiChatModel.builder()
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
            log.info("💬 OpenAI processando mensagem");
            
            String fullPrompt = systemPrompt + "\n\nUsuário: " + userMessage;
            
            ChatRequest request = ChatRequest.builder()
                    .messages(UserMessage.from(fullPrompt))
                    .build();
            
            ChatResponse response = getModel().chat(request);
            String result = response.aiMessage().text();
            
            log.info("✅ Resposta gerada: {} caracteres", result.length());
            return result;
            
        } catch (Exception e) {
            log.error("❌ Erro no OpenAI", e);
            throw new RuntimeException("Erro ao processar com OpenAI: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getProviderName() {
        return "OpenAI (" + modelName + ")";
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