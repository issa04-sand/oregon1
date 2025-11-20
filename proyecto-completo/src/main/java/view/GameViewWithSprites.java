package view;

import controller.GameController;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
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
 * GameView - Vista principal del juego CON SOPORTE DE SPRITES
 * 
 * CUSTOMIZACIÓN DE SPRITES:
 * - Coloca tus imágenes en src/main/resources/sprites/
 * - Actualiza las constantes SPRITE_* con tus rutas
 * - Los sprites se cargan automáticamente
 * 
 * ESTRUCTURA DE CARPETAS RECOMENDADA:
 * src/main/resources/
 * ├── sprites/
 * │   ├── player/
 * │   │   ├── player_idle.png
 * │   │   ├── player_walk_up.png
 * │   │   ├── player_walk_down.png
 * │   │   ├── player_walk_left.png
 * │   │   └── player_walk_right.png
 * │   ├── enemies/
 * │   │   ├── enemy_bandit.png
 * │   │   ├── enemy_wolf.png
 * │   │   └── enemy_bear.png
 * │   ├── items/
 * │   │   ├── food.png
 * │   │   ├── medicine.png
 * │   │   └── ammo.png
 * │   ├── weapons/
 * │   │   ├── rifle.png
 * │   │   └── revolver.png
 * │   ├── ui/
 * │   │   ├── heart_full.png
 * │   │   ├── heart_empty.png
 * │   │   └── reticle.png
 * │   └── backgrounds/
 * │       ├── llanuras_bg.png
 * │       ├── montanas_bg.png
 * │       └── rio_bg.png
 */
public class GameViewWithSprites {
    
    // ========================================
    // CONFIGURACIÓN DE SPRITES - PERSONALIZA AQUÍ
    // ========================================
    
    // JUGADOR
    private static final String SPRITE_PLAYER_IDLE = "/sprites/player/player_idle.png";
    private static final String SPRITE_PLAYER_WALK_UP = "/sprites/player/player_walk_up.png";
    private static final String SPRITE_PLAYER_WALK_DOWN = "/sprites/player/player_walk_down.png";
    private static final String SPRITE_PLAYER_WALK_LEFT = "/sprites/player/player_walk_left.png";
    private static final String SPRITE_PLAYER_WALK_RIGHT = "/sprites/player/player_walk_right.png";
    
    // ENEMIGOS
    private static final String SPRITE_ENEMY_BANDIT = "/sprites/enemies/enemy_bandit.png";
    private static final String SPRITE_ENEMY_WOLF = "/sprites/enemies/enemy_wolf.png";
    private static final String SPRITE_ENEMY_BEAR = "/sprites/enemies/enemy_bear.png";
    
    // ITEMS (RECURSOS)
    private static final String SPRITE_ITEM_FOOD = "/sprites/items/food.png";
    private static final String SPRITE_ITEM_MEDICINE = "/sprites/items/medicine.png";
    private static final String SPRITE_ITEM_AMMO = "/sprites/items/ammo.png";
    
    // ARMAS (ICONOS UI)
    private static final String SPRITE_WEAPON_RIFLE = "/sprites/weapons/rifle.png";
    private static final String SPRITE_WEAPON_REVOLVER = "/sprites/weapons/revolver.png";
    
    // UI
    private static final String SPRITE_HEART_FULL = "/sprites/ui/heart_full.png";
    private static final String SPRITE_HEART_EMPTY = "/sprites/ui/heart_empty.png";
    private static final String SPRITE_RETICLE = "/sprites/ui/reticle.png";
    
    // FONDOS DE ESCENARIOS
    private static final String SPRITE_BG_LLANURAS = "/sprites/backgrounds/llanuras_bg.png";
    private static final String SPRITE_BG_MONTANAS = "/sprites/backgrounds/montanas_bg.png";
    private static final String SPRITE_BG_RIO = "/sprites/backgrounds/rio_bg.png";
    
    // TAMAÑOS (ajusta según tus sprites)
    private static final double PLAYER_WIDTH = 40;
    private static final double PLAYER_HEIGHT = 40;
    private static final double ENEMY_WIDTH = 35;
    private static final double ENEMY_HEIGHT = 35;
    private static final double ITEM_SIZE = 20;
    
    // ========================================
    // FIN DE CONFIGURACIÓN
    // ========================================
    
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
    
    // Sprites cargados
    private Image playerSprite;
    private Image currentPlayerSprite;
    private Image enemySprite;
    private Image foodSprite;
    private Image medicineSprite;
    private Image ammoSprite;
    private Image rifleIcon;
    private Image revolverIcon;
    private Image heartFullIcon;
    private Image heartEmptyIcon;
    private Image reticleSprite;
    private Image backgroundImage;
    
    // Animación
    private AnimationTimer renderLoop;
    private double mouseX;
    private double mouseY;
    
