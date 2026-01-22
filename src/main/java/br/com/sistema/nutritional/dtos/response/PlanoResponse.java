package br.com.sistema.nutritional.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Response com plano nutricional completo")
public record PlanoResponse(
        
    @Schema(description = "ID do plano", example = "1")
    Integer id,
    
    @Schema(description = "Nome do paciente", example = "João Silva")
    String nome,
    
    @Schema(description = "Idade", example = "30")
    Integer idade,
    
    @Schema(description = "Peso atual em kg", example = "80.5")
    Double pesoAtual,
    
    @Schema(description = "Taxa Metabólica Basal (TMB)", example = "1750.5")
    Double tmb,
    
    @Schema(description = "Gasto Energético Total (GET)", example = "2713.3")
    Double get,
    
    @Schema(description = "Calorias diárias recomendadas", example = "2300")
    Integer calorias,
    
    @Schema(description = "Distribuição de macronutrientes")
    MacrosResponse macros,
    
    @Schema(description = "Recomendações personalizadas")
    List<String> recomendacoes,
    
    @Schema(description = "Objetivo do plano", example = "emagrecimento")
    String objetivo,
    
    @Schema(description = "Intensidade de exercício", example = "moderado")
    String intensidadeExercicio
    
) {
    @Schema(description = "Macronutrientes em gramas")
    public record MacrosResponse(
            
        @Schema(description = "Proteínas em gramas", example = "184")
        Integer proteinas,
        
        @Schema(description = "Carboidratos em gramas", example = "230")
        Integer carboidratos,
        
        @Schema(description = "Gorduras em gramas", example = "77")
        Integer gorduras
        
    ) {}
}