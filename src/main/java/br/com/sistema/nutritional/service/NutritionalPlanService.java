package br.com.sistema.nutritional.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NutritionalPlanService {
    
    private final NutritionalAiService aiService;
    
    public NutritionalPlanService(NutritionalAiService aiService) {
        this.aiService = aiService;
    }
    
    public String processMessage(String userMessage) {
        try {
            log.info("📩 Processando mensagem Nutritional Plan");
            log.info("   Mensagem: {}", userMessage.substring(0, Math.min(80, userMessage.length())));
            
            String response = aiService.chat(userMessage);
            
            log.info("✅ Resposta gerada com sucesso");
            return response;
            
        } catch (Exception e) {
            log.error("❌ Erro ao processar mensagem", e);
            return "Erro ao processar requisição: " + e.getMessage();
        }
    }
    
    public interface NutritionalAiService {
        String chat(String userMessage);
    }
}