    // Servicio de diálogos
    private GeminiService geminiService;
    
    // Escala para conversión de coordenadas
    private static final double SCALE = 20.0;
    
    // Estado del movimiento (para animaciones)
    private String lastMovement = "idle";
    
    public GameViewWithSprites(Stage stage, GameController controller) {
        this.stage = stage;
        this.controller = controller;
        
        // Inicializar Gemini Service
        this.geminiService = new GeminiService("YOUR_GEMINI_API_KEY");
        
        // Cargar sprites
        loadSprites();
        
        createUI();
        setupInputHandlers();
        startRenderLoop();
    }
    
    /**
     * Carga todos los sprites del juego
     * Si un sprite no existe, usa un color de respaldo
     */
    private void loadSprites() {
        try {
            // Jugador
            playerSprite = loadSpriteOrNull(SPRITE_PLAYER_IDLE);
            currentPlayerSprite = playerSprite;
            
            // Enemigos
            enemySprite = loadSpriteOrNull(SPRITE_ENEMY_BANDIT);
            
            // Items
            foodSprite = loadSpriteOrNull(SPRITE_ITEM_FOOD);
            medicineSprite = loadSpriteOrNull(SPRITE_ITEM_MEDICINE);
            ammoSprite = loadSpriteOrNull(SPRITE_ITEM_AMMO);
            
            // Armas (iconos)
            rifleIcon = loadSpriteOrNull(SPRITE_WEAPON_RIFLE);
            revolverIcon = loadSpriteOrNull(SPRITE_WEAPON_REVOLVER);
            
            // UI
            heartFullIcon = loadSpriteOrNull(SPRITE_HEART_FULL);
            heartEmptyIcon = loadSpriteOrNull(SPRITE_HEART_EMPTY);
            reticleSprite = loadSpriteOrNull(SPRITE_RETICLE);
            
            // Fondo inicial
            updateBackgroundForScenario();
            
        } catch (Exception e) {
            System.err.println("Error cargando sprites: " + e.getMessage());
            System.err.println("Se usarán formas de color como respaldo.");
        }
    }
    
