package com.alertae.api.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

/**
 * Serviço responsável por obter coordenadas geográficas (latitude e longitude)
 * a partir de informações de endereço, utilizando a API Nominatim (OpenStreetMap).
 * Implementa lógica de fallback para tentar diferentes níveis de detalhe do endereço.
 */
@Service
public class GeocodingService {

    private final OkHttpClient httpClient;
    private final Gson gson;

    @Value("${geocoding.api.url}")
    private String geocodingApiUrl;

    /**
     * Construtor do serviço de geocodificação.
     * @param httpClient Cliente HTTP para requisições.
     * @param gson Objeto Gson para manipulação de JSON.
     */
    public GeocodingService(OkHttpClient httpClient, Gson gson) {
        this.httpClient = httpClient;
        this.gson = gson;
    }

    /**
     * Tenta obter as coordenadas de um endereço com lógica de fallback,
     * passando por diferentes níveis de detalhe do endereço até encontrar um resultado.
     *
     * @param street Rua (e número, se houver).
     * @param neighborhood Bairro.
     * @param city Cidade.
     * @param state Estado (sigla).
     * @param country País.
     * @return Um array de double [latitude, longitude] se encontrado, ou null caso contrário.
     */
    public double[] getCoordinatesFromAddress(String street, String neighborhood, String city, String state, String country) {
        street = Objects.requireNonNullElse(street, "").trim();
        neighborhood = Objects.requireNonNullElse(neighborhood, "").trim();
        city = Objects.requireNonNullElse(city, "").trim();
        state = Objects.requireNonNullElse(state, "").trim();
        country = Objects.requireNonNullElse(country, "").trim();

        // 1. Tentar com o endereço completo (Rua, Bairro, Cidade, Estado, País)
        String fullAddress = formatAddress(street, neighborhood, city, state, country);
        if (!fullAddress.isEmpty()) {
            try {
                double[] coords = makeGeocodingRequest(fullAddress);
                if (coords != null) {
                    System.out.println("DEBUG: Geocodificação bem-sucedida (completa): " + fullAddress);
                    return coords;
                }
            } catch (IOException e) {
                System.err.println("DEBUG: Erro ao geocodificar (completa): " + fullAddress + " - " + e.getMessage());
            }
        }

        // 2. Tentar sem o número da rua (Rua sem número, Bairro, Cidade, Estado, País)
        String streetWithoutNumber = removeNumberFromStreet(street);
        if (!streetWithoutNumber.isEmpty() && !streetWithoutNumber.equals(street)) {
            String addressWithoutNumber = formatAddress(streetWithoutNumber, neighborhood, city, state, country);
            if (!addressWithoutNumber.isEmpty() && !addressWithoutNumber.equals(fullAddress)) {
                try {
                    double[] coords = makeGeocodingRequest(addressWithoutNumber);
                    if (coords != null) {
                        System.out.println("DEBUG: Geocodificação bem-sucedida (sem número): " + addressWithoutNumber);
                        return coords;
                    }
                } catch (IOException e) {
                    System.err.println("DEBUG: Erro ao geocodificar (sem número): " + addressWithoutNumber + " - " + e.getMessage());
                }
            }
        }

        // 3. Tentar com Bairro, Cidade, Estado, País (sem rua)
        String neighborhoodCityStateCountry = formatAddress("", neighborhood, city, state, country);
        if (!neighborhoodCityStateCountry.isEmpty() && !neighborhoodCityStateCountry.equals(fullAddress)) {
             try {
                double[] coords = makeGeocodingRequest(neighborhoodCityStateCountry);
                if (coords != null) {
                    System.out.println("DEBUG: Geocodificação bem-sucedida (Bairro+Cidade): " + neighborhoodCityStateCountry);
                    return coords;
                }
            } catch (IOException e) {
                System.err.println("DEBUG: Erro ao geocodificar (Bairro+Cidade): " + neighborhoodCityStateCountry + " - " + e.getMessage());
            }
        }

        // 4. Tentar com Cidade, Estado, País (a mais genérica)
        String cityStateCountry = formatAddress("", "", city, state, country);
         if (!cityStateCountry.isEmpty() && !cityStateCountry.equals(fullAddress)) {
            try {
                double[] coords = makeGeocodingRequest(cityStateCountry);
                if (coords != null) {
                    System.out.println("DEBUG: Geocodificação bem-sucedida (Cidade+Estado): " + cityStateCountry);
                    return coords;
                }
            } catch (IOException e) {
                System.err.println("DEBUG: Erro ao geocodificar (Cidade+Estado): " + cityStateCountry + " - " + e.getMessage());
            }
        }

        System.out.println("DEBUG: Nenhuma geocodificação bem-sucedida para o endereço original: " + fullAddress);
        return null;
    }

