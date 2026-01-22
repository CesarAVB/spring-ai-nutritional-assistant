package br.com.sistema.nutritional.llm;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GeminiProvider implements LLMProvider {
    
    @Value("${llm.gemini.api-key:}")
    private String apiKey;
    
    @Value("${llm.gemini.model-name:gemini-1.5-flash}")
    private String modelName;
    
    private GoogleAiGeminiChatModel model;
    
    // ====================================
    // Inicializa modelo Gemini (lazy)
    // ====================================
    private GoogleAiGeminiChatModel getModel() {
        if (model == null) {
            log.info("🔷 Inicializando Gemini: {}", modelName);
            model = GoogleAiGeminiChatModel.builder()
                    .apiKey(apiKey)
                    .modelName(modelName)
                    .temperature(0.7)
                    .build();
        }
        return model;
    }
    
    // ====================================
    // Envia mensagem para Gemini
    // ====================================
    @Override
    public String chat(String systemPrompt, String userMessage) {
        try {
            log.info("💬 Gemini processando mensagem");
            
            String fullPrompt = systemPrompt + "\n\nUsuário: " + userMessage;
            
            ChatRequest request = ChatRequest.builder()
                    .messages(UserMessage.from(fullPrompt))
                    .build();
            
            ChatResponse response = getModel().chat(request);
            String result = response.aiMessage().text();
            
            log.info("✅ Resposta gerada: {} caracteres", result.length());
            return result;
            
        } catch (Exception e) {
            log.error("❌ Erro no Gemini", e);
            throw new RuntimeException("Erro ao processar com Gemini: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getProviderName() {
        return "Gemini (" + modelName + ")";
    }
    
    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isEmpty();
    }
}