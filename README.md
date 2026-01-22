# 🥗 Nutritional Plan AI Assistant

Um microserviço inteligente especializado em cálculo de planos nutricionais personalizados usando IA (Google Gemini, OpenAI, Anthropic e OpenRouter).

## 🎯 Funcionalidades

- ✅ **Cálculo de TMB** - Taxa Metabólica Basal (Harris-Benedict)
- ✅ **Cálculo de GET** - Gasto Energético Total
- ✅ **Distribuição de Macronutrientes** - Proteínas, carboidratos e gorduras personalizados
- ✅ **Recomendações Inteligentes** - Orientações personalizadas por objetivo
- ✅ **Ajuste por Objetivo** - Emagrecimento, ganho de massa, manutenção
- ✅ **Ajuste por Intensidade** - Sedentário até muito intenso
- ✅ **Chat com IA** - Assistente nutricional em linguagem natural
- ✅ **Múltiplos Providers** - Suporte para Google Gemini, OpenAI, Anthropic e OpenRouter
- ✅ **Fallback Automático** - Comutação entre providers em caso de indisponibilidade

## 🚀 Quick Start

### Pré-requisitos

- **Java 21+**
- **Maven 3.8+**
- **Git**
- Chave de API de um provedor LLM (Gemini, OpenAI, Anthropic ou OpenRouter)

### Instalação

1. **Clone o repositório**
```bash
git clone https://github.com/seu-usuario/nutritional-plan-ai.git
cd nutritional-plan-ai
```

2. **Configure as variáveis de ambiente**
```bash
# Google Gemini (padrão)
export GEMINI_API_KEY=sua-chave-aqui

# OU OpenAI
export OPENAI_API_KEY=sua-chave-aqui

# OU Anthropic
export ANTHROPIC_API_KEY=sua-chave-aqui

# OU OpenRouter
export OPENROUTER_API_KEY=sua-chave-aqui
```

3. **Execute a aplicação**
```bash
mvn clean install
mvn spring-boot:run
```

4. **Acesse a API**
- 🌐 **API**: `http://localhost:8083`
- 📚 **Swagger UI**: `http://localhost:8083/swagger-ui.html`
- 📄 **OpenAPI JSON**: `http://localhost:8083/api-docs`

## 📡 Endpoints

### 🧮 Calcular Plano Nutricional

```bash
POST /api/v1/plano/calcular
Content-Type: application/json

{
  "nome": "João Silva",
  "idade": 30,
  "pesoAtual": 80.0,
  "objetivo": "emagrecimento",
  "intensidadeExercicio": "moderado"
}
```

**Resposta:**
```json
{
  "success": true,
  "assistant": "NutritionalPlanAssistant",
  "type": "calcular_plano",
  "question": "Calcule meu plano nutricional",
  "data": "✅ PLANO NUTRICIONAL COMPLETO...",
  "error": null,
  "timestamp": "2025-01-22T15:30:00"
}
```

### 💬 Chat com Assistente

```bash
POST /api/v1/plano/chat
Content-Type: application/json

{
  "message": "Qual é minha TMB? Tenho 30 anos e peso 80kg"
}
```

**Resposta:**
```json
{
  "success": true,
  "assistant": "NutritionalPlanAssistant",
  "type": "chat",
  "question": "Qual é minha TMB? Tenho 30 anos e peso 80kg",
  "data": "✅ TMB Calculada com Sucesso!...",
  "error": null,
  "timestamp": "2025-01-22T15:30:00"
}
```

### 🏥 Health Check

```bash
GET /api/v1/plano/health
```

**Resposta:**
```
✅ Nutritional Plan Assistant Online
```

## 🎯 Objetivos Suportados

| Objetivo | Déficit/Superávit | Proteína/kg | Caso de Uso |
|----------|------------------|------------|-----------|
| **Emagrecimento** | -15% | 2.0g | Perda de gordura preservando músculos |
| **Ganho de Massa** | +15% | 2.2g | Hipertrofia muscular |
| **Manutenção** | 0% | 1.6g | Manter peso e composição |

## 🏃 Intensidades de Exercício

| Intensidade | Fator | Descrição |
|-----------|-------|-----------|
| **Sedentário** | 1.2 | Pouca ou nenhuma atividade |
| **Leve** | 1.375 | 1-3x por semana |
| **Moderado** | 1.55 | 3-5x por semana |
| **Intenso** | 1.725 | 6-7x por semana |
| **Muito Intenso** | 1.9 | 2x/dia ou trabalho físico pesado |

## 📚 Fórmulas Utilizadas

### Taxa Metabólica Basal (TMB) - Harris-Benedict

