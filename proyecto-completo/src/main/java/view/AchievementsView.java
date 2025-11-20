package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Achivement;
import model.NodeAchivement;
import model.OregonTrail;

/**
 * AchievementsView - Vista del árbol de logros
 * 
 * Muestra el árbol binario de logros de forma visual
 * Los logros están ordenados por dificultad
 */
public class AchievementsView {
    
    private Stage stage;
    private MainMenuView returnView;
    private BorderPane root;
    private Scene scene;
    private Canvas treeCanvas;
    private GraphicsContext gc;
    
    private OregonTrail gameModel;
    
    private static final double NODE_RADIUS = 40;
    private static final double LEVEL_HEIGHT = 120;
    private static final double HORIZONTAL_SPACING = 100;
    
    public AchievementsView(Stage stage, MainMenuView returnView) {
        this.stage = stage;
        this.returnView = returnView;
        
        // Inicializar modelo de logros
        gameModel = new OregonTrail();
        gameModel.insertAllachivementsToArraylist();
        gameModel.insertAchivementsToTree();
        
        createUI();
    }
    
    private void createUI() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");
        
        // Título
        VBox header = createHeader();
        root.setTop(header);
        
        // Canvas para el árbol
        treeCanvas = new Canvas(1200, 600);
        gc = treeCanvas.getGraphicsContext2D();
        
        // Dibujar el árbol
        drawTree();
        
        ScrollPane scrollPane = new ScrollPane(treeCanvas);
        scrollPane.setStyle("-fx-background: #1a1a2e;");
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        
        root.setCenter(scrollPane);
        
        // Botón volver
        Button btnBack = createBackButton();
        VBox footer = new VBox(btnBack);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(20));
        root.setBottom(footer);
        
        scene = new Scene(root, 1280, 720);
    }
    
    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #16213e;");
        
        Text title = new Text("ÁRBOL DE LOGROS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        title.setFill(Color.web("#ffd700"));
        
        Text subtitle = new Text("Organizados por dificultad (Árbol Binario de Búsqueda)");
        subtitle.setFont(Font.font("Arial", 18));
        subtitle.setFill(Color.LIGHTGRAY);
        
        header.getChildren().addAll(title, subtitle);
        
        return header;
    }
    
    private Button createBackButton() {
        Button button = new Button("⬅ VOLVER");
        button.setPrefWidth(200);
        button.setPrefHeight(45);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        button.setStyle(
            "-fx-background-color: #8b4513;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #d2691e;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-cursor: hand;"
        );
        
        button.setOnAction(e -> goBack());
        
        return button;
    }
    
    /**
     * Dibuja el árbol binario de logros
     */
    private void drawTree() {
        // Limpiar canvas
        gc.setFill(Color.web("#1a1a2e"));
        gc.fillRect(0, 0, treeCanvas.getWidth(), treeCanvas.getHeight());
        
        NodeAchivement root = gameModel.treeAchivement.getRootAllAchivements();
        
        if (root == null) {
            // Si no hay logros, mostrar mensaje
            gc.setFill(Color.LIGHTGRAY);
            gc.setFont(Font.font("Arial", 24));
            gc.fillText("No hay logros disponibles", 
                treeCanvas.getWidth()/2 - 150, 
                treeCanvas.getHeight()/2);
            return;
        }
        
        // Dibujar el árbol desde la raíz
        double startX = treeCanvas.getWidth() / 2;
        double startY = 80;
        
        drawNode(root, startX, startY, treeCanvas.getWidth() / 4, 0);
    }
    
    /**
     * Dibuja un nodo del árbol recursivamente
     */
    private void drawNode(NodeAchivement node, double x, double y, 
                          double offsetX, int level) {
        if (node == null) return;
        
        Achivement achievement = node.getValue();
        
        // Dibujar conexiones a hijos primero (para que queden detrás)
        gc.setStroke(Color.web("#d2691e"));
        gc.setLineWidth(2);
        
        if (node.getLeft() != null) {
            double childX = x - offsetX;
            double childY = y + LEVEL_HEIGHT;
            gc.strokeLine(x, y, childX, childY);
            
            // Recursión izquierda
            drawNode(node.getLeft(), childX, childY, offsetX / 2, level + 1);
        }
        
        if (node.getRight() != null) {
            double childX = x + offsetX;
            double childY = y + LEVEL_HEIGHT;
            gc.strokeLine(x, y, childX, childY);
            
            // Recursión derecha
            drawNode(node.getRight(), childX, childY, offsetX / 2, level + 1);
        }
        
        // Dibujar el nodo actual
        drawAchievementNode(achievement, x, y);
    }
    
    /**
     * Dibuja un nodo individual de logro
     */
    private void drawAchievementNode(Achivement achievement, double x, double y) {
        // Sombra
        gc.setFill(Color.rgb(0, 0, 0, 0.4));
        gc.fillOval(x - NODE_RADIUS + 3, y - NODE_RADIUS + 3, 
            NODE_RADIUS * 2, NODE_RADIUS * 2);
        
        // Color según dificultad
        Color nodeColor;
        if (achievement.getDifficulty() <= 3) {
            nodeColor = Color.web("#2ecc71"); // Fácil - Verde
        } else if (achievement.getDifficulty() <= 7) {
            nodeColor = Color.web("#f39c12"); // Medio - Naranja
        } else {
            nodeColor = Color.web("#e74c3c"); // Difícil - Rojo
        }
        
        // Círculo del nodo
        gc.setFill(nodeColor);
        gc.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        
        // Borde
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.strokeOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        
        // Número de dificultad
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        String diffText = String.valueOf(achievement.getDifficulty());
        double textWidth = gc.getFont().getSize() * diffText.length() * 0.5;
        gc.fillText(diffText, x - textWidth, y + 10);
        
        // Nombre del logro (debajo del nodo)
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        gc.setFill(Color.LIGHTGRAY);
        String name = achievement.getNameAchivement();
        if (name.length() > 15) {
            name = name.substring(0, 12) + "...";
        }
        double nameWidth = name.length() * 6;
        gc.fillText(name, x - nameWidth/2, y + NODE_RADIUS + 20);
    }
    
    private void goBack() {
        if (returnView != null) {
            returnView.show();
        } else {
            MainMenuView mainMenu = new MainMenuView(stage);
            mainMenu.show();
        }
    }
    
    public void show() {
        stage.setScene(scene);
    }
}
