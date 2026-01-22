package br.com.sistema.nutritional.llm;

public interface LLMProvider {
    
    // ====================================
    // Envia mensagem e retorna resposta
    // ====================================
    String chat(String systemPrompt, String userMessage);
    
    // ====================================
    // Retorna nome do provider
    // ====================================
    String getProviderName();
    
    // ====================================
    // Verifica se provider está disponível
    // ====================================
    boolean isAvailable();
}