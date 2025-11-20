package view;

import controller.GameController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * MainMenuView - Vista del menú principal
 * 
 * Pantalla inicial del juego con opciones:
 * - Nuevo Juego
 * - Ver Logros
 * - Manual de Usuario
 * - Salir
 */
public class MainMenuView {
    
    private Stage stage;
    private VBox root;
    private Scene scene;
    
    public MainMenuView(Stage stage) {
        this.stage = stage;
        createUI();
    }
    
    private void createUI() {
        root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e);"
        );
        
        // Título del juego
        Text title = new Text("OREGON TRAIL");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 72));
        title.setFill(Color.web("#f4a460"));
        title.setEffect(createGlowEffect());
        
        Text subtitle = new Text("SURVIVAL - 1848");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 32));
        subtitle.setFill(Color.web("#d2691e"));
        
        // Botones del menú
        Button btnNewGame = createMenuButton("NUEVO JUEGO");
        Button btnAchievements = createMenuButton("VER LOGROS");
        Button btnManual = createMenuButton("MANUAL DE USUARIO");
        Button btnExit = createMenuButton("SALIR");
        
        // Eventos de los botones
        btnNewGame.setOnAction(e -> startNewGame());
        btnAchievements.setOnAction(e -> showAchievements());
        btnManual.setOnAction(e -> showManual());
        btnExit.setOnAction(e -> stage.close());
        
        // Información adicional
        Text info = new Text("Un juego de supervivencia y gestión de recursos");
        info.setFont(Font.font("Arial", 16));
        info.setFill(Color.LIGHTGRAY);
        
        // Agregar elementos al layout
        VBox titleBox = new VBox(10, title, subtitle);
        titleBox.setAlignment(Pos.CENTER);
        
        VBox buttonBox = new VBox(15, btnNewGame, btnAchievements, btnManual, btnExit);
        buttonBox.setAlignment(Pos.CENTER);
        
        root.getChildren().addAll(titleBox, buttonBox, info);
        
        scene = new Scene(root, 1280, 720);
    }
    
    /**
     * Crea un botón con estilo personalizado para el menú
     */
    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(300);
        button.setPrefHeight(50);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        button.setStyle(
            "-fx-background-color: #8b4513;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #d2691e;" +
            "-fx-border-width: 3;" +
            "-fx-border-radius: 10;" +
            "-fx-cursor: hand;"
        );
        
        // Efectos hover
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: #a0522d;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #f4a460;" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-scale-x: 1.05;" +
                "-fx-scale-y: 1.05;"
            );
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: #8b4513;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #d2691e;" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 10;" +
                "-fx-cursor: hand;"
            );
        });
        
        return button;
    }
    
    /**
     * Crea efecto de brillo para el título
     */
    private DropShadow createGlowEffect() {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#ff8c00"));
        glow.setRadius(20);
        glow.setSpread(0.5);
        return glow;
    }
    
    /**
     * Inicia un nuevo juego
     */
    private void startNewGame() {
        GameController controller = new GameController();
        GameView gameView = new GameView(stage, controller);
        gameView.show();
        controller.startGameLoop();
    }
    
    /**
     * Muestra la ventana de logros
     */
    private void showAchievements() {
        AchievementsView achievementsView = new AchievementsView(stage, this);
        achievementsView.show();
    }
    
    /**
     * Muestra el manual de usuario
     */
    private void showManual() {
        ManualView manualView = new ManualView(stage, this);
        manualView.show();
    }
    
    /**
     * Muestra la vista en el stage
     */
    public void show() {
        stage.setScene(scene);
    }
}
