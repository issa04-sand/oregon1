package view;

import controller.GameController;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.*;
import service.GeminiService;

/**
 * GameView - Vista principal del juego
 * 
 * Responsabilidades:
 * - Renderizar el juego (jugador, enemigos, escenario)
 * - Mostrar HUD con indicadores (vida, municiones, inventario)
 * - Capturar input del jugador
 * - Actualizar animaciones con hilos
 */
public class GameView {
    
    private Stage stage;
    private GameController controller;
    private BorderPane root;
    private Canvas gameCanvas;
    private GraphicsContext gc;
    private Scene scene;
    
    // HUD Elements
    private Label lblHealth;
    private Label lblAmmo;
    private Label lblWeapon;
    private Label lblScenario;
    private Label lblInventory;
    private VBox dialogBox;
    private Label lblDialog;
    
    // Animación
    private AnimationTimer renderLoop;
    private double playerSize = 30;
    private double enemySize = 25;
    
    // Mouse tracking para retícula
    private double mouseX;
    private double mouseY;
    
    // Servicio de diálogos
    private GeminiService geminiService;
    
    // Escala para conversión de coordenadas
    private static final double SCALE = 20.0; // 20 píxeles por unidad del modelo
    
    public GameView(Stage stage, GameController controller) {
        this.stage = stage;
        this.controller = controller;
        
        // Inicializar Gemini Service
        // NOTA: Reemplazar con tu API Key real
        this.geminiService = new GeminiService("YOUR_GEMINI_API_KEY");
        
        createUI();
        setupInputHandlers();
        startRenderLoop();
    }
    
    /**
     * Crea la interfaz de usuario
     */
    private void createUI() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #2c3e50;");
        
        // Canvas para el juego
        gameCanvas = new Canvas(900, 600);
        gc = gameCanvas.getGraphicsContext2D();
        
        // Panel del juego con borde
        StackPane gamePane = new StackPane(gameCanvas);
        gamePane.setStyle(
            "-fx-background-color: #34495e;" +
            "-fx-border-color: #d2691e;" +
            "-fx-border-width: 3;"
        );
        gamePane.setAlignment(Pos.CENTER);
        
        root.setCenter(gamePane);
        
        // HUD Superior
        HBox topHUD = createTopHUD();
        root.setTop(topHUD);
        
        // HUD Derecho (inventario)
        VBox rightHUD = createRightHUD();
        root.setRight(rightHUD);
        
        // Panel de diálogos (inferior)
        dialogBox = createDialogBox();
        root.setBottom(dialogBox);
        
