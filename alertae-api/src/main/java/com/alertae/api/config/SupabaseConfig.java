package com.alertae.api.config;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração dos beans e propriedades para integração com Supabase.
 */
@Configuration
public class SupabaseConfig {

    /**
     * URL do Supabase, definida no application.properties.
     */
    @Value("${supabase.url}")
    private String supabaseUrl;

    /**
     * Chave anônima do Supabase, definida no application.properties.
     */
    @Value("${supabase.anon-key}")
    private String supabaseAnonKey;

    /**
     * Cria e fornece um bean OkHttpClient para requisições HTTP.
     * @return instância configurada de OkHttpClient.
     */
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder().build();
    }

    /**
     * Cria e fornece um bean Gson para serialização e desserialização JSON.
     * @return instância de Gson.
     */
    @Bean
    public Gson gson() {
        return new Gson();
    }

    /**
     * Retorna a URL do Supabase.
     * @return URL do Supabase.
     */
    public String getSupabaseUrl() {
        return supabaseUrl;
    }

    /**
     * Retorna a chave anônima do Supabase.
     * @return chave anônima do Supabase.
     */
    public String getSupabaseAnonKey() {
        return supabaseAnonKey;
    }
}