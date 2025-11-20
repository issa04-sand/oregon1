package ui;

import javafx.application.Application;
import javafx.stage.Stage;
import view.MainMenuView;

/**
 * Main - Clase principal de la aplicación
 * Punto de entrada del programa Oregon Trail Survival
 * 
 * Patrón: MVC (Model-View-Controller)
 * Esta clase inicia la aplicación JavaFX y muestra el menú principal
 */
public class Main extends Application {
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("Oregon Trail Survival - 1848");
            primaryStage.setWidth(1280);
            primaryStage.setHeight(720);
            primaryStage.setResizable(false);
            
            // Mostrar el menú principal
            MainMenuView mainMenu = new MainMenuView(primaryStage);
            mainMenu.show();
            
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
