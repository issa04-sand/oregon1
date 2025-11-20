package controller;

import model.*;
import javafx.animation.AnimationTimer;
import java.util.Random;

/**
 * GameController - Controlador principal del juego
 * Patrón: MVC (Model-View-Controller)
 * 
 * Responsabilidades:
 * - Gestionar la lógica del juego
 * - Coordinar el modelo y la vista
 * - Manejar las transiciones entre escenarios
 * - Controlar el ciclo de vida del juego
 */
public class GameController {
    
    // Modelo del juego
    private Player player;
    private Inventory inventory;
    private ListEnemy enemies;
    private Scenario currentScenario;
    private SceneState sceneState;
    private TreeAchivement achievementTree;
    private OregonTrail gameModel;
    
    // Controladores específicos
    private MovementController movementController;
    private Spawner spawner;
    private EnemyAI enemyAI;
    private AmmoManager ammoManager;
    private FireControl fireControl;
    
    // Estado del juego
    private String currentWeapon;
    private boolean isGameOver;
    private boolean isVictory;
    private int currentScenarioIndex;
    private Random random;
    
    // Timer para animaciones
    private AnimationTimer gameLoop;
    private long lastEnemyUpdate;
    private long lastSpawnTime;
    
    /**
     * Constructor
     * Inicializa el controlador del juego
     */
    public GameController() {
        this.random = new Random();
        this.currentScenarioIndex = 0;
        this.isGameOver = false;
        this.isVictory = false;
        this.lastEnemyUpdate = 0;
        this.lastSpawnTime = 0;
        initializeGame();
    }
    
    /**
     * Inicializa todos los componentes del juego
     */
    private void initializeGame() {
        // Inicializar jugador
        player = new Player(5, 5, 3);
        
        // Inicializar inventario con capacidad de 10 items
        inventory = new Inventory(10);
        
        // Agregar items iniciales
        inventory.add(ItemInventory.food(5));
        inventory.add(ItemInventory.medkit(3));
        inventory.add(ItemInventory.ammo("rifle", 20));
        inventory.add(ItemInventory.ammo("revolver", 30));
        
        // Inicializar municiones
        ammoManager = new AmmoManager(20, 30);
        currentWeapon = "rifle";
        
        // Inicializar control de disparo
        Reloading reloading = new Reloading(true, 2000);
        fireControl = new FireControl(currentWeapon, ammoManager, reloading);
        
        // Inicializar escenario
        int[][] board = new int[30][30];
        currentScenario = new Scenario(board, TypeScenarios.START);
        currentScenario.setCantMaxRespawnEnemies(8);
        currentScenario.setCantResources(5);
        
        // Inicializar mapa y movimiento
        MapLoader mapLoader = MapLoader.of("llanuras.map");
        movementController = new MovementController(mapLoader, player);
        
        // Inicializar enemigos
        enemies = new ListEnemy();
        spawner = new Spawner(8, 5);
        spawner.setEnemies(enemies);
        enemyAI = new EnemyAI(100.0);
        
        // Inicializar estado de la escena
        sceneState = new SceneState(3, 20, 30, 10);
        
        // Inicializar sistema de logros
        gameModel = new OregonTrail();
        gameModel.insertAllachivementsToArraylist();
        gameModel.insertAchivementsToTree();
        achievementTree = new TreeAchivement();
    }
    
