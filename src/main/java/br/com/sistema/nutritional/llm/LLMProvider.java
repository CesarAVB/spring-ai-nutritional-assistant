package br.com.sistema.nutritional.llm;

import dev.langchain4j.model.chat.ChatModel;

public interface LLMProvider {
    
    String chat(String systemPrompt, String userMessage);
    
    String getProviderName();
    
    boolean isAvailable();
    
    ChatModel getChatModel();
}