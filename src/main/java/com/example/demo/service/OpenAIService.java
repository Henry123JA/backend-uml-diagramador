package com.example.demo.service;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class OpenAIService {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-4.1-mini"; // o gpt-4o-mini
    private final String apiKey = "#######----colocar api key de open ia aca-----###############";

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    // 1. Generar entidades Java
    public String generateEntitiesFromJson(String umlJson, String basePackage) throws Exception {
        String systemPrompt =
                "Eres un generador de código Java con Spring Boot JPA. " +
                        "Convierte un JSON exportado de un diagrama UML en entidades Java (@Entity). " +
                        "Usa jakarta.persistence. " +
                        "Agrega siempre un campo `private Long id` con `@Id` y `@GeneratedValue(strategy = GenerationType.IDENTITY)`. " +
                        "Genera siempre también el getter y el setter explícito para `id`, incluso si usas Lombok. " +
                        "Usa lombok.Data para los demás campos. " +
                        "Reconoce relaciones de herencia, composición, agregación y asociación. " +
                        "Siempre que uses mappedBy en una entidad, asegúrate que la otra entidad tenga el atributo correspondiente con la anotación adecuada. " +
                        "Usa nombres de columnas simples y claros (ej: producto_id, categoria_id). " +
                        "Devuelve cada clase en un bloque separado con ```java y el nombre correcto del archivo. " +
                        "Incluye siempre la línea `package " + basePackage + ";` al inicio de cada clase.";
        return callOpenAi(systemPrompt, umlJson);
    }

    // 2. Generar script SQL (DDL)
    public String generateSqlSchemaFromJson(String umlJson) throws Exception {
        String systemPrompt =
                "Eres un generador de scripts SQL para PostgreSQL. " +
                        "Convierte un JSON UML en sentencias CREATE TABLE. " +
                        "Usa tipos estándar de PostgreSQL (BIGINT, VARCHAR, NUMERIC, TIMESTAMP, BOOLEAN, etc.). " +
                        "Incluye claves primarias y foráneas si corresponden. " +
                        "Devuelve solo SQL dentro de ```sql.";
        return callOpenAi(systemPrompt, umlJson);
    }

    // 3. Generar script SQL con datos de ejemplo
    public String generateSqlInsertsFromJson(String umlJson, int rows) throws Exception {
        String systemPrompt =
                "Eres un generador de datos de prueba para PostgreSQL. " +
                        "Convierte un JSON UML en sentencias INSERT INTO. " +
                        "Genera " + rows + " filas de datos realistas por cada tabla. " +
                        "Respeta los tipos de datos (ej: fechas, enteros, strings). " +
                        "Devuelve solo SQL dentro de ```sql.";
        return callOpenAi(systemPrompt, umlJson);
    }

    // 4. Generar XML compatible con draw.io
    public String generateDrawioXmlFromJson(String umlJson) throws Exception {
        String systemPrompt =
                "Eres un generador de diagramas draw.io. " +
                        "Convierte un JSON UML en un archivo XML válido para draw.io (.drawio). " +
                        "El XML debe comenzar con '<?xml version=\"1.0\" encoding=\"UTF-8\"?>'. " +
                        "Usa <mxfile host=\"app.diagrams.net\" modified=\"2025-09-30T12:00:00Z\" agent=\"draw.io\"> como raíz. " +
                        "Cada <mxCell> debe tener ids numéricos consecutivos (0,1,2,3...). " +
                        "Las entidades (clases) deben representarse como vertex=\"1\" y las relaciones como edge=\"1\". " +
                        "No uses ids de texto como 'producto_attrs'. " +
                        "No uses compresión ni base64, genera XML plano expandido. " +
                        "Devuelve únicamente el contenido XML, sin bloques de código ni backticks.";

        return callOpenAi(systemPrompt, umlJson);
    }

    // 🔹 Método genérico para llamar a la API
    private String callOpenAi(String systemPrompt, String userContent) throws Exception {
        JSONObject body = new JSONObject()
                .put("model", MODEL)
                .put("messages", new JSONArray()
                        .put(new JSONObject().put("role", "system").put("content", systemPrompt))
                        .put(new JSONObject().put("role", "user").put("content", userContent))
                );

        Request request = new Request.Builder()
                .url(API_URL)
                .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Error API: " + response);
            }
            JSONObject jsonResponse = new JSONObject(response.body().string());
            return jsonResponse
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
        }
    }
}
