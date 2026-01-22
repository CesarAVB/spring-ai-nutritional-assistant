package br.com.sistema.nutritional.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * Configuração CORS (Cross-Origin Resource Sharing) para Nutritional Plan Assistant.
 * 
 * Permite que aplicações frontend (Angular, React, mobile apps)
 * acessem a API de cálculo de planos nutricionais.
 * 
 * @author César Augusto
 * @version 1.0.0
 */
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // ============================================
        // ORIGENS PERMITIDAS
        // ============================================
        
        // DESENVOLVIMENTO
        config.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "https://localhost:*"
        ));
        
        // PRODUÇÃO
        // config.setAllowedOrigins(Arrays.asList(
        //     "https://nutri.sua-empresa.com",
        //     "https://app-nutricao.sua-empresa.com"
        // ));
        
        // ============================================
        // MÉTODOS HTTP PERMITIDOS
        // ============================================
        
        config.setAllowedMethods(Arrays.asList(
                "GET",      // Consultar planos
                "POST",     // Calcular novos planos
                "PUT",      // Atualizar planos
                "DELETE",   // Remover planos
                "OPTIONS"   // Preflight
        ));
        
        // ============================================
        // HEADERS PERMITIDOS
        // ============================================
        
        config.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "X-API-Key",
                "Cache-Control"
        ));
        
        // ============================================
        // HEADERS EXPOSTOS
        // ============================================
        
        config.setExposedHeaders(Arrays.asList(
                "Authorization",
                "X-Plan-Version",      // Versão do plano
                "X-Calculation-Time",  // Tempo de cálculo
                "X-Total-Plans"        // Total de planos
        ));
        
        // ============================================
        // CONFIGURAÇÕES ADICIONAIS
        // ============================================
        
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        
        // ============================================
        // APLICAR CONFIGURAÇÃO
        // ============================================
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}