    /**
     * Realiza a requisição HTTP à API Nominatim para obter coordenadas de um endereço.
     * @param query String do endereço completo para consulta.
     * @return Array de double [latitude, longitude] se encontrado, ou null caso contrário.
     * @throws IOException Se houver erro de comunicação com o serviço de geocodificação.
     */
    private double[] makeGeocodingRequest(String query) throws IOException {
        if (query == null || query.trim().isEmpty()) {
            return null;
        }

        HttpUrl.Builder urlBuilder = HttpUrl.parse(geocodingApiUrl).newBuilder();
        urlBuilder.addQueryParameter("q", query);
        urlBuilder.addQueryParameter("format", "json");
        urlBuilder.addQueryParameter("limit", "1");

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("User-Agent", "AlertaeApp/1.0 (julia.azevedolins@gmail.com)")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("DEBUG: Nominatim request failed with code " + response.code() + " for query: " + query + ". Message: " + response.message());
                return null;
            }

            String responseBody = response.body() != null ? response.body().string() : "";
            JsonArray jsonResponse = gson.fromJson(responseBody, JsonArray.class);

            if (jsonResponse != null && jsonResponse.size() > 0) {
                JsonObject firstResult = jsonResponse.get(0).getAsJsonObject();

                JsonElement latElement = firstResult.get("lat");
                JsonElement lonElement = firstResult.get("lon");

                if (latElement != null && lonElement != null && latElement.isJsonPrimitive() && lonElement.isJsonPrimitive()) {
                    double lat = latElement.getAsDouble();
                    double lon = lonElement.getAsDouble();
                    return new double[]{lat, lon};
                } else {
                    System.out.println("DEBUG: Nominatim response did not contain valid 'lat' or 'lon' fields for query: " + query + ". Full response: " + responseBody);
                    return null;
                }
            } else {
                System.out.println("DEBUG: No coordinates found for address query: " + query + ". Full response: " + responseBody);
                return null;
            }
        }
    }

    /**
     * Formata as partes do endereço em uma única string, separada por vírgulas,
     * ignorando partes nulas ou vazias.
     * @param street Rua.
     * @param neighborhood Bairro.
     * @param city Cidade.
     * @param state Estado.
     * @param country País.
     * @return Endereço formatado como string.
     */
    private String formatAddress(String street, String neighborhood, String city, String state, String country) {
        StringBuilder sb = new StringBuilder();
        if (!street.isEmpty()) sb.append(street);
        if (!neighborhood.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(neighborhood);
        }
        if (!city.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }
        if (!state.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(state);
        }
        if (!country.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(country);
        }
        return sb.toString();
    }

    /**
     * Remove o número da rua de uma string de endereço.
     * Exemplo: "Avenida Brasil, 123" -> "Avenida Brasil"
     * Exemplo: "Rua das Flores 45" -> "Rua das Flores"
     * @param street Rua (possivelmente com número).
     * @return Rua sem o número.
     */
    private String removeNumberFromStreet(String street) {
        if (street == null || street.trim().isEmpty()) {
            return "";
        }
        String cleaned = street.replaceAll("\\b\\d+\\b", "").trim();
        cleaned = cleaned.replaceAll(",\\s*$", "").trim();
        cleaned = cleaned.replaceAll("\\b(nº|n|num|número)\\s*\\d+\\b", "").trim();
        cleaned = cleaned.replaceAll("\\s+", " ").trim();
        return cleaned;
    }
}