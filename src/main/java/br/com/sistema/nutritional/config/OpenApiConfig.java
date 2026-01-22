package br.com.sistema.nutritional.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;

/**
 * Configuração do OpenAPI/Swagger para Nutritional Plan Assistant.
 * 
 * Fornece documentação interativa da API em:
 * - Swagger UI: http://localhost:8083/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8083/api-docs
 * 
 * @author César Augusto
 * @version 1.0.0
 */
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🥗 Nutritional Plan AI Assistant API")
                        .version("1.0.0")
                        .description("""
                                API REST para assistente inteligente de planos nutricionais com IA (Google Gemini).
                                
                                ## 🎯 Funcionalidades
                                
                                - **Cálculo de TMB**: Taxa Metabólica Basal (Harris-Benedict)
                                - **Cálculo de GET**: Gasto Energético Total
                                - **Distribuição de Macros**: Proteínas, carboidratos e gorduras
                                - **Recomendações**: Personalizadas por objetivo
                                - **Ajuste de Intensidade**: Por nível de atividade física
                                - **Chat com IA**: Orientação nutricional em linguagem natural
                                
                                ## 🎯 Objetivos Suportados
                                
                                - **Emagrecimento**: Déficit calórico de 15-20%
                                - **Ganho de Massa**: Superávit calórico de 10-15%
                                - **Manutenção**: Manutenção do peso atual
                                
                                ## 🏃 Intensidades de Exercício
                                
                                - **Sedentário**: GET = TMB × 1.2
                                - **Leve**: GET = TMB × 1.375
                                - **Moderado**: GET = TMB × 1.55
                                - **Intenso**: GET = TMB × 1.725
                                - **Muito Intenso**: GET = TMB × 1.9
                                
                                ## 🔧 Tecnologias
                                
                                - Spring Boot 3.2.5
                                - Java 21
                                - LangChain4j 1.7.1
                                - Google Gemini AI
                                - MapStruct 1.5.5
                                
                                ## 🚀 Como Usar
                                
                                1. Configure `GEMINI_API_KEY`
                                2. Envie POST para `/api/v1/plano/calcular` com dados do paciente
                                3. Receba plano completo com TMB, GET, macros e recomendações
                                
                                ## 📊 Exemplo de Request
```json
                                {
                                  "nome": "João Silva",
                                  "idade": 30,
                                  "pesoAtual": 80.0,
                                  "objetivo": "emagrecimento",
                                  "intensidadeExercicio": "moderado"
                                }
```
                                
                                ## 📚 Documentação
                                
                                Para mais informações, visite o [GitHub](https://github.com/seu-usuario/spring-ai-nutritional-plan)
                                """)
                        .contact(new Contact()
                                .name("César Augusto")
                                .email("cesar.augusto.rj1@gmail.com")
                                .url("https://portfolio.cesaravb.com.br"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8083")
                                .description("🖥️ Servidor Local de Desenvolvimento"),
                        new Server()
                                .url("https://nutrition.sua-empresa.com")
                                .description("🌐 Servidor de Produção")
                ))
                .tags(List.of(
                        new Tag()
                                .name("Plano Nutricional")
                                .description("Endpoints de cálculo e geração de planos"),
                        new Tag()
                                .name("Cálculos")
                                .description("Cálculos de TMB, GET e macronutrientes"),
                        new Tag()
                                .name("Chat Assistant")
                                .description("Interação com IA para orientação nutricional"),
                        new Tag()
                                .name("Pacientes")
                                .description("Gerenciamento de dados de pacientes"),
                        new Tag()
                                .name("Health")
                                .description("Endpoints de saúde e status do serviço")
                ));
    }
}