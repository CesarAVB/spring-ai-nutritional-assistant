  package br.com.sistema.nutritional.config;

  
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.sistema.nutritional.llm.AnthropicProvider;
import br.com.sistema.nutritional.llm.GeminiProvider;
import br.com.sistema.nutritional.llm.LLMProvider;
import br.com.sistema.nutritional.llm.OpenAIProvider;
import br.com.sistema.nutritional.llm.OpenRouterProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class LLMConfig {
    
    private final LLMProperties properties;
    private final GeminiProvider geminiProvider;
    private final OpenAIProvider openAIProvider;
    private final AnthropicProvider anthropicProvider;
    private final OpenRouterProvider openRouterProvider;
    
    // ====================================
    // Cria provider baseado na configuração
    // ====================================
    @Bean
    public LLMProvider llmProvider() {
        String providerName = properties.getProvider().toLowerCase();
        
        log.info("🔧 Configurando LLM Provider: {}", providerName);
        
        LLMProvider selectedProvider = switch (providerName) {
            case "gemini" -> geminiProvider;
            case "openai" -> openAIProvider;
            case "anthropic" -> anthropicProvider;
            case "openrouter" -> openRouterProvider;
            default -> {
                log.warn("⚠️ Provider '{}' desconhecido, usando Gemini", providerName);
                yield geminiProvider;
            }
        };
        
        if (!selectedProvider.isAvailable()) {
            if (properties.isEnableFallback()) {
                log.warn("⚠️ {} não disponível, tentando fallback", providerName);
                return getFallbackProvider();
            } else {
                throw new RuntimeException(
                    "Provider " + providerName + " não está configurado corretamente"
                );
            }
        }
        
        log.info("✅ Provider ativo: {}", selectedProvider.getProviderName());
        return selectedProvider;
    }
    
    // ====================================
    // Retorna primeiro provider disponível
    // ====================================
    private LLMProvider getFallbackProvider() {
        if (geminiProvider.isAvailable()) {
            log.info("🔄 Fallback: Gemini");
            return geminiProvider;
        }
        if (openAIProvider.isAvailable()) {
            log.info("🔄 Fallback: OpenAI");
            return openAIProvider;
        }
        if (anthropicProvider.isAvailable()) {
            log.info("🔄 Fallback: Anthropic");
            return anthropicProvider;
        }
        if (openRouterProvider.isAvailable()) {
            log.info("🔄 Fallback: OpenRouter");
            return openRouterProvider;
        }
        
        throw new RuntimeException("Nenhum provider LLM disponível!");
    }
}