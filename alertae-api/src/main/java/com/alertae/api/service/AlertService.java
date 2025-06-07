package com.alertae.api.service;

import com.alertae.api.dto.AddressRequest;
import com.alertae.api.model.Alert;
import com.alertae.api.repository.SupabaseAlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Serviço responsável pela lógica de negócio relacionada aos alertas.
 * Realiza operações de criação, leitura, atualização e exclusão de alertas,
 * além de obter coordenadas geográficas a partir de endereços.
 */
@Service
public class AlertService {

    private final SupabaseAlertRepository alertRepository;
    private final GeocodingService geocodingService;

    /**
     * Construtor para injeção de dependências.
     * @param alertRepository repositório de alertas (Supabase)
     * @param geocodingService serviço de geocodificação de endereços
     */
    @Autowired
    public AlertService(SupabaseAlertRepository alertRepository, GeocodingService geocodingService) {
        this.alertRepository = alertRepository;
        this.geocodingService = geocodingService;
    }

    /**
     * Cria um novo alerta, obtendo as coordenadas do endereço com lógica de fallback.
     *
     * @param addressRequest Os dados do alerta incluindo o endereço.
     * @return O alerta criado com coordenadas.
     * @throws IllegalArgumentException Se as coordenadas não puderem ser encontradas após todas as tentativas.
     * @throws IOException Se houver um erro de comunicação inesperado com o Supabase.
     */
    public Alert createAlert(AddressRequest addressRequest) throws IOException {
        double[] coords = geocodingService.getCoordinatesFromAddress(
                addressRequest.getStreet(),
                addressRequest.getNeighborhood(),
                addressRequest.getCity(),
                addressRequest.getCountry(),
                addressRequest.getState()
        );

        if (coords == null) {
            throw new IllegalArgumentException("Não foi possível encontrar coordenadas para o endereço fornecido: " +
                    addressRequest.getStreet() + ", " + addressRequest.getNeighborhood() + ", " +
                    addressRequest.getCity() + ", " + addressRequest.getState() + ", " + addressRequest.getCountry());
        }

        Alert alert = new Alert();
        alert.setTitle(addressRequest.getTitle());
        alert.setMessage(addressRequest.getMessage());
        alert.setEmailNotification(addressRequest.getEmailNotification());
        alert.setLat(coords[0]);
        alert.setLongitude(coords[1]);

        return alertRepository.createAlert(alert);
    }

    /**
     * Retorna todos os alertas cadastrados.
     * @return lista de alertas
     * @throws IOException em caso de erro de comunicação com o Supabase
     */
    public List<Alert> getAllAlerts() throws IOException {
        return alertRepository.getAllAlerts();
    }

    /**
     * Busca um alerta pelo seu ID.
     * @param id identificador do alerta
     * @return alerta encontrado ou null se não existir
     * @throws IOException em caso de erro de comunicação com o Supabase
     */
    public Alert getAlertById(String id) throws IOException {
        return alertRepository.getAlertById(id);
    }

    /**
     * Atualiza um alerta existente.
     * @param id identificador do alerta a ser atualizado
     * @param alert objeto Alert com os dados atualizados
     * @return alerta atualizado
     * @throws IOException em caso de erro de comunicação com o Supabase
     */
    public Alert updateAlert(String id, Alert alert) throws IOException {
        return alertRepository.updateAlert(id, alert);
    }

    /**
     * Exclui um alerta pelo seu ID.
     * @param id identificador do alerta a ser excluído
     * @throws IOException em caso de erro de comunicação com o Supabase
     */
    public void deleteAlert(String id) throws IOException {
        alertRepository.deleteAlert(id);
    }
}