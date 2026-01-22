package br.com.sistema.nutritional.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request para chat com Nutritional Plan Assistant")
public record NutritionalChatRequest(
        
    @Schema(
        description = "Mensagem em linguagem natural",
        example = "Calcule um plano para ganho de massa muscular",
        required = true
    )
    String message
    
) {
    // ==================================
    // Validação básica
    // ==================================
    public boolean isValid() {
        return message != null && !message.trim().isEmpty();
    }
}