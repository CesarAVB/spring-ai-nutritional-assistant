# 🥗 Nutritional Plan AI Assistant

Microserviço especializado em cálculo de planos nutricionais usando IA (Google Gemini).

## 🎯 Funcionalidades

- 🔢 Calcular TMB
- 📊 Calcular necessidades calóricas
- 🍎 Distribuir macronutrientes
- 💡 Recomendações personalizadas
- 🎯 Ajuste por objetivo (emagrecimento, ganho de massa, manutenção)
- 🏃 Ajuste por intensidade de exercício

## 🚀 Quick Start
```bash
# Configurar variáveis
export GEMINI_API_KEY=sua-chave

# Rodar
mvn spring-boot:run

# Acesso
http://localhost:8083
http://localhost:8083/swagger-ui.html
```

## 📡 Endpoints

- `POST /api/v1/plano/calcular` - Calcular plano
- `POST /api/v1/plano/chat` - Chat com assistente
- `GET /api/v1/plano/health` - Health check

## 🔧 Tecnologias

- Java 21
- Spring Boot 3.2.5
- LangChain4j 1.7.1
- Google Gemini AI
- MapStruct 1.5.5

## 📝 Exemplo de Uso
```bash
curl -X POST http://localhost:8083/api/v1/plano/calcular \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "idade": 30,
    "pesoAtual": 80.0,
    "objetivo": "emagrecimento",
    "intensidadeExercicio": "moderado"
  }'
```

## 📚 Fórmulas Utilizadas

- **TMB**: Harris-Benedict
- **GET**: TMB × fator de atividade
- **Déficit/Superávit**: 15-20% do GET
- **Macros**: Baseado em objetivo