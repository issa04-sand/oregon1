package view;

import controller.GameController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import service.GeminiService;

/**
 * VictoryView - Pantalla de Victoria
 * 
 * Se muestra cuando el jugador completa todos los escenarios
 * Incluye mensaje generado por Gemini API
 */
public class VictoryView {
    
    private Stage stage;
    private GameController controller;
    private VBox root;
    private Scene scene;
    private GeminiService geminiService;
    
    public VictoryView(Stage stage, GameController controller) {
        this.stage = stage;
        this.controller = controller;
        this.geminiService = new GeminiService("YOUR_GEMINI_API_KEY");
        createUI();
    }
    
    private void createUI() {
        root = new VBox(35);
        root.setAlignment(Pos.CENTER);
        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #004d00, #006600);"
        );
        
        // Título VICTORIA
        Text title = new Text("¡VICTORIA!");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 80));
        title.setFill(Color.web("#ffd700"));
        
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#ffff00"));
        glow.setRadius(30);
        glow.setSpread(0.7);
        title.setEffect(glow);
        
        // Subtítulo
        Text subtitle = new Text("Has llegado al Valle de Willamette");
        subtitle.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        subtitle.setFill(Color.web("#90ee90"));
        
        // Estadísticas finales
        Text stats = new Text(String.format(
            "Escenarios completados: 3/3\n" +
            "Vida final: %d/3\n" +
            "Recursos restantes: %d",
            controller.getPlayer().getVida(),
            controller.getInventory().size()
        ));
        stats.setFont(Font.font("Arial", 20));
        stats.setFill(Color.WHITE);
        stats.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        // Mensaje generado por Gemini
        Text geminiMessage = new Text("Generando mensaje épico...");
        geminiMessage.setFont(Font.font("Arial", FontWeight.ITALIC, 22));
        geminiMessage.setFill(Color.web("#ffd700"));
        geminiMessage.setWrappingWidth(900);
        geminiMessage.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        // Generar mensaje con Gemini (hilo separado)
        new Thread(() -> {
            String message = geminiService.generateVictoryMessage(
                controller.getPlayer().getVida(),
                3
            );
            
            javafx.application.Platform.runLater(() -> {
                geminiMessage.setText("\"" + message + "\"");
            });
        }).start();
        
        // Botones
        Button btnAchievements = createButton("VER LOGROS DESBLOQUEADOS");
        Button btnMenu = createButton("MENÚ PRINCIPAL");
        
        btnAchievements.setOnAction(e -> showAchievements());
        btnMenu.setOnAction(e -> returnToMenu());
        
        // Layout
        VBox titleBox = new VBox(10, title, subtitle);
        titleBox.setAlignment(Pos.CENTER);
        
        VBox buttonBox = new VBox(15, btnAchievements, btnMenu);
        buttonBox.setAlignment(Pos.CENTER);
        
        root.getChildren().addAll(titleBox, stats, geminiMessage, buttonBox);
        
        scene = new Scene(root, 1280, 720);
    }
    
    private Button createButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(350);
        button.setPrefHeight(50);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        button.setStyle(
            "-fx-background-color: #228b22;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #90ee90;" +
            "-fx-border-width: 3;" +
            "-fx-border-radius: 10;" +
            "-fx-cursor: hand;"
        );
        
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: #32cd32;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #98fb98;" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 10;" +
                "-fx-cursor: hand;"
            );
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: #228b22;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #90ee90;" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 10;" +
                "-fx-cursor: hand;"
            );
        });
        
        return button;
    }
    
    private void showAchievements() {
        AchievementsView achievementsView = new AchievementsView(stage, null);
        achievementsView.show();
    }
    
    private void returnToMenu() {
        MainMenuView mainMenu = new MainMenuView(stage);
        mainMenu.show();
    }
    
    public void show() {
        stage.setScene(scene);
    }
}
