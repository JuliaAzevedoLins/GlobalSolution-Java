# Alertae API

Bem-vindo à documentação da API Alertae! Esta API foi desenvolvida como parte de um projeto para gerenciar alertas, utilizando geocodificação para associar alertas a localizações geográficas específicas.

## Membros da Equipe

- Julia Azevedo Lins - RM98690  
- Luís Gustavo Barreto Garrido - RM99210  
- Victor Hugo Aranda Forte - RM99667  

## Tecnologias e Configurações

Esta API é construída com as seguintes tecnologias:

- **Linguagem de Programação:** Java 17+
- **Framework:** Spring Boot 3.x
- **Servidor Web Embutido:** Apache Tomcat
- **Cliente HTTP:** OkHttp
- **Manipulação JSON:** Google Gson
- **Banco de Dados:** Supabase (PostgreSQL)
- **Geocodificação:** Nominatim (OpenStreetMap)
- **Documentação da API:** Swagger UI (OpenAPI 3)
- **Ferramenta de Build:** Maven

---

## Como Começar

### Pré-requisitos

- Java Development Kit (JDK) 17 ou superior  
- Maven 3.x  
- Acesso à internet para consumir as APIs externas (Nominatim e Supabase)  
- Instância do Supabase para persistência dos dados  

### Configuração da Aplicação

No arquivo `src/main/resources/application.properties`:

```properties
# Supabase Configuration
supabase.url=SUA_URL_DO_SUPABASE
supabase.anon-key=SUA_CHAVE_ANONIMA_DO_SUPABASE

# Geocoding API Configuration (Nominatim)
geocoding.api.url=https://nominatim.openstreetmap.org/search
```

### Executando a Aplicação

```bash
mvn clean install
mvn spring-boot:run
```

A aplicação será iniciada na porta padrão `8080`.

---

## Estrutura do Projeto

```
.
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── alertae
│   │   │           ├── AlertaeApiApplication.java
│   │   │           ├── config
│   │   │           │   ├── SupabaseConfig.java
│   │   │           │   └── SwaggerConfig.java
│   │   │           ├── controller
│   │   │           │   └── AlertController.java
│   │   │           ├── dto
│   │   │           │   └── AddressRequest.java
│   │   │           ├── model
│   │   │           │   └── Alert.java
│   │   │           ├── repository
│   │   │           │   └── SupabaseAlertRepository.java
│   │   │           └── service
│   │   │               ├── AlertService.java
│   │   │               └── GeocodingService.java
│   └── resources
│       ├── application.properties
│       └── static
└── test
    └── java
        └── com
            └── alertae
                └── api
```

---

## Detalhes das Classes Principais

- `AlertaeApiApplication.java`: Classe principal da aplicação Spring Boot.
- `config/SupabaseConfig.java`: Configuração de integração com Supabase e instância de OkHttp/Gson.
- `config/SwaggerConfig.java`: Configuração do Swagger/OpenAPI.
- `controller/AlertController.java`: Controlador REST para alertas.
- `dto/AddressRequest.java`: DTO para entrada de dados de endereço.
- `model/Alert.java`: Modelo de dados com título, mensagem, email, localização e data.
- `repository/SupabaseAlertRepository.java`: Camada de persistência com Supabase.
- `service/AlertService.java`: Lógica principal, chama geocodificação e salva alerta.
- `service/GeocodingService.java`: Obtém coordenadas via Nominatim.

---

## Endpoints da API

### Documentação Interativa

Disponível em: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

### POST `/api/v1/alerts` - Criar Alerta

**Exemplo de Requisição:**

```json
{
  "title": "Alerta de Enchente",
  "message": "Nível do rio subindo rapidamente na área central.",
  "emailNotification": "usuario@example.com",
  "street": "Rua das Flores, 123",
  "neighborhood": "Jardim América",
  "city": "São Paulo",
  "state": "SP",
  "country": "Brasil"
}
```

**Respostas:**

- `201 Created`: Alerta criado com sucesso.
- `400 Bad Request`: Endereço inválido ou não encontrado.
- `500 Internal Server Error`: Erro de comunicação com serviços externos.

---

### GET `/api/v1/alerts` - Listar Alertas

**Respostas:**

- `200 OK`: Lista de alertas.
- `500 Internal Server Error`: Erro ao acessar Supabase.

---

### GET `/api/v1/alerts/{id}` - Buscar por ID

**Respostas:**

- `200 OK`: Alerta encontrado.
- `404 Not Found`: Alerta não encontrado.
- `500 Internal Server Error`: Erro interno.

---

### PUT `/api/v1/alerts/{id}` - Atualizar Alerta

**Exemplo de Requisição:**

```json
{
  "id": "id-do-alerta-existente",
  "title": "Alerta de Enchente (Atualizado)",
  "message": "Nível do rio estabilizado na área central.",
  "emailNotification": "novo.email@example.com",
  "lat": -23.5505,
  "longitude": -46.6333,
  "createdAt": "2024-06-06T10:00:00Z"
}
```

**Respostas:**

- `200 OK`: Atualizado com sucesso.
- `404 Not Found`: Alerta não encontrado.
- `500 Internal Server Error`: Falha na atualização.

---

### DELETE `/api/v1/alerts/{id}` - Excluir Alerta

**Respostas:**

- `204 No Content`: Alerta excluído.
- `500 Internal Server Error`: Falha na exclusão.

---

## Licença

Este projeto está sob a licença MIT.