package br.com.sistema.nutritional.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.sistema.nutritional.dtos.request.CreatePlanoRequest;
import br.com.sistema.nutritional.dtos.request.NutritionalChatRequest;
import br.com.sistema.nutritional.dtos.response.AssistantResponse;
import br.com.sistema.nutritional.service.NutritionalPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/plano")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Nutritional Plan Assistant", description = "Assistente de IA para planos nutricionais")
public class NutritionalPlanController {
    
    private final NutritionalPlanService planService;
    
    // ==================================
    // Chat com o assistente nutricional
    // ==================================
    @PostMapping("/chat")
    @Operation(
        summary = "Chat com assistente nutricional",
        description = "Envie uma mensagem em linguagem natural para o assistente processar"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Mensagem processada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AssistantResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "assistant": "NutritionalPlanAssistant",
                      "type": "chat",
                      "question": "Calcule meu plano nutricional",
                      "data": "✅ Plano calculado...",
                      "error": null,
                      "timestamp": "2025-01-22T15:30:00"
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Request inválido"
        )
    })
    public ResponseEntity<AssistantResponse> chat(@RequestBody NutritionalChatRequest request) {
        log.info("💬 Chat recebido");
        
        if (!request.isValid()) {
            return ResponseEntity.badRequest()
                    .body(AssistantResponse.error(
                            request.message(),
                            "Mensagem não pode ser vazia"
                    ));
        }
        
        try {
            String response = planService.processMessage(request.message());
            return ResponseEntity.ok(AssistantResponse.success(request.message(), response));
            
        } catch (Exception e) {
            log.error("❌ Erro ao processar chat", e);
            return ResponseEntity.internalServerError()
                    .body(AssistantResponse.error(
                            request.message(),
                            "Erro ao processar mensagem: " + e.getMessage()
                    ));
        }
    }
    
    // ==================================
    // Calcula plano nutricional direto (sem chat)
    // ==================================
    @PostMapping("/calcular")
    @Operation(
        summary = "Calcular plano nutricional",
        description = "Calcula plano nutricional completo baseado nos dados fornecidos"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Plano calculado com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos"
        )
    })
    public ResponseEntity<AssistantResponse> calcularPlano(@RequestBody CreatePlanoRequest request) {
        log.info("🧮 Calculando plano para: {}", request.nome());
        
        if (!request.isValid()) {
            return ResponseEntity.badRequest()
                    .body(AssistantResponse.error(
                            "Calcular plano",
                            "Dados inválidos. Verifique os campos obrigatórios."
                    ));
        }
        
        try {
            String prompt = String.format(
                    "Calcule um plano nutricional completo para %s, %d anos, %.1fkg, " +
                    "objetivo %s, exercícios %s",
                    request.nome(),
                    request.idade(),
                    request.pesoAtual(),
                    request.objetivo(),
                    request.intensidadeExercicio()
            );
            
            String response = planService.processMessage(prompt);
            
            return ResponseEntity.ok(AssistantResponse.success(
                    "Calcular plano nutricional",
                    response
            ));
            
        } catch (Exception e) {
            log.error("❌ Erro ao calcular plano", e);
            return ResponseEntity.internalServerError()
                    .body(AssistantResponse.error(
                            "Calcular plano",
                            "Erro: " + e.getMessage()
                    ));
        }
    }
    
    // ==================================
    // Health check do serviço
    // ==================================
    @GetMapping("/health")
    @Operation(
        summary = "Health check",
        description = "Verifica se o serviço está online"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Serviço online"
    )
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("✅ Nutritional Plan Assistant Online");
    }
}