package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * ManualView - Manual de usuario del juego
 * 
 * Incluye:
 * - Controles del teclado y mouse
 * - Explicación de indicadores (HUD)
 * - Estrategias de supervivencia
 * - Información sobre escenarios
 */
public class ManualView {
    
    private Stage stage;
    private MainMenuView returnView;
    private BorderPane root;
    private Scene scene;
    
    public ManualView(Stage stage, MainMenuView returnView) {
        this.stage = stage;
        this.returnView = returnView;
        createUI();
    }
    
    private void createUI() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");
        
        // Título
        VBox header = createHeader();
        root.setTop(header);
        
        // Tabs con diferentes secciones
        TabPane tabPane = createTabPane();
        root.setCenter(tabPane);
        
        // Botón volver
        Button btnBack = createBackButton();
        VBox footer = new VBox(btnBack);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(15));
        root.setBottom(footer);
        
        scene = new Scene(root, 1280, 720);
    }
    
    private VBox createHeader() {
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #16213e;");
        
        Text title = new Text("MANUAL DE USUARIO");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 38));
        title.setFill(Color.web("#f4a460"));
        
        Text subtitle = new Text("Guía completa para sobrevivir en Oregon Trail");
        subtitle.setFont(Font.font("Arial", 16));
        subtitle.setFill(Color.LIGHTGRAY);
        
        header.getChildren().addAll(title, subtitle);
        
        return header;
    }
    
    private TabPane createTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: #1a1a2e;");
        
        Tab tabControls = new Tab("⌨ Controles", createControlsContent());
        Tab tabHUD = new Tab("📊 Indicadores (HUD)", createHUDContent());
        Tab tabStrategy = new Tab("🎯 Estrategias", createStrategyContent());
        Tab tabScenarios = new Tab("🗺 Escenarios", createScenariosContent());
        
        // No permitir cerrar tabs
        tabControls.setClosable(false);
        tabHUD.setClosable(false);
        tabStrategy.setClosable(false);
        tabScenarios.setClosable(false);
        
        tabPane.getTabs().addAll(tabControls, tabHUD, tabStrategy, tabScenarios);
        
        return tabPane;
    }
    
    /**
     * Contenido de la pestaña Controles
     */
    private ScrollPane createControlsContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #16213e;");
        
        // Movimiento
        content.getChildren().add(createSection(
            "MOVIMIENTO",
            "W / ↑ : Mover hacia arriba\n" +
            "S / ↓ : Mover hacia abajo\n" +
            "A / ← : Mover hacia la izquierda\n" +
            "D / → : Mover hacia la derecha\n\n" +
            "El jugador no puede atravesar paredes ni límites del mapa."
        ));
        
        // Combate
        content.getChildren().add(createSection(
            "COMBATE",
            "Mouse : Mover la retícula de puntería\n" +
            "Click Izquierdo : Disparar el arma actual\n" +
            "Q : Cambiar de arma (Rifle ↔ Revolver)\n\n" +
            "• RIFLE: Mayor daño, menor cadencia de fuego\n" +
            "• REVOLVER: Menor daño, mayor cadencia de fuego\n\n" +
            "La munición es limitada. ¡Administra tus recursos!"
        ));
        
        // Inventario
        content.getChildren().add(createSection(
            "INVENTARIO Y RECURSOS",
            "E : Usar medicina (botiquín) del inventario\n\n" +
            "Recoge recursos caminando sobre ellos:\n" +
            "• 🍖 Comida: Mantiene tu salud\n" +
            "• 💊 Medicina: Recupera vida (+1)\n" +
            "• 🔫 Munición: Recargar armas\n\n" +
            "El inventario tiene capacidad limitada (10 items)."
        ));
        
        // Otros
        content.getChildren().add(createSection(
            "OTROS CONTROLES",
            "ESC : Pausar juego / Volver al menú\n" +
            "F : Ver pantalla completa (próximamente)\n" +
            "H : Ver este manual durante el juego"
        ));
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setStyle("-fx-background: #16213e;");
        scrollPane.setFitToWidth(true);
        
        return scrollPane;
    }
    
    /**
     * Contenido de la pestaña HUD
     */
    private ScrollPane createHUDContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #16213e;");
        
        content.getChildren().add(createSection(
            "♥ VIDA",
            "Indica tu salud actual. Comienza con 3 puntos de vida.\n" +
            "Pierdes 1 punto cada vez que un enemigo te ataca.\n" +
            "Usa medicina para recuperar vida.\n\n" +
            "⚠ Si llegas a 0 vida, es GAME OVER."
        ));
        
        content.getChildren().add(createSection(
            "⚔ ARMA ACTUAL",
            "Muestra qué arma estás usando actualmente.\n\n" +
            "RIFLE:\n" +
            "• Daño: Alto\n" +
            "• Velocidad: Baja\n" +
            "• Ideal para: Enemigos a distancia\n\n" +
            "REVOLVER:\n" +
            "• Daño: Medio\n" +
            "• Velocidad: Alta\n" +
            "• Ideal para: Combate rápido"
        ));
        
        content.getChildren().add(createSection(
            "➤ MUNICIONES",
            "Muestra las balas disponibles para el arma actual.\n" +
            "Cada disparo consume 1 bala.\n\n" +
            "Recoge munición en el mapa para recargar.\n" +
            "Si te quedas sin munición, deberás cambiar de arma\n" +
            "o buscar más recursos."
        ));
        
        content.getChildren().add(createSection(
            "📍 ESCENARIO",
            "Indica en qué etapa del viaje te encuentras:\n\n" +
            "1. Llanuras y Praderas (Fácil)\n" +
            "2. Montañas Rocosas (Medio)\n" +
            "3. Río Columbia (Difícil)\n\n" +
            "Cada escenario tiene mayor dificultad y más enemigos."
        ));
        
        content.getChildren().add(createSection(
            "🎒 INVENTARIO",
            "Muestra todos los items que llevas:\n\n" +
            "• Nombre del item\n" +
            "• Cantidad disponible (x#)\n" +
            "• Tipo (COMIDA, MEDICINA, MUNICION)\n\n" +
            "Capacidad máxima: 10 items diferentes."
        ));
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setStyle("-fx-background: #16213e;");
        scrollPane.setFitToWidth(true);
        
        return scrollPane;
    }
    
    /**
     * Contenido de la pestaña Estrategias
     */
    private ScrollPane createStrategyContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #16213e;");
        
        content.getChildren().add(createSection(
            "🎯 GESTIÓN DE RECURSOS",
            "1. Prioriza la medicina cuando estés bajo de vida\n" +
            "2. Recoge munición antes de enfrentarte a muchos enemigos\n" +
            "3. No desperdicies balas: apunta cuidadosamente\n" +
            "4. Usa el rifle para enemigos lejanos\n" +
            "5. Cambia al revolver cuando te rodeen"
        ));
        
        content.getChildren().add(createSection(
            "⚔ ESTRATEGIAS DE COMBATE",
            "1. Mantén distancia de los enemigos\n" +
            "2. Los enemigos te persiguen cuando estás cerca\n" +
            "3. Elimina enemigos antes de que te alcancen\n" +
            "4. Usa obstáculos del mapa para protegerte\n" +
            "5. Si hay muchos enemigos, retrocede y dispara"
        ));
        
        content.getChildren().add(createSection(
            "🗺 EXPLORACIÓN",
            "1. Explora todo el mapa buscando recursos\n" +
            "2. Los recursos aparecen aleatoriamente\n" +
            "3. Avanza al siguiente escenario cuando estés preparado\n" +
            "4. Cada escenario tiene un punto de transición\n" +
            "5. No puedes volver a escenarios anteriores"
        ));
        
        content.getChildren().add(createSection(
            "💡 CONSEJOS AVANZADOS",
            "• Los enemigos aparecen lejos de ti (radio seguro)\n" +
            "• Hay un límite de enemigos por escenario\n" +
            "• Usa la medicina estratégicamente (solo +1 vida)\n" +
            "• El inventario completo te impide recoger items\n" +
            "• Descarta items menos importantes si es necesario"
        ));
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setStyle("-fx-background: #16213e;");
        scrollPane.setFitToWidth(true);
        
        return scrollPane;
    }
    
    /**
     * Contenido de la pestaña Escenarios
     */
    private ScrollPane createScenariosContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #16213e;");
        
        content.getChildren().add(createSection(
            "1️⃣ LLANURAS Y PRADERAS",
            "Dificultad: FÁCIL\n" +
            "Enemigos máximos: 8\n" +
            "Recursos: Abundantes\n\n" +
            "Descripción:\n" +
            "La primera etapa de tu viaje. Terreno abierto y llano\n" +
            "con hierba verde. Aquí aprenderás los controles básicos\n" +
            "y te familiarizarás con el combate.\n\n" +
            "Consejo: Recoge todos los recursos que puedas antes\n" +
            "de avanzar al siguiente escenario."
        ));
        
        content.getChildren().add(createSection(
            "2️⃣ MONTAÑAS ROCOSAS",
            "Dificultad: MEDIO\n" +
            "Enemigos máximos: 12\n" +
            "Recursos: Escasos\n\n" +
            "Descripción:\n" +
            "Terreno montañoso y difícil. Los enemigos son más\n" +
            "agresivos y aparecen con mayor frecuencia.\n" +
            "El terreno marrón y rocoso dificulta la visibilidad.\n\n" +
            "Consejo: Conserva munición. No dispares sin estar\n" +
            "seguro del impacto."
        ));
        
        content.getChildren().add(createSection(
            "3️⃣ RÍO COLUMBIA",
            "Dificultad: DIFÍCIL\n" +
            "Enemigos máximos: 15\n" +
            "Recursos: Muy escasos\n\n" +
            "Descripción:\n" +
            "La etapa final cerca del Valle de Willamette.\n" +
            "Terreno azulado cerca del río. Los enemigos\n" +
            "son numerosos y agresivos.\n\n" +
            "Consejo: Esta es la prueba final. Usa todas tus\n" +
            "habilidades y estrategias aprendidas."
        ));
        
        content.getChildren().add(createSection(
            "🏆 VICTORIA",
            "Para ganar el juego, debes:\n\n" +
            "✓ Sobrevivir los 3 escenarios\n" +
            "✓ Llegar al Valle de Willamette\n" +
            "✓ Mantener al menos 1 punto de vida\n\n" +
            "¡Buena suerte en tu travesía!"
        ));
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setStyle("-fx-background: #16213e;");
        scrollPane.setFitToWidth(true);
        
        return scrollPane;
    }
    
    /**
     * Crea una sección del manual con título y contenido
     */
    private VBox createSection(String title, String content) {
        VBox section = new VBox(10);
        section.setStyle(
            "-fx-background-color: #1a1a2e;" +
            "-fx-border-color: #d2691e;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 20;"
        );
        
        Text titleText = new Text(title);
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleText.setFill(Color.web("#f4a460"));
        
        Text contentText = new Text(content);
        contentText.setFont(Font.font("Arial", 16));
        contentText.setFill(Color.LIGHTGRAY);
        contentText.setWrappingWidth(1100);
        
        TextFlow textFlow = new TextFlow(contentText);
        
        section.getChildren().addAll(titleText, textFlow);
        
        return section;
    }
    
    private Button createBackButton() {
        Button button = new Button("⬅ VOLVER AL MENÚ");
        button.setPrefWidth(250);
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
