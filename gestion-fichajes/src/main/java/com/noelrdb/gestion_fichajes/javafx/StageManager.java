package com.noelrdb.gestion_fichajes.javafx;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.noelrdb.gestion_fichajes.FxmlView;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@Component
public class StageManager {

    private final ApplicationContext springContext;
    private Stage primaryStage;

    public StageManager(ApplicationContext springContext) {
        this.springContext = springContext;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void switchScene(FxmlView view) {
        Parent viewRootNodeHierarchy = loadViewNodeHierarchy(view.getFxmlFile());
        show(viewRootNodeHierarchy, view.getTitle());
    }

    private void show(Parent rootNode, String title) {
        Scene scene = prepareScene(rootNode);
        
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private Scene prepareScene(Parent rootNode) {
        Scene scene = primaryStage.getScene();

        if (scene == null) {
            scene = new Scene(rootNode);
        }
        scene.setRoot(rootNode);
        return scene;
    }

    private Parent loadViewNodeHierarchy(String fxmlFilePath) {
        Parent rootNode = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilePath));
            loader.setControllerFactory(springContext::getBean);
            rootNode = loader.load();
        } catch (IOException exception) {
            System.err.println("Error loading FXML file: " + fxmlFilePath);
            exception.printStackTrace();
            throw new RuntimeException("Failed to load FXML: " + fxmlFilePath, exception);
        } catch (Exception exception) {
            System.err.println("Error loading view: " + fxmlFilePath);
            exception.printStackTrace();
            throw new RuntimeException("Failed to load view: " + fxmlFilePath, exception);
        }
        return rootNode;
    }
}
