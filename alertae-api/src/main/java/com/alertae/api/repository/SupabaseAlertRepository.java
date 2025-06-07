package com.alertae.api.repository;

import com.alertae.api.model.Alert;
import com.google.gson.Gson;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import com.google.gson.reflect.TypeToken;

/**
 * Repositório responsável por gerenciar operações de persistência de alertas no Supabase.
 * Realiza operações de criação, leitura, atualização e exclusão de alertas via API REST do Supabase.
 */
@Repository
public class SupabaseAlertRepository {

    private final OkHttpClient httpClient;
    private final Gson gson;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.anon-key}")
    private String supabaseAnonKey;

    /**
     * Construtor do repositório de alertas.
     * @param httpClient cliente HTTP para requisições
     * @param gson objeto Gson para serialização/desserialização JSON
     */
    public SupabaseAlertRepository(OkHttpClient httpClient, Gson gson) {
        this.httpClient = httpClient;
        this.gson = gson;
    }

    /**
     * Cria um novo alerta no Supabase.
     * @param alert objeto Alert a ser criado
     * @return alerta criado com dados retornados pelo Supabase
     * @throws IOException em caso de erro de comunicação ou resposta inválida
     */
    public Alert createAlert(Alert alert) throws IOException {
        String json = gson.toJson(alert);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(supabaseUrl + "/rest/v1/alerts")
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer " + supabaseAnonKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = Objects.requireNonNull(response.body()).string();
                throw new IOException("Falha ao criar alerta: " + errorBody);
            }
            String responseBody = Objects.requireNonNull(response.body()).string();
            List<Alert> createdAlerts = gson.fromJson(responseBody, new TypeToken<List<Alert>>(){}.getType());
            if (createdAlerts != null && !createdAlerts.isEmpty()) {
                return createdAlerts.get(0);
            }
            throw new IOException("Falha ao criar alerta: Nenhum dado retornado do Supabase.");
        }
    }

    /**
     * Busca todos os alertas cadastrados no Supabase.
     * @return lista de alertas
     * @throws IOException em caso de erro de comunicação ou resposta inválida
     */
    public List<Alert> getAllAlerts() throws IOException {
        Request request = new Request.Builder()
                .url(supabaseUrl + "/rest/v1/alerts")
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer " + supabaseAnonKey)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Falha ao buscar alertas: " + Objects.requireNonNull(response.body()).string());
            }
            String responseBody = Objects.requireNonNull(response.body()).string();
            return gson.fromJson(responseBody, new TypeToken<List<Alert>>(){}.getType());
        }
    }

    /**
     * Busca um alerta pelo seu ID no Supabase.
     * @param id identificador do alerta
     * @return alerta encontrado ou null se não existir
     * @throws IOException em caso de erro de comunicação ou resposta inválida
     */
    public Alert getAlertById(String id) throws IOException {
        Request request = new Request.Builder()
                .url(supabaseUrl + "/rest/v1/alerts?id=eq." + id)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer " + supabaseAnonKey)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Falha ao buscar alerta por ID: " + Objects.requireNonNull(response.body()).string());
            }
            String responseBody = Objects.requireNonNull(response.body()).string();
            List<Alert> alerts = gson.fromJson(responseBody, new TypeToken<List<Alert>>(){}.getType());
            if (alerts != null && !alerts.isEmpty()) {
                return alerts.get(0);
            }
            return null;
        }
    }

    /**
     * Atualiza um alerta existente no Supabase.
     * @param id identificador do alerta a ser atualizado
     * @param alert objeto Alert com dados atualizados
     * @return alerta atualizado retornado pelo Supabase
     * @throws IOException em caso de erro de comunicação ou resposta inválida
     */
    public Alert updateAlert(String id, Alert alert) throws IOException {
        String json = gson.toJson(alert);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(supabaseUrl + "/rest/v1/alerts?id=eq." + id)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer " + supabaseAnonKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .patch(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = Objects.requireNonNull(response.body()).string();
                throw new IOException("Falha ao atualizar alerta: " + errorBody);
            }
            String responseBody = Objects.requireNonNull(response.body()).string();
            List<Alert> updatedAlerts = gson.fromJson(responseBody, new TypeToken<List<Alert>>(){}.getType());
            if (updatedAlerts != null && !updatedAlerts.isEmpty()) {
                return updatedAlerts.get(0);
            }
            throw new IOException("Falha ao atualizar alerta: Nenhum dado retornado do Supabase.");
        }
    }

    /**
     * Exclui um alerta do Supabase pelo seu ID.
     * @param id identificador do alerta a ser excluído
     * @throws IOException em caso de erro de comunicação ou resposta inválida
     */
    public void deleteAlert(String id) throws IOException {
        Request request = new Request.Builder()
                .url(supabaseUrl + "/rest/v1/alerts?id=eq." + id)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer " + supabaseAnonKey)
                .delete()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Falha ao excluir alerta: " + Objects.requireNonNull(response.body()).string());
            }
            // Supabase DELETE geralmente retorna 204 No Content para sucesso, sem corpo de resposta
        }
    }
}