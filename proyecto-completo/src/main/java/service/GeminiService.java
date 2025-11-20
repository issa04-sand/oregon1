package service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * GeminiService - Servicio para integración con Gemini API
 * 
 * Genera diálogos contextuales para:
 * - Enemigos al ser eliminados
 * - NPCs pasivos (comerciantes, viajeros)
 * - Mensajes de eventos del juego
 * 
 * Uso de IA Generativa: Nivel 4 - IAG completa
 * Los diálogos son generados dinámicamente según el contexto del juego
 */
public class GeminiService {
    
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";
    private final String apiKey;
    private final HttpClient httpClient;
    private final Gson gson;
    
    /**
     * Constructor
     * @param apiKey Clave API de Gemini (obtener en https://makersuite.google.com/app/apikey)
     */
    public GeminiService(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
    }
    
    /**
     * Genera un diálogo para un enemigo eliminado
     * 
     * @param enemyType Tipo de enemigo eliminado
     * @param playerWeapon Arma usada por el jugador
     * @param scenarioName Nombre del escenario actual
     * @return Diálogo generado o mensaje por defecto
     */
    public String generateEnemyDeathDialog(String enemyType, String playerWeapon, String scenarioName) {
        String prompt = String.format(
            "Eres un narrador del juego Oregon Trail. " +
            "Un %s fue eliminado por el jugador usando un %s en %s. " +
            "Genera UNA SOLA FRASE corta (máximo 15 palabras) que el enemigo diría antes de caer, " +
            "contextual al periodo histórico de 1848. " +
            "Debe sonar auténtico y dramático. " +
            "Solo la frase, sin comillas ni explicaciones.",
            enemyType, playerWeapon, scenarioName
        );
        
        return generateText(prompt, "¡Argh! ¡Me has derrotado!");
    }
    
    /**
     * Genera un diálogo para un comerciante NPC
     * 
     * @param playerHealth Salud actual del jugador
     * @param hasFood Si el jugador tiene comida
     * @param hasMedicine Si el jugador tiene medicina
     * @return Diálogo del comerciante
     */
    public String generateMerchantDialog(int playerHealth, boolean hasFood, boolean hasMedicine) {
        String prompt = String.format(
            "Eres un comerciante en el camino de Oregon en 1848. " +
            "El jugador tiene %d de vida, %s comida y %s medicina. " +
            "Genera UNA SOLA FRASE de bienvenida (máximo 20 palabras) ofreciendo tus servicios. " +
            "Sé amigable pero realista con el contexto histórico. " +
            "Solo la frase, sin comillas.",
            playerHealth,
            hasFood ? "tiene" : "no tiene",
            hasMedicine ? "tiene" : "no tiene"
        );
        
        return generateText(prompt, "¡Bienvenido viajero! ¿Necesitas suministros para tu travesía?");
    }
    
    /**
     * Genera un mensaje de evento aleatorio
     * 
     * @param eventType Tipo de evento (tormenta, encuentro, descanso)
     * @param scenarioName Escenario actual
     * @return Descripción del evento
     */
    public String generateEventMessage(String eventType, String scenarioName) {
        String prompt = String.format(
            "Eres narrador de Oregon Trail 1848. " +
            "Describe brevemente (máximo 25 palabras) un evento de tipo '%s' en %s. " +
            "Hazlo inmersivo y contextual al periodo. " +
            "Solo la descripción, sin formato.",
            eventType, scenarioName
        );
        
        return generateText(prompt, "Un evento inesperado ocurre en tu viaje...");
    }
    
    /**
     * Genera un consejo de supervivencia
     * 
     * @param currentHealth Salud actual
     * @param ammoCount Municiones disponibles
     * @param enemyCount Número de enemigos cercanos
     * @return Consejo generado
     */
    public String generateSurvivalTip(int currentHealth, int ammoCount, int enemyCount) {
        String prompt = String.format(
            "Eres un guía experto del Oregon Trail. " +
            "El jugador tiene %d de vida, %d municiones y hay %d enemigos cerca. " +
            "Da UN consejo táctico corto (máximo 20 palabras) en español. " +
            "Solo el consejo directo.",
            currentHealth, ammoCount, enemyCount
        );
        
        return generateText(prompt, "Conserva tus recursos y planifica tus movimientos cuidadosamente.");
    }
    
