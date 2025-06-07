package com.alertae.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisições de criação de alerta, contendo informações de endereço para geocodificação.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detalhes do alerta com informações de endereço para geocodificação")
public class AddressRequest {

    /**
     * Título do alerta.
     */
    @Schema(description = "Título do alerta", example = "Incêndio na floresta")
    private String title;

    /**
     * Mensagem detalhada do alerta.
     */
    @Schema(description = "Mensagem detalhada do alerta", example = "Um incêndio de grandes proporções foi avistado na floresta próxima ao bairro X.")
    private String message;

    /**
     * Email para notificação.
     */
    @Schema(description = "Email para notificação", example = "usuario@example.com")
    private String emailNotification;

    /**
     * Endereço completo (rua, número).
     */
    @Schema(description = "Endereço completo (rua, número)", example = "Rua das Flores, 123")
    private String street;

    /**
     * Bairro do endereço.
     */
    @Schema(description = "Bairro", example = "Centro")
    private String neighborhood;

    /**
     * Cidade do endereço.
     */
    @Schema(description = "Cidade", example = "São Paulo")
    private String city;

    /**
     * Estado do endereço (sigla).
     */
    @Schema(description = "Estado (sigla)", example = "SP")
    private String state;

    /**
     * País do endereço. Valor padrão: Brasil.
     */
    @Schema(description = "País", example = "Brasil", defaultValue = "Brasil")
    private String country = "Brasil";
}