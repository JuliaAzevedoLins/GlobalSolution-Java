package com.alertae.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Swagger/OpenAPI para documentação da API Alertae.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Cria e configura o bean OpenAPI com informações da API Alertae.
     * @return instância configurada de OpenAPI.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Alertae API")
                        .version("1.0")
                        .description("API para gerenciamento de alertas do projeto Alertae"));
    }
}