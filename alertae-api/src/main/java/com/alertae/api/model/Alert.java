package com.alertae.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.google.gson.annotations.SerializedName;

/**
 * Representa um alerta cadastrado no sistema Alertae.
 * Contém informações sobre o alerta, localização e dados de criação.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alert {
    /**
     * Identificador único do alerta.
     */
    private String id;

    /**
     * Título do alerta.
     */
    private String title;

    /**
     * Mensagem detalhada do alerta.
     */
    private String message;

    /**
     * Email para notificação.
     */
    @SerializedName("email_notification")
    private String emailNotification;

    /**
     * Latitude da localização do alerta.
     */
    private Double lat;

    /**
     * Longitude da localização do alerta.
     */
    @SerializedName("long")
    private Double longitude;

    /**
     * Data e hora de criação do alerta.
     */
    @SerializedName("created_at")
    private String createdAt;
}