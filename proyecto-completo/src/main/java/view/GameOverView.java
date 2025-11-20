package view;

import controller.GameController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import service.GeminiService;

/**
 * GameOverView - Pantalla de Game Over
 * 
 * Se muestra cuando el jugador pierde todas sus vidas
 * Incluye mensaje generado por Gemini API
 */
public class GameOverView {
    
    private Stage stage;
    private GameController controller;
    private VBox root;
    private Scene scene;
    private GeminiService geminiService;
    
    public GameOverView(Stage stage, GameController controller) {
        this.stage = stage;
        this.controller = controller;
        this.geminiService = new GeminiService("YOUR_GEMINI_API_KEY");
        createUI();
    }
    
    private void createUI() {
        root = new VBox(40);
        root.setAlignment(Pos.CENTER);
        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #1a0000, #330000);"
        );
        
        // Título GAME OVER
        Text title = new Text("GAME OVER");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 80));
        title.setFill(Color.web("#8b0000"));
        title.setEffect(new GaussianBlur(10));
        
        // Subtítulo
        Text subtitle = new Text("Tu viaje ha terminado");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 28));
        subtitle.setFill(Color.web("#ff6b6b"));
        
        // Estadísticas
        Text stats = new Text(String.format(
            "Escenario alcanzado: %s\n" +
            "Enemigos eliminados: --\n" +
            "Días sobrevividos: --",
            controller.getCurrentScenarioName()
        ));
        stats.setFont(Font.font("Arial", 18));
        stats.setFill(Color.LIGHTGRAY);
        stats.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        // Mensaje generado por Gemini (en hilo separado)
        Text geminiMessage = new Text("Cargando mensaje...");
        geminiMessage.setFont(Font.font("Arial", FontWeight.ITALIC, 20));
        geminiMessage.setFill(Color.web("#ffd700"));
        geminiMessage.setWrappingWidth(800);
        geminiMessage.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        // Generar mensaje con Gemini
        new Thread(() -> {
            String message = geminiService.generateGameOverMessage(
                "combate",
                controller.getCurrentScenarioName()
            );
            
            javafx.application.Platform.runLater(() -> {
                geminiMessage.setText("\"" + message + "\"");
            });
        }).start();
        
        // Botones
        Button btnRetry = createButton("INTENTAR DE NUEVO");
        Button btnMenu = createButton("MENÚ PRINCIPAL");
        
        btnRetry.setOnAction(e -> retry());
        btnMenu.setOnAction(e -> returnToMenu());
        
        // Layout
        VBox titleBox = new VBox(15, title, subtitle);
        titleBox.setAlignment(Pos.CENTER);
        
        VBox buttonBox = new VBox(15, btnRetry, btnMenu);
        buttonBox.setAlignment(Pos.CENTER);
        
        root.getChildren().addAll(titleBox, stats, geminiMessage, buttonBox);
        
        scene = new Scene(root, 1280, 720);
    }
    
    private Button createButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(300);
        button.setPrefHeight(50);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        button.setStyle(
            "-fx-background-color: #8b0000;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #ff6b6b;" +
            "-fx-border-width: 3;" +
            "-fx-border-radius: 10;" +
            "-fx-cursor: hand;"
        );
        
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: #a00000;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #ff8c8c;" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 10;" +
                "-fx-cursor: hand;"
            );
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: #8b0000;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #ff6b6b;" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 10;" +
                "-fx-cursor: hand;"
            );
        });
        
        return button;
    }
    
    private void retry() {
        GameController newController = new GameController();
        GameView gameView = new GameView(stage, newController);
        gameView.show();
        newController.startGameLoop();
    }
    
    private void returnToMenu() {
        MainMenuView mainMenu = new MainMenuView(stage);
        mainMenu.show();
    }
    
    public void show() {
        stage.setScene(scene);
    }
}
