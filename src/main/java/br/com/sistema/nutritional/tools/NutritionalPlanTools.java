package br.com.sistema.nutritional.tools;

import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NutritionalPlanTools {
    
    // Constantes para cálculos
    private static final double TMB_HOMEM_PESO = 13.75;
    private static final double TMB_HOMEM_ALTURA = 5.003;
    private static final double TMB_HOMEM_IDADE = 6.755;
    private static final double TMB_HOMEM_BASE = 66.47;
    
    // Fatores de atividade física
    private static final double FATOR_SEDENTARIO = 1.2;
    private static final double FATOR_LEVE = 1.375;
    private static final double FATOR_MODERADO = 1.55;
    private static final double FATOR_INTENSO = 1.725;
    private static final double FATOR_MUITO_INTENSO = 1.9;
    
    // Ajustes calóricos por objetivo
    private static final double DEFICIT_EMAGRECIMENTO = 0.85;  // -15%
    private static final double SUPERAVIT_GANHO_MASSA = 1.15;  // +15%
    private static final double MANUTENCAO = 1.0;  // 0%
    
    // ==================================
    // Calcula Taxa Metabólica Basal (TMB) usando fórmula de Harris-Benedict
    // ==================================
    @Tool("Calcula a Taxa Metabólica Basal (TMB) de uma pessoa. A TMB é a quantidade mínima de energia que o corpo precisa em repouso.")
    public String calcularTMB(Integer idade, Double peso) {
        try {
            log.info("📊 Calculando TMB: idade={}, peso={}", idade, peso);
            
            if (idade == null || idade <= 0 || idade > 150) {
                return "❌ Idade inválida. Deve estar entre 1 e 150 anos.";
            }
            
            if (peso == null || peso <= 0 || peso > 500) {
                return "❌ Peso inválido. Deve estar entre 1 e 500 kg.";
            }
            
            // Fórmula de Harris-Benedict (simplificada para homens)
            // TMB = 66.47 + (13.75 × peso) + (5.003 × altura) - (6.755 × idade)
            // Como não temos altura, usamos uma estimativa padrão de 170cm
            double alturaEstimada = 170.0;
            
            double tmb = TMB_HOMEM_BASE 
                    + (TMB_HOMEM_PESO * peso) 
                    + (TMB_HOMEM_ALTURA * alturaEstimada) 
                    - (TMB_HOMEM_IDADE * idade);
            
            return String.format("""
                    ✅ TMB Calculada com Sucesso!
                    
                    📊 Taxa Metabólica Basal (TMB):
                    ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                    
                    🔢 Valor: %.2f kcal/dia
                    
                    📝 O que é TMB?
                    A Taxa Metabólica Basal é a quantidade mínima de energia
                    (calorias) que seu corpo precisa em repouso absoluto para
                    manter funções vitais como:
                    • Respiração
                    • Circulação sanguínea
                    • Regulação de temperatura
                    • Funções celulares
                    
                    💡 Importante:
                    A TMB representa apenas o gasto em repouso. Para calcular
                    o gasto total diário, é necessário considerar o nível de
                    atividade física (GET - Gasto Energético Total).
                    """, tmb);
            
        } catch (Exception e) {
            log.error("❌ Erro ao calcular TMB", e);
            return "❌ Erro ao calcular TMB: " + e.getMessage();
        }
    }
    
    // ==================================
    // Calcula Gasto Energético Total (GET) baseado na TMB e nível de atividade
    // ==================================
    @Tool("Calcula o Gasto Energético Total (GET) baseado na TMB e intensidade de exercício. O GET é o total de calorias gastas por dia.")
    public String calcularGET(Double tmb, String intensidadeExercicio) {
        try {
            log.info("📊 Calculando GET: tmb={}, intensidade={}", tmb, intensidadeExercicio);
            
            if (tmb == null || tmb <= 0) {
                return "❌ TMB inválida. Calcule a TMB primeiro.";
            }
            
            if (intensidadeExercicio == null || intensidadeExercicio.trim().isEmpty()) {
                return "❌ Intensidade de exercício não informada.";
            }
            
            double fator = obterFatorAtividade(intensidadeExercicio);
            double get = tmb * fator;
            
            String descricaoIntensidade = obterDescricaoIntensidade(intensidadeExercicio);
            
            return String.format("""
                    ✅ GET Calculado com Sucesso!
                    
                    🏃 Gasto Energético Total (GET):
                    ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                    
                    🔢 Valor: %.2f kcal/dia
                    
                    📊 Cálculo:
                    • TMB: %.2f kcal/dia
                    • Fator de Atividade: %.2f (%s)
                    • GET = TMB × Fator = %.2f kcal/dia
                    
                    📝 O que é GET?
                    O Gasto Energético Total é a soma de:
                    • TMB (gasto em repouso)
                    • Atividade física
                    • Efeito térmico dos alimentos
                    • Termogênese não relacionada a exercício
                    
                    💡 Seu nível de atividade:
                    %s
                    """, 
                    get, 
                    tmb, 
                    fator, 
                    intensidadeExercicio, 
                    get,
                    descricaoIntensidade);
            
        } catch (Exception e) {
            log.error("❌ Erro ao calcular GET", e);
            return "❌ Erro ao calcular GET: " + e.getMessage();
        }
    }
    
    // ==================================
    // Calcula calorias diárias recomendadas baseadas no objetivo
    // ==================================
    @Tool("Calcula as calorias diárias recomendadas baseadas no GET e objetivo (emagrecimento, ganho de massa ou manutenção).")
    public String calcularCaloriasObjetivo(Double get, String objetivo) {
        try {
            log.info("🎯 Calculando calorias para objetivo: get={}, objetivo={}", get, objetivo);
            
            if (get == null || get <= 0) {
                return "❌ GET inválido. Calcule o GET primeiro.";
            }
            
            if (objetivo == null || objetivo.trim().isEmpty()) {
                return "❌ Objetivo não informado.";
            }
            
            double fatorObjetivo = obterFatorObjetivo(objetivo);
            int calorias = (int) Math.round(get * fatorObjetivo);
            
            String descricaoObjetivo = obterDescricaoObjetivo(objetivo);
            int diferenca = calorias - (int) Math.round(get);
            String sinalDiferenca = diferenca >= 0 ? "+" : "";
            
            return String.format("""
                    ✅ Calorias Calculadas com Sucesso!
                    
                    🎯 Calorias Recomendadas:
                    ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                    
                    🔢 Valor: %d kcal/dia
                    
                    📊 Cálculo:
                    • GET (manutenção): %.0f kcal/dia
                    • Objetivo: %s
                    • Ajuste: %s%d kcal/dia (%.0f%%)
                    • Total: %d kcal/dia
                    
                    📝 Seu Objetivo:
                    %s
                    
                    💡 Dica:
                    %s
                    """, 
                    calorias,
                    get,
                    objetivo,
                    sinalDiferenca,
                    diferenca,
                    (fatorObjetivo - 1) * 100,
                    calorias,
                    descricaoObjetivo,
                    obterDicaObjetivo(objetivo));
            
        } catch (Exception e) {
            log.error("❌ Erro ao calcular calorias", e);
            return "❌ Erro ao calcular calorias: " + e.getMessage();
        }
    }
    
    // ==================================
    // Calcula distribuição de macronutrientes (proteínas, carboidratos, gorduras)
    // ==================================
    @Tool("Calcula a distribuição de macronutrientes (proteínas, carboidratos e gorduras) em gramas baseada nas calorias e objetivo.")
    public String calcularMacronutrientes(Integer calorias, Double peso, String objetivo) {
        try {
            log.info("🍽️ Calculando macros: calorias={}, peso={}, objetivo={}", calorias, peso, objetivo);
            
            if (calorias == null || calorias <= 0) {
                return "❌ Calorias inválidas.";
            }
            
            if (peso == null || peso <= 0) {
                return "❌ Peso inválido.";
            }
            
            if (objetivo == null || objetivo.trim().isEmpty()) {
                return "❌ Objetivo não informado.";
            }
            
            // Cálculo de proteínas baseado no objetivo e peso
            double proteinasPorKg = obterProteinasPorKg(objetivo);
            int proteinas = (int) Math.round(peso * proteinasPorKg);
            int caloriasProteinas = proteinas * 4; // 4 kcal por grama
            
            // Cálculo de gorduras (25-30% das calorias totais)
            double percentualGorduras = 0.27;
            int caloriasGorduras = (int) Math.round(calorias * percentualGorduras);
            int gorduras = caloriasGorduras / 9; // 9 kcal por grama
            
            // Resto vai para carboidratos
            int caloriasCarboidratos = calorias - caloriasProteinas - caloriasGorduras;
            int carboidratos = caloriasCarboidratos / 4; // 4 kcal por grama
            
            // Percentuais
            double percProteinas = (caloriasProteinas * 100.0) / calorias;
            double percCarboidratos = (caloriasCarboidratos * 100.0) / calorias;
            double percGorduras = (caloriasGorduras * 100.0) / calorias;
            
            return String.format("""
                    ✅ Macronutrientes Calculados!
                    
                    🍽️ Distribuição de Macronutrientes:
                    ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                    
                    🥩 PROTEÍNAS: %d gramas/dia
                       • %.1fg por kg de peso corporal
                       • %d kcal (%.1f%% das calorias)
                       • Função: Construção e reparação muscular
                    
                    🍞 CARBOIDRATOS: %d gramas/dia
                       • %d kcal (%.1f%% das calorias)
                       • Função: Energia principal para treinos
                    
                    🥑 GORDURAS: %d gramas/dia
                       • %d kcal (%.1f%% das calorias)
                       • Função: Hormônios e absorção de vitaminas
                    
                    📊 Total: %d kcal/dia
                    
                    💡 Dicas de Consumo:
                    %s
                    """,
                    proteinas, proteinasPorKg, caloriasProteinas, percProteinas,
                    carboidratos, caloriasCarboidratos, percCarboidratos,
                    gorduras, caloriasGorduras, percGorduras,
                    calorias,
                    obterDicasMacros(objetivo));
            
        } catch (Exception e) {
            log.error("❌ Erro ao calcular macros", e);
            return "❌ Erro ao calcular macronutrientes: " + e.getMessage();
        }
    }
    
    // ==================================
    // Gera recomendações personalizadas baseadas no objetivo
    // ==================================
    @Tool("Gera recomendações personalizadas de nutrição e treino baseadas no objetivo do usuário.")
    public String gerarRecomendacoes(String objetivo, String intensidadeExercicio) {
        try {
            log.info("💡 Gerando recomendações: objetivo={}, intensidade={}", objetivo, intensidadeExercicio);
            
            if (objetivo == null || objetivo.trim().isEmpty()) {
                return "❌ Objetivo não informado.";
            }
            
            StringBuilder recomendacoes = new StringBuilder();
            recomendacoes.append("💡 Recomendações Personalizadas:\n");
            recomendacoes.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            
            // Recomendações específicas por objetivo
            switch (objetivo.toLowerCase()) {
                case "emagrecimento" -> {
                    recomendacoes.append("""
                            🎯 FOCO: Perda de Gordura com Preservação Muscular
                            
                            🍽️ ALIMENTAÇÃO:
                            • Mantenha déficit calórico de 15-20%
                            • Priorize proteínas em todas as refeições
                            • Escolha carboidratos de baixo índice glicêmico
                            • Aumente consumo de vegetais (fibras)
                            • Beba 2-3 litros de água por dia
                            • Evite alimentos ultraprocessados
                            
                            🏋️ TREINO:
                            • Combine treino de força com cardio
                            • Treino de força: 3-4x por semana
                            • Cardio moderado: 2-3x por semana
                            • HIIT: 1-2x por semana (opcional)
                            
                            ⏰ TIMING:
                            • Coma a cada 3-4 horas
                            • Não pule o café da manhã
                            • Jantar mais leve
                            • Evite carboidratos à noite
                            
                            💊 SUPLEMENTAÇÃO (OPCIONAL):
                            • Whey Protein (se não atingir proteína na dieta)
                            • Multivitamínico
                            • Ômega 3
                            • Cafeína pré-treino
                            """);
                }
                case "ganho_massa", "ganho de massa" -> {
                    recomendacoes.append("""
                            🎯 FOCO: Hipertrofia Muscular
                            
                            🍽️ ALIMENTAÇÃO:
                            • Mantenha superávit calórico de 10-15%
                            • Consuma 2-2.5g de proteína por kg
                            • Carboidratos são seus aliados (60% das calorias)
                            • Não tenha medo de gorduras boas
                            • Beba 3-4 litros de água por dia
                            • Faça 5-6 refeições por dia
                            
                            🏋️ TREINO:
                            • Treino de força: 4-6x por semana
                            • Foco em exercícios compostos
                            • Progressive overload é essencial
                            • Cardio leve: 1-2x por semana
                            • Descanso adequado: 7-9h de sono
                            
                            ⏰ TIMING:
                            • Refeição pré-treino: 1-2h antes
                            • Refeição pós-treino: até 1h após
                            • Carboidratos antes e depois do treino
                            • Proteína antes de dormir (caseína)
                            
                            💊 SUPLEMENTAÇÃO (OPCIONAL):
                            • Whey Protein
                            • Creatina (5g/dia)
                            • Maltodextrina (pós-treino)
                            • BCAA (durante treino)
                            • Hipercalórico (se dificuldade em comer)
                            """);
                }
                case "manutencao", "manutenção" -> {
                    recomendacoes.append("""
                            🎯 FOCO: Manter Peso e Composição Corporal
                            
                            🍽️ ALIMENTAÇÃO:
                            • Mantenha calorias de manutenção
                            • Dieta balanceada e variada
                            • 40% carboidratos, 30% proteínas, 30% gorduras
                            • Foque em alimentos naturais
                            • Flexibilidade: 80/20 (80% saudável)
                            • Hidratação adequada
                            
                            🏋️ TREINO:
                            • Treino de força: 3-4x por semana
                            • Cardio: 2-3x por semana
                            • Variedade de exercícios
                            • Mantenha consistência
                            
                            ⏰ TIMING:
                            • Flexível, adapte à sua rotina
                            • O mais importante é a consistência
                            • Não pule refeições
                            
                            💊 SUPLEMENTAÇÃO (OPCIONAL):
                            • Multivitamínico
                            • Ômega 3
                            • Vitamina D
                            """);
                }
            }
            
            // Recomendações adicionais baseadas na intensidade
            recomendacoes.append("\n📊 AJUSTES POR INTENSIDADE:\n");
            recomendacoes.append(obterRecomendacoesPorIntensidade(intensidadeExercicio));
            
            return recomendacoes.toString();
            
        } catch (Exception e) {
            log.error("❌ Erro ao gerar recomendações", e);
            return "❌ Erro ao gerar recomendações: " + e.getMessage();
        }
    }
    
    // ==================================
    // Calcula plano nutricional completo de uma vez
    // ==================================
    @Tool("Calcula um plano nutricional completo incluindo TMB, GET, calorias, macros e recomendações.")
    public String calcularPlanoCompleto(String nome, Integer idade, Double peso, 
                                       String objetivo, String intensidadeExercicio) {
        try {
            log.info("📋 Calculando plano completo para: {}", nome);
            
            // Validações
            if (nome == null || nome.trim().isEmpty()) {
                return "❌ Nome não informado.";
            }
            
            // Calcular TMB
            double tmb = calcularTMBNumerico(idade, peso);
            
            // Calcular GET
            double fatorAtividade = obterFatorAtividade(intensidadeExercicio);
            double get = tmb * fatorAtividade;
            
            // Calcular calorias do objetivo
            double fatorObjetivo = obterFatorObjetivo(objetivo);
            int calorias = (int) Math.round(get * fatorObjetivo);
            
            // Calcular macros
            double proteinasPorKg = obterProteinasPorKg(objetivo);
            int proteinas = (int) Math.round(peso * proteinasPorKg);
            int caloriasProteinas = proteinas * 4;
            
            int caloriasGorduras = (int) Math.round(calorias * 0.27);
            int gorduras = caloriasGorduras / 9;
            
            int caloriasCarboidratos = calorias - caloriasProteinas - caloriasGorduras;
            int carboidratos = caloriasCarboidratos / 4;
            
            // Montar resposta completa
            return String.format("""
                    ✅ PLANO NUTRICIONAL COMPLETO
                    ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                    
                    👤 DADOS PESSOAIS:
                    • Nome: %s
                    • Idade: %d anos
                    • Peso: %.1f kg
                    • Objetivo: %s
                    • Intensidade: %s
                    
                    📊 CÁLCULOS ENERGÉTICOS:
                    • TMB (Taxa Metabólica Basal): %.0f kcal/dia
                    • GET (Gasto Energético Total): %.0f kcal/dia
                    • Calorias Recomendadas: %d kcal/dia
                    
                    🍽️ MACRONUTRIENTES:
                    • 🥩 Proteínas: %d g/dia (%d kcal)
                    • 🍞 Carboidratos: %d g/dia (%d kcal)
                    • 🥑 Gorduras: %d g/dia (%d kcal)
                    
                    💡 PRÓXIMOS PASSOS:
                    1. Siga as calorias e macros recomendados
                    2. Faça 4-6 refeições por dia
                    3. Beba bastante água (2-4L/dia)
                    4. Durma bem (7-9h por noite)
                    5. Seja consistente!
                    
                    📝 Peça recomendações detalhadas para seu objetivo!
                    """,
                    nome, idade, peso, objetivo, intensidadeExercicio,
                    tmb, get, calorias,
                    proteinas, caloriasProteinas,
                    carboidratos, caloriasCarboidratos,
                    gorduras, caloriasGorduras);
            
        } catch (Exception e) {
            log.error("❌ Erro ao calcular plano completo", e);
            return "❌ Erro ao calcular plano: " + e.getMessage();
        }
    }
    
    // ==================================
    // MÉTODOS AUXILIARES PRIVADOS
    // ==================================
    
    private double calcularTMBNumerico(Integer idade, Double peso) {
        double alturaEstimada = 170.0;
        return TMB_HOMEM_BASE 
                + (TMB_HOMEM_PESO * peso) 
                + (TMB_HOMEM_ALTURA * alturaEstimada) 
                - (TMB_HOMEM_IDADE * idade);
    }
    
    private double obterFatorAtividade(String intensidade) {
        return switch (intensidade.toLowerCase()) {
            case "sedentario", "sedentário" -> FATOR_SEDENTARIO;
            case "leve" -> FATOR_LEVE;
            case "moderado" -> FATOR_MODERADO;
            case "intenso" -> FATOR_INTENSO;
            case "muito_intenso", "muito intenso" -> FATOR_MUITO_INTENSO;
            default -> FATOR_MODERADO;
        };
    }
    
    private double obterFatorObjetivo(String objetivo) {
        return switch (objetivo.toLowerCase()) {
            case "emagrecimento" -> DEFICIT_EMAGRECIMENTO;
            case "ganho_massa", "ganho de massa" -> SUPERAVIT_GANHO_MASSA;
            case "manutencao", "manutenção" -> MANUTENCAO;
            default -> MANUTENCAO;
        };
    }
    
    private double obterProteinasPorKg(String objetivo) {
        return switch (objetivo.toLowerCase()) {
            case "emagrecimento" -> 2.0;  // Maior proteína para preservar músculo
            case "ganho_massa", "ganho de massa" -> 2.2;  // Alta proteína para construir
            case "manutencao", "manutenção" -> 1.6;  // Proteína moderada
            default -> 1.6;
        };
    }
    
    private String obterDescricaoIntensidade(String intensidade) {
        return switch (intensidade.toLowerCase()) {
            case "sedentario", "sedentário" -> 
                "Sedentário - Pouca ou nenhuma atividade física";
            case "leve" -> 
                "Leve - Exercícios leves 1-3x por semana";
            case "moderado" -> 
                "Moderado - Exercícios moderados 3-5x por semana";
            case "intenso" -> 
                "Intenso - Exercícios intensos 6-7x por semana";
            case "muito_intenso", "muito intenso" -> 
                "Muito Intenso - Exercícios intensos 2x por dia ou trabalho físico pesado";
            default -> "Moderado";
        };
    }
    
    private String obterDescricaoObjetivo(String objetivo) {
        return switch (objetivo.toLowerCase()) {
            case "emagrecimento" -> 
                "Emagrecimento - Perda de gordura com déficit calórico de 15%";
            case "ganho_massa", "ganho de massa" -> 
                "Ganho de Massa - Hipertrofia muscular com superávit de 15%";
            case "manutencao", "manutenção" -> 
                "Manutenção - Manter peso e composição corporal atual";
            default -> "Manutenção";
        };
    }
    
    private String obterDicaObjetivo(String objetivo) {
        return switch (objetivo.toLowerCase()) {
            case "emagrecimento" -> 
                "Combine déficit calórico com treino de força para preservar músculos!";
            case "ganho_massa", "ganho de massa" -> 
                "Superávit moderado + treino pesado = ganhos de qualidade!";
            case "manutencao", "manutenção" -> 
                "Consistência é a chave para manter seus resultados!";
            default -> "Seja consistente e os resultados virão!";
        };
    }
    
    private String obterDicasMacros(String objetivo) {
        return switch (objetivo.toLowerCase()) {
            case "emagrecimento" -> """
                • Proteína em todas as refeições (saciedade)
                • Carboidratos antes do treino (energia)
                • Gorduras boas (azeite, abacate, castanhas)
                • Fibras para saciedade (vegetais)
                """;
            case "ganho_massa", "ganho de massa" -> """
                • Proteína distribuída ao longo do dia
                • Carboidratos antes e depois do treino
                • Não tenha medo de gorduras boas
                • Coma de 3 em 3 horas
                """;
            case "manutencao", "manutenção" -> """
                • Dieta balanceada e variada
                • Foque em alimentos naturais
                • Flexibilidade: 80/20 rule
                • Escute seu corpo
                """;
            default -> "Consulte um nutricionista para orientação personalizada!";
        };
    }
    
    private String obterRecomendacoesPorIntensidade(String intensidade) {
        return switch (intensidade.toLowerCase()) {
            case "sedentario", "sedentário" -> """
                • Comece devagar, aumente intensidade gradualmente
                • Caminhe 30min por dia para começar
                • Foco em criar o hábito primeiro
                """;
            case "leve" -> """
                • Aumente frequência gradualmente
                • Adicione 1 dia de treino por mês
                • Varie os tipos de exercício
                """;
            case "moderado" -> """
                • Excelente frequência! Mantenha consistência
                • Varie intensidade durante a semana
                • 1-2 dias de descanso ativo
                """;
            case "intenso" -> """
                • Atenção ao overtraining!
                • Pelo menos 1 dia de descanso completo
                • Sono de 8-9h é essencial
                • Considere periodização
                """;
            case "muito_intenso", "muito intenso" -> """
                • CUIDADO: Risco alto de overtraining!
                • Monitore sinais de fadiga
                • Sono de 9h+ é obrigatório
                • Considere acompanhamento profissional
                • Periodização é essencial
                """;
            default -> "Mantenha consistência nos treinos!";
        };
    }
}