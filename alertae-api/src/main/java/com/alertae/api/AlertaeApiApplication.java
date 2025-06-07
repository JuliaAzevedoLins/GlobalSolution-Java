package com.alertae.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal para inicialização da aplicação Spring Boot Alertae API.
 */
@SpringBootApplication
public class AlertaeApiApplication {

    /**
     * Método principal que inicia a aplicação Alertae API.
     * @param args argumentos de linha de comando
     */
    public static void main(String[] args) {
        SpringApplication.run(AlertaeApiApplication.class, args);
    }

}