        scene = new Scene(root, 1280, 720);
    }
    
    /**
     * Crea el HUD superior con indicadores
     */
    private HBox createTopHUD() {
        HBox hud = new HBox(30);
        hud.setPadding(new Insets(15));
        hud.setStyle("-fx-background-color: #1a1a2e;");
        hud.setAlignment(Pos.CENTER_LEFT);
        
        // Vida
        lblHealth = createHUDLabel("♥ Vida: 3/3");
        lblHealth.setTextFill(Color.web("#e74c3c"));
        
        // Arma actual
        lblWeapon = createHUDLabel("⚔ Arma: RIFLE");
        lblWeapon.setTextFill(Color.web("#f39c12"));
        
        // Municiones
        lblAmmo = createHUDLabel("➤ Municiones: 20");
        lblAmmo.setTextFill(Color.web("#3498db"));
        
        // Escenario
        lblScenario = createHUDLabel("📍 Llanuras y Praderas");
        lblScenario.setTextFill(Color.web("#2ecc71"));
        
        hud.getChildren().addAll(lblHealth, lblWeapon, lblAmmo, lblScenario);
        
        return hud;
    }
    
    /**
     * Crea el HUD derecho con el inventario
     */
    private VBox createRightHUD() {
        VBox inventoryPanel = new VBox(10);
        inventoryPanel.setPadding(new Insets(15));
        inventoryPanel.setPrefWidth(250);
        inventoryPanel.setStyle(
            "-fx-background-color: #1a1a2e;" +
            "-fx-border-color: #d2691e;" +
            "-fx-border-width: 0 0 0 3;"
        );
        
        Label title = new Label("INVENTARIO");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#f4a460"));
        
        lblInventory = new Label();
        lblInventory.setFont(Font.font("Arial", 14));
        lblInventory.setTextFill(Color.WHITE);
        lblInventory.setWrapText(true);
        
        inventoryPanel.getChildren().addAll(title, lblInventory);
        
        return inventoryPanel;
    }
    
    /**
     * Crea el cuadro de diálogos
     */
    private VBox createDialogBox() {
        VBox box = new VBox(5);
        box.setPadding(new Insets(10));
        box.setPrefHeight(80);
        box.setStyle(
            "-fx-background-color: rgba(26, 26, 46, 0.9);" +
            "-fx-border-color: #d2691e;" +
            "-fx-border-width: 3 0 0 0;"
        );
        
        lblDialog = new Label("");
        lblDialog.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        lblDialog.setTextFill(Color.web("#ecf0f1"));
        lblDialog.setWrapText(true);
        lblDialog.setMaxWidth(1200);
        
        box.getChildren().add(lblDialog);
        box.setAlignment(Pos.CENTER);
        
        return box;
    }
    
    /**
     * Crea un label para el HUD
     */
    private Label createHUDLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        label.setTextFill(Color.WHITE);
        return label;
    }
    
    /**
     * Configura los manejadores de input
     */
    private void setupInputHandlers() {
        // Teclado
        scene.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            
            // Movimiento
            if (code == KeyCode.W || code == KeyCode.UP) {
                controller.movePlayer("up");
            } else if (code == KeyCode.S || code == KeyCode.DOWN) {
                controller.movePlayer("down");
            } else if (code == KeyCode.A || code == KeyCode.LEFT) {
                controller.movePlayer("left");
            } else if (code == KeyCode.D || code == KeyCode.RIGHT) {
                controller.movePlayer("right");
            }
            // Cambiar arma
            else if (code == KeyCode.Q) {
                controller.switchWeapon();
            }
            // Usar item (medicina)
            else if (code == KeyCode.E) {
                useFirstMedkit();
            }
            // Pausar / Menú
            else if (code == KeyCode.ESCAPE) {
                pauseGame();
            }
        });
        
        // Mouse - tracking para retícula
        gameCanvas.setOnMouseMoved(e -> {
            mouseX = e.getX();
            mouseY = e.getY();
        });
        
        // Mouse - disparo
        gameCanvas.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                shoot(e.getX(), e.getY());
            }
        });
    }
    
    /**
     * Inicia el loop de renderizado
     * Usa AnimationTimer para animaciones fluidas (concurrencia)
     */
    private void startRenderLoop() {
        renderLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                render();
                updateHUD();
                checkGameStatus();
            }
        };
        renderLoop.start();
    }
    
    /**
     * Renderiza el juego completo
     */
    private void render() {
        // Limpiar canvas
        gc.setFill(Color.web("#228b22")); // Verde pasto
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        
        // Renderizar escenario actual
        renderScenario();
        
        // Renderizar enemigos
        renderEnemies();
        
        // Renderizar jugador
        renderPlayer();
        
        // Renderizar retícula
        renderReticle();
    }
    
    /**
     * Renderiza el escenario con detalles visuales
     */
    private void renderScenario() {
        Scenario scenario = controller.getCurrentScenario();
        
        // Color de fondo según el escenario
        Color bgColor;
        switch (scenario.getType()) {
            case START:
                bgColor = Color.web("#90EE90"); // Verde claro - llanuras
                break;
            case ROAD:
                bgColor = Color.web("#8B7355"); // Marrón - montañas
                break;
            case RIVER:
                bgColor = Color.web("#87CEEB"); // Azul claro - río
                break;
            default:
                bgColor = Color.LIGHTGRAY;
        }
        
        gc.setFill(bgColor);
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        
        // Dibujar grid sutil
        gc.setStroke(Color.rgb(0, 0, 0, 0.1));
        gc.setLineWidth(1);
        for (int i = 0; i < gameCanvas.getWidth(); i += SCALE) {
            gc.strokeLine(i, 0, i, gameCanvas.getHeight());
        }
        for (int i = 0; i < gameCanvas.getHeight(); i += SCALE) {
            gc.strokeLine(0, i, gameCanvas.getWidth(), i);
        }
    }
    
    /**
     * Renderiza al jugador
     */
    private void renderPlayer() {
        Player player = controller.getPlayer();
        double x = player.getX() * SCALE;
        double y = player.getY() * SCALE;
        
        // Sombra
        gc.setFill(Color.rgb(0, 0, 0, 0.3));
        gc.fillOval(x - playerSize/2 + 2, y - playerSize/2 + 2, playerSize, playerSize);
        
        // Jugador (círculo azul)
        gc.setFill(Color.web("#3498db"));
        gc.fillOval(x - playerSize/2, y - playerSize/2, playerSize, playerSize);
        
        // Borde
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeOval(x - playerSize/2, y - playerSize/2, playerSize, playerSize);
    }
    
    /**
     * Renderiza todos los enemigos
     */
    private void renderEnemies() {
        ListEnemy enemies = controller.getEnemies();
        NodeEnemy current = enemies.getFirst();
        
        while (current != null) {
            Enemy enemy = current.getData();
            double x = enemy.getX() * SCALE;
            double y = enemy.getY() * SCALE;
            
            // Sombra
            gc.setFill(Color.rgb(0, 0, 0, 0.3));
            gc.fillOval(x - enemySize/2 + 2, y - enemySize/2 + 2, enemySize, enemySize);
            
            // Enemigo (círculo rojo)
            gc.setFill(Color.web("#e74c3c"));
            gc.fillOval(x - enemySize/2, y - enemySize/2, enemySize, enemySize);
            
            // Borde
            gc.setStroke(Color.DARKRED);
            gc.setLineWidth(2);
            gc.strokeOval(x - enemySize/2, y - enemySize/2, enemySize, enemySize);
            
            current = current.getNext();
        }
    }
    
    /**
     * Renderiza la retícula del mouse
     */
    private void renderReticle() {
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        
        // Cruz
        gc.strokeLine(mouseX - 10, mouseY, mouseX + 10, mouseY);
        gc.strokeLine(mouseX, mouseY - 10, mouseX, mouseY + 10);
        
        // Círculo
        gc.strokeOval(mouseX - 15, mouseY - 15, 30, 30);
    }
    
    /**
     * Actualiza los indicadores del HUD
     */
    private void updateHUD() {
        Player player = controller.getPlayer();
        AmmoManager ammo = controller.getAmmoManager();
        
        // Vida
        lblHealth.setText(String.format("♥ Vida: %d/3", player.getVida()));
        
        // Arma
        String weapon = controller.getCurrentWeapon().toUpperCase();
        lblWeapon.setText("⚔ Arma: " + weapon);
        
        // Municiones
        int ammoCount = weapon.equals("RIFLE") ? 
            ammo.getRifleAmmo() : ammo.getRevolverAmmo();
        lblAmmo.setText(String.format("➤ Municiones: %d", ammoCount));
        
        // Escenario
        lblScenario.setText("📍 " + controller.getCurrentScenarioName());
        
        // Inventario
        updateInventoryDisplay();
    }
    
    /**
     * Actualiza la visualización del inventario
     */
    private void updateInventoryDisplay() {
        Inventory inv = controller.getInventory();
        StringBuilder sb = new StringBuilder();
        
        var items = inv.getItems();
        var iterator = items.iterator();
        
        while (iterator.hasNext()) {
            ItemInventory item = iterator.next();
            sb.append("• ").append(item.toString()).append("\n");
        }
        
        if (sb.length() == 0) {
            sb.append("(Vacío)");
        }
        
        lblInventory.setText(sb.toString());
    }
    
    /**
     * Maneja el disparo
     */
    private void shoot(double canvasX, double canvasY) {
        // Convertir coordenadas de canvas a coordenadas del modelo
        int modelX = (int)(canvasX / SCALE);
        int modelY = (int)(canvasY / SCALE);
        
        boolean hit = controller.shoot(modelX, modelY);
        
        if (hit) {
            // Generar diálogo con Gemini API (en hilo separado para no bloquear)
            new Thread(() -> {
                String dialog = geminiService.generateEnemyDeathDialog(
                    "Bandido",
                    controller.getCurrentWeapon(),
                    controller.getCurrentScenarioName()
                );
                
                // Actualizar UI en el hilo de JavaFX
                javafx.application.Platform.runLater(() -> {
                    showDialog(dialog);
                });
            }).start();
        }
    }
    
    /**
     * Usa el primer botiquín del inventario
     */
    private void useFirstMedkit() {
        Inventory inv = controller.getInventory();
        var items = inv.getItems();
        var iterator = items.iterator();
        
        while (iterator.hasNext()) {
            ItemInventory item = iterator.next();
            if (item.getTipo() == TypeItem.MEDICINA) {
                controller.useItem(item);
                showDialog("Has usado un botiquín. +1 Vida");
                break;
            }
        }
    }
    
    /**
     * Muestra un diálogo en la parte inferior
     */
    private void showDialog(String message) {
        lblDialog.setText(message);
        
        // Ocultar después de 5 segundos
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                javafx.application.Platform.runLater(() -> lblDialog.setText(""));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    /**
     * Verifica el estado del juego (Game Over / Victoria)
     */
    private void checkGameStatus() {
        if (controller.isGameOver()) {
            renderLoop.stop();
            controller.stopGameLoop();
            showGameOver();
        } else if (controller.isVictory()) {
            renderLoop.stop();
            controller.stopGameLoop();
            showVictory();
        }
    }
    
    /**
     * Muestra pantalla de Game Over
     */
    private void showGameOver() {
        GameOverView gameOverView = new GameOverView(stage, controller);
        gameOverView.show();
    }
    
    /**
     * Muestra pantalla de Victoria
     */
    private void showVictory() {
        VictoryView victoryView = new VictoryView(stage, controller);
        victoryView.show();
    }
    
    /**
     * Pausa el juego
     */
    private void pauseGame() {
        renderLoop.stop();
        controller.stopGameLoop();
        
        // Aquí podrías mostrar un menú de pausa
        MainMenuView mainMenu = new MainMenuView(stage);
        mainMenu.show();
    }
    
    /**
     * Muestra la vista en el stage
     */
    public void show() {
        stage.setScene(scene);
    }
}