    /**
     * Inicia el loop principal del juego
     */
    public void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isGameOver && !isVictory) {
                    updateGame(now);
                }
            }
        };
        gameLoop.start();
    }
    
    /**
     * Actualiza la lógica del juego en cada frame
     */
    private void updateGame(long now) {
        // Spawn de enemigos cada 3 segundos
        if (now - lastSpawnTime > 3_000_000_000L) {
            spawner.tickSpawn(currentScenario, player);
            lastSpawnTime = now;
        }
        
        // Actualizar IA de enemigos cada 100ms
        if (now - lastEnemyUpdate > 100_000_000L) {
            updateEnemies();
            lastEnemyUpdate = now;
        }
        
        // Verificar condiciones de victoria/derrota
        checkGameStatus();
    }
    
    /**
     * Actualiza el comportamiento de todos los enemigos
     */
    private void updateEnemies() {
        NodeEnemy current = enemies.getFirst();
        while (current != null) {
            Enemy enemy = current.getData();
            enemyAI.update(enemy, player);
            
            // Si el enemigo alcanza al jugador, causar daño
            if (enemy.getX() == player.getX() && enemy.getY() == player.getY()) {
                player.damage(1);
            }
            
            current = current.getNext();
        }
    }
    
    /**
     * Verifica el estado del juego (victoria/derrota)
     */
    private void checkGameStatus() {
        // Game Over si la vida llega a 0
        if (player.getVida() <= 0) {
            isGameOver = true;
            stopGameLoop();
        }
        
        // Victoria si completa todos los escenarios
        if (currentScenarioIndex >= 3) {
            isVictory = true;
            stopGameLoop();
        }
    }
    
    /**
     * Detiene el loop del juego
     */
    public void stopGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }
    
    /**
     * Maneja el movimiento del jugador
     */
    public void movePlayer(String direction) {
        switch (direction.toLowerCase()) {
            case "up":
            case "w":
                movementController.moveUp();
                break;
            case "down":
            case "s":
                movementController.moveDown();
                break;
            case "left":
            case "a":
                movementController.moveLeft();
                break;
            case "right":
            case "d":
                movementController.moveRight();
                break;
        }
    }
    
    /**
     * Cambia el arma actual
     */
    public void switchWeapon() {
        currentWeapon = currentWeapon.equals("rifle") ? "revolver" : "rifle";
        Reloading reloading = new Reloading(true, 2000);
        fireControl = new FireControl(currentWeapon, ammoManager, reloading);
    }
    
    /**
     * Dispara el arma actual
     */
    public boolean shoot(int targetX, int targetY) {
        if (fireControl.shoot()) {
            // Verificar si impactó algún enemigo
            return checkEnemyHit(targetX, targetY);
        }
        return false;
    }
    
    /**
     * Verifica si un disparo impactó un enemigo
     */
    private boolean checkEnemyHit(int x, int y) {
        NodeEnemy current = enemies.getFirst();
        while (current != null) {
            Enemy enemy = current.getData();
            // Radio de impacto de 20 píxeles
            double distance = Math.hypot(enemy.getX() - x, enemy.getY() - y);
            if (distance < 20) {
                enemies.delete(enemy);
                return true;
            }
            current = current.getNext();
        }
        return false;
    }
    
    /**
     * Usa un item del inventario
     */
    public boolean useItem(ItemInventory item) {
        return inventory.use(item, player);
    }
    
    /**
     * Recoge un recurso del escenario
     */
    public boolean pickupResource(ItemInventory item) {
        return inventory.add(item);
    }
    
    /**
     * Avanza al siguiente escenario
     */
    public void nextScenario() {
        currentScenarioIndex++;
        
        if (currentScenarioIndex == 1) {
            // Montañas Rocosas
            currentScenario.setType(TypeScenarios.ROAD);
            currentScenario.setCantMaxRespawnEnemies(12);
            sceneState.setCurrentMap("montanas.map");
        } else if (currentScenarioIndex == 2) {
            // Río Columbia
            currentScenario.setType(TypeScenarios.RIVER);
            currentScenario.setCantMaxRespawnEnemies(15);
            sceneState.setCurrentMap("rio.map");
        }
        
        // Limpiar enemigos del escenario anterior
        enemies = new ListEnemy();
        spawner.setEnemies(enemies);
        
        // Reposicionar jugador
        player.setPosition(5, 5);
    }
    
    /**
     * Desbloquea un logro
     */
    public void unlockAchievement(int difficulty, String name, String description) {
        Achivement achievement = new Achivement(difficulty, name, description);
        NodeAchivement node = new NodeAchivement(achievement, null, null);
        achievementTree.insert(node);
    }
    
    // ============= GETTERS =============
    
    public Player getPlayer() {
        return player;
    }
    
    public Inventory getInventory() {
        return inventory;
    }
    
    public ListEnemy getEnemies() {
        return enemies;
    }
    
    public Scenario getCurrentScenario() {
        return currentScenario;
    }
    
    public SceneState getSceneState() {
        return sceneState;
    }
    
    public TreeAchivement getAchievementTree() {
        return achievementTree;
    }
    
    public AmmoManager getAmmoManager() {
        return ammoManager;
    }
    
    public String getCurrentWeapon() {
        return currentWeapon;
    }
    
    public boolean isGameOver() {
        return isGameOver;
    }
    
    public boolean isVictory() {
        return isVictory;
    }
    
    public int getCurrentScenarioIndex() {
        return currentScenarioIndex;
    }
    
    public String getCurrentScenarioName() {
        switch (currentScenarioIndex) {
            case 0: return "Llanuras y Praderas";
            case 1: return "Montañas Rocosas";
            case 2: return "Río Columbia";
            default: return "Desconocido";
        }
    }
    
    /**
     * Reinicia el juego
     */
    public void resetGame() {
        stopGameLoop();
        isGameOver = false;
        isVictory = false;
        currentScenarioIndex = 0;
        initializeGame();
    }
}
