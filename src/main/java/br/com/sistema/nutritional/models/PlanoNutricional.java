package br.com.sistema.nutritional.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanoNutricional {
    
    private Integer id;
    private String nome;
    private Integer idade;
    private Double pesoAtual;
    private Double tmb;
    private Double get;
    private Integer calorias;
    private Macronutrientes macros;
    private List<String> recomendacoes;
    private String objetivo;
    private String intensidadeExercicio;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Macronutrientes {
        private Integer proteinas;
        private Integer carboidratos;
        private Integer gorduras;
    }
}