package com.alertae.api.controller;

import com.alertae.api.dto.AddressRequest;
import com.alertae.api.model.Alert;
import com.alertae.api.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Controlador REST para gerenciamento de alertas no sistema Alertae.
 * Fornece endpoints para criar, listar, buscar, atualizar e excluir alertas.
 */
@RestController
@RequestMapping("/api/v1/alerts")
@Tag(name = "Alerts", description = "Gerenciamento de Alertas do Alertae")
public class AlertController {

    private final AlertService alertService;

    /**
     * Construtor para injeção do serviço de alertas.
     * @param alertService serviço responsável pelas operações de alerta
     */
    @Autowired
    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    /**
     * Cria um novo alerta a partir de um endereço.
     * O endereço é geocodificado para obter latitude e longitude.
     * @param addressRequest DTO contendo os dados do endereço
     * @return ResponseEntity com o alerta criado e status HTTP correspondente
     */
    @PostMapping
    @Operation(summary = "Cria um novo alerta",
               description = "Cria um alerta, geocodificando o endereço fornecido para obter latitude e longitude. As coordenadas não são expostas na requisição.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Alerta criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida ou endereço não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> createAlert(@RequestBody AddressRequest addressRequest) {
        try {
            Alert createdAlert = alertService.createAlert(addressRequest);
            return new ResponseEntity<>(createdAlert, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao comunicar com serviços externos ou Supabase: " + e.getMessage());
        }
    }

    /**
     * Lista todos os alertas cadastrados.
     * @return ResponseEntity com a lista de alertas e status HTTP correspondente
     */
    @GetMapping
    @Operation(summary = "Lista todos os alertas",
               description = "Retorna uma lista de todos os alertas cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de alertas retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> getAllAlerts() {
        try {
            List<Alert> alerts = alertService.getAllAlerts();
            return ResponseEntity.ok(alerts);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao comunicar com o Supabase: " + e.getMessage());
        }
    }

    /**
     * Busca um alerta pelo seu ID.
     * @param id identificador do alerta
     * @return ResponseEntity com o alerta encontrado ou status de não encontrado
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtém um alerta por ID",
               description = "Retorna um alerta específico pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alerta encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Alerta não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> getAlertById(@Parameter(description = "ID do alerta") @PathVariable String id) {
        try {
            Alert alert = alertService.getAlertById(id);
            if (alert != null) {
                return ResponseEntity.ok(alert);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao comunicar com o Supabase: " + e.getMessage());
        }
    }

    /**
     * Atualiza um alerta existente.
     * @param id identificador do alerta a ser atualizado
     * @param alert objeto Alert com os dados atualizados
     * @return ResponseEntity com o alerta atualizado ou status de não encontrado
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um alerta existente",
               description = "Atualiza os detalhes de um alerta existente. As coordenadas lat/long podem ser atualizadas caso o endereço seja alterado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alerta atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Alerta não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> updateAlert(@Parameter(description = "ID do alerta") @PathVariable String id, @RequestBody Alert alert) {
        try {
            Alert updatedAlert = alertService.updateAlert(id, alert);
            if (updatedAlert != null) {
                return ResponseEntity.ok(updatedAlert);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao comunicar com o Supabase: " + e.getMessage());
        }
    }

    /**
     * Exclui um alerta pelo seu ID.
     * @param id identificador do alerta a ser excluído
     * @return ResponseEntity com status de sucesso ou erro
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um alerta",
               description = "Exclui um alerta do sistema pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Alerta excluído com sucesso (sem conteúdo)"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> deleteAlert(@Parameter(description = "ID do alerta") @PathVariable String id) {
        try {
            alertService.deleteAlert(id);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao comunicar com o Supabase: " + e.getMessage());
        }
    }
}