```
TMB = 66.47 + (13.75 × peso) + (5.003 × altura) - (6.755 × idade)
```

### Gasto Energético Total (GET)

```
GET = TMB × Fator de Atividade
```

### Calorias por Objetivo

```
Calorias = GET × Fator do Objetivo

Emagrecimento: GET × 0.85 (-15%)
Ganho de Massa: GET × 1.15 (+15%)
Manutenção: GET × 1.0
```

### Distribuição de Macronutrientes

```
Proteínas: (peso em kg) × (proteína/kg do objetivo) × 4 kcal
Gorduras: calorias totais × 27% ÷ 9 kcal
Carboidratos: (calorias totais - proteínas - gorduras) ÷ 4 kcal
```

## 🔧 Configuração Avançada

### Configurar Provider Padrão

Edite `application.yml`:

```yaml
llm:
  provider: gemini  # gemini, openai, anthropic, openrouter
  enable-fallback: true
  
  gemini:
    api-key: ${GEMINI_API_KEY}
    model-name: gemini-1.5-flash
  
  openai:
    api-key: ${OPENAI_API_KEY}
    model-name: gpt-4o-mini
  
  anthropic:
    api-key: ${ANTHROPIC_API_KEY}
    model-name: claude-3-5-sonnet-20241022
  
  openrouter:
    api-key: ${OPENROUTER_API_KEY}
    model-name: anthropic/claude-3.5-sonnet
    base-url: https://openrouter.ai/api/v1
```

### CORS (Cross-Origin Resource Sharing)

Configure origens permitidas em `CorsConfig.java`:

```java
// DESENVOLVIMENTO
config.setAllowedOriginPatterns(Arrays.asList(
    "http://localhost:*",
    "http://127.0.0.1:*"
));

// PRODUÇÃO
// config.setAllowedOrigins(Arrays.asList(
//     "https://nutri.sua-empresa.com"
// ));
```

## 📊 Exemplo Completo de Uso

### Via cURL

```bash
curl -X POST http://localhost:8083/api/v1/plano/calcular \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Maria Santos",
    "idade": 28,
    "pesoAtual": 65.5,
    "objetivo": "ganho_massa",
    "intensidadeExercicio": "intenso"
  }'
```

### Via Angular/TypeScript

```typescript
import { HttpClient } from '@angular/common/http';

@Injectable()
export class NutritionalService {
  private apiUrl = 'http://localhost:8083/api/v1/plano';

  constructor(private http: HttpClient) {}

  calcularPlano(dados: CreatePlanoRequest) {
    return this.http.post<AssistantResponse>(
      `${this.apiUrl}/calcular`,
      dados
    );
  }

  chatAssistant(message: string) {
    return this.http.post<AssistantResponse>(
      `${this.apiUrl}/chat`,
      { message }
    );
  }
}
```

```typescript
// Usar o serviço
this.nutritionalService.calcularPlano({
  nome: 'João',
  idade: 30,
  pesoAtual: 80,
  objetivo: 'emagrecimento',
  intensidadeExercicio: 'moderado'
}).subscribe(response => {
  console.log(response.data);
});
```

## 🏗️ Arquitetura

```
src/main/java/br/com/sistema/nutritional/
├── config/           # Configurações (CORS, OpenAPI, LLM)
├── controller/       # Controllers REST
├── dtos/
│   ├── request/      # DTOs de entrada
│   └── response/     # DTOs de saída
├── llm/              # Providers de LLM (Gemini, OpenAI, etc)
├── models/           # Modelos de domínio
├── service/          # Serviços de negócio
└── tools/            # Ferramentas para cálculos (LangChain4j)
```

## 🔧 Tecnologias

| Tecnologia | Versão | Propósito |
|-----------|--------|----------|
| Java | 21 | Linguagem principal |
| Spring Boot | 3.2.5 | Framework web |
| LangChain4j | 1.7.1 | Orquestração de LLM |
| Swagger/OpenAPI | 3.x | Documentação interativa |
| MapStruct | 1.5.5 | Mapeamento de objetos |
| Lombok | 1.18.x | Redução de boilerplate |
| Google Gemini API | - | Modelo de IA padrão |
| OpenAI API | - | Alternativa de IA |
| Anthropic API | - | Alternativa de IA |

## 📈 Roadmap

- [ ] Persistência em banco de dados
- [ ] Histórico de planos por usuário
- [ ] Ajustes dinâmicos de plano
- [ ] Integração com wearables
- [ ] Suporte a múltiplos idiomas
- [ ] Sistema de avaliações
- [ ] API de sincronização com apps mobile
- [ ] Dashboard admin