    /**
     * Intenta cargar un sprite, retorna null si no existe
     */
    private Image loadSpriteOrNull(String path) {
        try {
            var stream = getClass().getResourceAsStream(path);
            if (stream != null) {
                return new Image(stream);
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar sprite: " + path);
        }
        return null;
    }
    
    /**
     * Actualiza el fondo según el escenario actual
     */
    private void updateBackgroundForScenario() {
        Scenario scenario = controller.getCurrentScenario();
        String bgPath = null;
        
        switch (scenario.getType()) {
            case START:
                bgPath = SPRITE_BG_LLANURAS;
                break;
            case ROAD:
                bgPath = SPRITE_BG_MONTANAS;
                break;
            case RIVER:
                bgPath = SPRITE_BG_RIO;
                break;
        }
        
        if (bgPath != null) {
            backgroundImage = loadSpriteOrNull(bgPath);
        }
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
        
        // Vida con iconos de corazones
        HBox healthBox = new HBox(5);
        lblHealth = createHUDLabel("❤ Vida: 3/3");
        lblHealth.setTextFill(Color.web("#e74c3c"));
        healthBox.getChildren().add(lblHealth);
        
        // Arma actual con icono
        HBox weaponBox = new HBox(5);
        lblWeapon = createHUDLabel("⚔ Arma: RIFLE");
        lblWeapon.setTextFill(Color.web("#f39c12"));
        weaponBox.getChildren().add(lblWeapon);
        
        // Municiones
        lblAmmo = createHUDLabel("➤ Municiones: 20");
        lblAmmo.setTextFill(Color.web("#3498db"));
        
        // Escenario
        lblScenario = createHUDLabel("📍 Llanuras y Praderas");
        lblScenario.setTextFill(Color.web("#2ecc71"));
        
        hud.getChildren().addAll(healthBox, weaponBox, lblAmmo, lblScenario);
        
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
            
            // Movimiento con animaciones
            if (code == KeyCode.W || code == KeyCode.UP) {
                controller.movePlayer("up");
                lastMovement = "up";
                currentPlayerSprite = loadSpriteOrNull(SPRITE_PLAYER_WALK_UP);
                if (currentPlayerSprite == null) currentPlayerSprite = playerSprite;
            } else if (code == KeyCode.S || code == KeyCode.DOWN) {
                controller.movePlayer("down");
                lastMovement = "down";
                currentPlayerSprite = loadSpriteOrNull(SPRITE_PLAYER_WALK_DOWN);
                if (currentPlayerSprite == null) currentPlayerSprite = playerSprite;
            } else if (code == KeyCode.A || code == KeyCode.LEFT) {
                controller.movePlayer("left");
                lastMovement = "left";
                currentPlayerSprite = loadSpriteOrNull(SPRITE_PLAYER_WALK_LEFT);
                if (currentPlayerSprite == null) currentPlayerSprite = playerSprite;
            } else if (code == KeyCode.D || code == KeyCode.RIGHT) {
                controller.movePlayer("right");
                lastMovement = "right";
                currentPlayerSprite = loadSpriteOrNull(SPRITE_PLAYER_WALK_RIGHT);
                if (currentPlayerSprite == null) currentPlayerSprite = playerSprite;
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
        
        // Volver a idle cuando se suelta la tecla
        scene.setOnKeyReleased(e -> {
            lastMovement = "idle";
            currentPlayerSprite = playerSprite;
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
        // Renderizar fondo del escenario
        renderBackground();
        
        // Renderizar recursos del escenario (comida, medicina, munición)
        renderResources();
        
        // Renderizar enemigos
        renderEnemies();
        
        // Renderizar jugador
        renderPlayer();
        
        // Renderizar retícula
        renderReticle();
        
        // Renderizar efectos (opcional)
        renderEffects();
    }
    
    /**
     * ============================================
     * MÉTODO CLAVE: Renderiza el fondo del escenario
     * AQUÍ ES DONDE COLOCAS TUS FONDOS PERSONALIZADOS
     * ============================================
     */
    private void renderBackground() {
        if (backgroundImage != null) {
            // Dibujar imagen de fondo escalada al canvas
            gc.drawImage(backgroundImage, 0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        } else {
            // Color de respaldo según el escenario
            Scenario scenario = controller.getCurrentScenario();
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
            
            // Dibujar grid opcional
            drawGrid();
        }
    }
    
    /**
     * Dibuja un grid sutil (opcional)
     */
    private void drawGrid() {
        gc.setStroke(Color.rgb(0, 0, 0, 0.05));
        gc.setLineWidth(1);
        for (int i = 0; i < gameCanvas.getWidth(); i += SCALE) {
            gc.strokeLine(i, 0, i, gameCanvas.getHeight());
        }
        for (int i = 0; i < gameCanvas.getHeight(); i += SCALE) {
            gc.strokeLine(0, i, gameCanvas.getWidth(), i);
        }
    }
    
    /**
     * ============================================
     * MÉTODO CLAVE: Renderiza los recursos del escenario
     * AQUÍ ES DONDE APARECEN LOS ITEMS RECOLECTABLES
     * ============================================
     */
    private void renderResources() {
        // TODO: Implementar sistema de recursos en el mapa
        // Por ahora, ejemplo de cómo renderizar un item:
        
        // Ejemplo: Comida en posición (5, 5)
        /*
        if (foodSprite != null) {
            double x = 5 * SCALE;
            double y = 5 * SCALE;
            gc.drawImage(foodSprite, x - ITEM_SIZE/2, y - ITEM_SIZE/2, ITEM_SIZE, ITEM_SIZE);
        }
        */
        
        // En tu implementación, deberías iterar sobre una lista de recursos:
        // for (Resource resource : controller.getScenarioResources()) {
        //     renderResource(resource);
        // }
    }
    
    /**
     * ============================================
     * MÉTODO CLAVE: Renderiza al jugador
     * AQUÍ ES DONDE DIBUJAS TU SPRITE DEL JUGADOR
     * ============================================
     */
    private void renderPlayer() {
        Player player = controller.getPlayer();
        double x = player.getX() * SCALE;
        double y = player.getY() * SCALE;
        
        if (currentPlayerSprite != null) {
            // DIBUJAR SPRITE DEL JUGADOR
            gc.drawImage(
                currentPlayerSprite,
                x - PLAYER_WIDTH/2,
                y - PLAYER_HEIGHT/2,
                PLAYER_WIDTH,
                PLAYER_HEIGHT
            );
        } else {
            // Respaldo: círculo azul
            // Sombra
            gc.setFill(Color.rgb(0, 0, 0, 0.3));
            gc.fillOval(x - PLAYER_WIDTH/2 + 2, y - PLAYER_HEIGHT/2 + 2, PLAYER_WIDTH, PLAYER_HEIGHT);
            
            // Jugador
            gc.setFill(Color.web("#3498db"));
            gc.fillOval(x - PLAYER_WIDTH/2, y - PLAYER_HEIGHT/2, PLAYER_WIDTH, PLAYER_HEIGHT);
            
            // Borde
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2);
            gc.strokeOval(x - PLAYER_WIDTH/2, y - PLAYER_HEIGHT/2, PLAYER_WIDTH, PLAYER_HEIGHT);
        }
    }
    
    /**
     * ============================================
     * MÉTODO CLAVE: Renderiza todos los enemigos
     * AQUÍ ES DONDE DIBUJAS TUS SPRITES DE ENEMIGOS
     * ============================================
     */
    private void renderEnemies() {
        ListEnemy enemies = controller.getEnemies();
        NodeEnemy current = enemies.getFirst();
        
        while (current != null) {
            Enemy enemy = current.getData();
            double x = enemy.getX() * SCALE;
            double y = enemy.getY() * SCALE;
            
            if (enemySprite != null) {
                // DIBUJAR SPRITE DEL ENEMIGO
                gc.drawImage(
                    enemySprite,
                    x - ENEMY_WIDTH/2,
                    y - ENEMY_HEIGHT/2,
                    ENEMY_WIDTH,
                    ENEMY_HEIGHT
                );
            } else {
                // Respaldo: círculo rojo
                // Sombra
                gc.setFill(Color.rgb(0, 0, 0, 0.3));
                gc.fillOval(x - ENEMY_WIDTH/2 + 2, y - ENEMY_HEIGHT/2 + 2, ENEMY_WIDTH, ENEMY_HEIGHT);
                
                // Enemigo
                gc.setFill(Color.web("#e74c3c"));
                gc.fillOval(x - ENEMY_WIDTH/2, y - ENEMY_HEIGHT/2, ENEMY_WIDTH, ENEMY_HEIGHT);
                
                // Borde
                gc.setStroke(Color.DARKRED);
                gc.setLineWidth(2);
                gc.strokeOval(x - ENEMY_WIDTH/2, y - ENEMY_HEIGHT/2, ENEMY_WIDTH, ENEMY_HEIGHT);
            }
            
            current = current.getNext();
        }
    }
    
    /**
     * ============================================
     * MÉTODO CLAVE: Renderiza la retícula del mouse
     * AQUÍ PUEDES USAR TU SPRITE DE MIRA PERSONALIZADA
     * ============================================
     */
    private void renderReticle() {
        if (reticleSprite != null) {
            // DIBUJAR SPRITE DE RETÍCULA
            double reticleSize = 40;
            gc.drawImage(
                reticleSprite,
                mouseX - reticleSize/2,
                mouseY - reticleSize/2,
                reticleSize,
                reticleSize
            );
        } else {
            // Respaldo: retícula con cruz
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2);
            
            // Cruz
            gc.strokeLine(mouseX - 10, mouseY, mouseX + 10, mouseY);
            gc.strokeLine(mouseX, mouseY - 10, mouseX, mouseY + 10);
            
            // Círculo
            gc.strokeOval(mouseX - 15, mouseY - 15, 30, 30);
        }
    }
    
    /**
     * Renderiza efectos visuales (disparos, explosiones, etc.)
     */
    private void renderEffects() {
        // TODO: Agregar efectos de partículas, disparos, etc.
        // Ejemplo:
        // - Efecto de disparo (línea desde jugador hasta mouse)
        // - Efecto de impacto cuando eliminas enemigo
        // - Partículas al recoger items
    }
    
    /**
     * Actualiza los indicadores del HUD
     */
    private void updateHUD() {
        Player player = controller.getPlayer();
        AmmoManager ammo = controller.getAmmoManager();
        
        // Vida con corazones
        int vida = player.getVida();
        String heartDisplay = "";
        for (int i = 0; i < 3; i++) {
            if (i < vida) {
                heartDisplay += "❤ ";
            } else {
                heartDisplay += "♡ ";
            }
        }
        lblHealth.setText(heartDisplay + String.format("(%d/3)", vida));
        
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
        
        // Actualizar fondo si cambió de escenario
        updateBackgroundForScenario();
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
            // Agregar icono según tipo
            String icon = "";
            switch (item.getTipo()) {
                case COMIDA:
                    icon = "🍖";
                    break;
                case MEDICINA:
                    icon = "💊";
                    break;
                case MUNICION:
                    icon = "🔫";
                    break;
            }
            sb.append(icon).append(" ").append(item.toString()).append("\n");
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
        int modelX = (int)(canvasX / SCALE);
        int modelY = (int)(canvasY / SCALE);
        
        boolean hit = controller.shoot(modelX, modelY);
        
        if (hit) {
            // Generar diálogo con Gemini API
            new Thread(() -> {
                String dialog = geminiService.generateEnemyDeathDialog(
                    "Bandido",
                    controller.getCurrentWeapon(),
                    controller.getCurrentScenarioName()
                );
                
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
     * Verifica el estado del juego
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
    
    private void showGameOver() {
        GameOverView gameOverView = new GameOverView(stage, controller);
        gameOverView.show();
    }
    
    private void showVictory() {
        VictoryView victoryView = new VictoryView(stage, controller);
        victoryView.show();
    }
    
    private void pauseGame() {
        renderLoop.stop();
        controller.stopGameLoop();
        MainMenuView mainMenu = new MainMenuView(stage);
        mainMenu.show();
    }
    
    public void show() {
        stage.setScene(scene);
    }
}
