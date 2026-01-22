package br.com.sistema.nutritional.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request para criar plano nutricional")
public record CreatePlanoRequest(
        
    @Schema(description = "Nome do paciente", example = "João Silva", required = true)
    String nome,
    
    @Schema(description = "Idade em anos", example = "30", required = true)
    Integer idade,
    
    @Schema(description = "Peso atual em kg", example = "80.5", required = true)
    Double pesoAtual,
    
    @Schema(
        description = "Objetivo nutricional",
        example = "emagrecimento",
        allowableValues = {"emagrecimento", "ganho_massa", "manutencao"},
        required = true
    )
    String objetivo,
    
    @Schema(
        description = "Intensidade de exercício",
        example = "moderado",
        allowableValues = {"sedentario", "leve", "moderado", "intenso", "muito_intenso"},
        required = true
    )
    String intensidadeExercicio
    
) {
    // ==================================
    // Validação completa dos campos
    // ==================================
    public boolean isValid() {
        return nome != null && !nome.trim().isEmpty()
                && idade != null && idade > 0 && idade < 150
                && pesoAtual != null && pesoAtual > 0 && pesoAtual < 500
                && objetivo != null && !objetivo.trim().isEmpty()
                && intensidadeExercicio != null && !intensidadeExercicio.trim().isEmpty();
    }
}