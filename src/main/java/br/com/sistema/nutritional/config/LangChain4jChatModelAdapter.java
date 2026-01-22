package br.com.sistema.nutritional.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import br.com.sistema.nutritional.llm.LLMProvider;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class LangChain4jChatModelAdapter {
    
    private final LLMProvider llmProvider;
    
    @Bean
    @Primary
    public ChatModel chatModel() {
        log.info("🔧 Criando ChatModel adaptado de: {}", llmProvider.getProviderName());
        
        return new ChatModel() {
            @Override
            public ChatResponse chat(ChatRequest request) {
                try {
                    String userMessage = extrairMensagem(request);
                    
                    log.debug("💬 Processando com {}: {}", 
                        llmProvider.getProviderName(), 
                        userMessage.substring(0, Math.min(50, userMessage.length())));
                    
                    String response = llmProvider.chat("", userMessage);
                    
                    return ChatResponse.builder()
                            .aiMessage(new AiMessage(response))
                            .build();
                            
                } catch (Exception e) {
                    log.error("❌ Erro ao processar chat", e);
                    throw new RuntimeException("Erro ao processar mensagem: " + e.getMessage(), e);
                }
            }
        };
    }
    
    private String extrairMensagem(ChatRequest request) {
        if (request == null) {
            return "";
        }
        
        String requestStr = request.toString();
        
        if (requestStr.contains("text=")) {
            int inicio = requestStr.indexOf("text=") + 5;
            int fim = requestStr.indexOf(",", inicio);
            if (fim == -1) {
                fim = requestStr.indexOf("}", inicio);
            }
            if (fim > inicio) {
                return requestStr.substring(inicio, fim).replaceAll("['\"]", "").trim();
            }
        }
        
        return requestStr;
    }
}