    /**
     * Método genérico para generar texto usando Gemini API
     * 
     * @param prompt Prompt para enviar a Gemini
     * @param defaultMessage Mensaje por defecto si falla la API
     * @return Texto generado
     */
    private String generateText(String prompt, String defaultMessage) {
        try {
            // Construir el cuerpo de la solicitud JSON
            JsonObject requestBody = new JsonObject();
            JsonArray contents = new JsonArray();
            JsonObject content = new JsonObject();
            JsonArray parts = new JsonArray();
            JsonObject part = new JsonObject();
            
            part.addProperty("text", prompt);
            parts.add(part);
            content.add("parts", parts);
            contents.add(content);
            requestBody.add("contents", contents);
            
            // Configuración de generación
            JsonObject generationConfig = new JsonObject();
            generationConfig.addProperty("temperature", 0.9);
            generationConfig.addProperty("topK", 1);
            generationConfig.addProperty("topP", 1);
            generationConfig.addProperty("maxOutputTokens", 100);
            requestBody.add("generationConfig", generationConfig);
            
            // Configuración de seguridad
            JsonArray safetySettings = new JsonArray();
            requestBody.add("safetySettings", safetySettings);
            
            String jsonBody = gson.toJson(requestBody);
            
            // Crear la solicitud HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "?key=" + apiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .timeout(Duration.ofSeconds(30))
                    .build();
            
            // Enviar la solicitud
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                // Parsear la respuesta
                JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
                
                if (jsonResponse.has("candidates")) {
                    JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
                    if (candidates.size() > 0) {
                        JsonObject candidate = candidates.get(0).getAsJsonObject();
                        JsonObject contentObj = candidate.getAsJsonObject("content");
                        JsonArray partsArray = contentObj.getAsJsonArray("parts");
                        if (partsArray.size() > 0) {
                            JsonObject partObj = partsArray.get(0).getAsJsonObject();
                            String generatedText = partObj.get("text").getAsString();
                            return generatedText.trim();
                        }
                    }
                }
            } else {
                System.err.println("Error en API de Gemini: " + response.statusCode());
                System.err.println("Respuesta: " + response.body());
            }
            
        } catch (Exception e) {
            System.err.println("Error al llamar a Gemini API: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Retornar mensaje por defecto si hay algún error
        return defaultMessage;
    }
    
    /**
     * Genera diálogo para victoria
     */
    public String generateVictoryMessage(int finalHealth, int scenariosCompleted) {
        String prompt = String.format(
            "Eres narrador de Oregon Trail. " +
            "El jugador completó los %d escenarios con %d de vida. " +
            "Genera un mensaje épico de victoria (máximo 30 palabras) " +
            "celebrando su llegada al Valle de Willamette. " +
            "Solo el mensaje.",
            scenariosCompleted, finalHealth
        );
        
        return generateText(prompt, 
            "¡Lo lograste! Has completado el peligroso viaje hasta Oregón. " +
            "Tu valentía y determinación te han traído hasta aquí.");
    }
    
    /**
     * Genera diálogo para game over
     */
    public String generateGameOverMessage(String causeOfDeath, String scenarioName) {
        String prompt = String.format(
            "Eres narrador de Oregon Trail. " +
            "El jugador murió por %s en %s. " +
            "Genera un mensaje sombrío pero respetuoso (máximo 25 palabras) " +
            "recordando su viaje. Solo el mensaje.",
            causeOfDeath, scenarioName
        );
        
        return generateText(prompt, 
            "Tu viaje ha llegado a su fin en estas tierras inhóspitas. " +
            "Que tu valentía sea recordada.");
    }
}
