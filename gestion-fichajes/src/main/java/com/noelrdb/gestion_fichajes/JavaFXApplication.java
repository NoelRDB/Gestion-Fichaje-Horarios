package com.noelrdb.gestion_fichajes;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.noelrdb.gestion_fichajes.javafx.StageManager;

public class JavaFXApplication extends Application {

    private ConfigurableApplicationContext springContext;
    private StageManager stageManager;

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(GestionFichajesApplication.class)
                .run();
        stageManager = springContext.getBean(StageManager.class);
    }

    @Override
    public void start(Stage primaryStage) {
        stageManager.setPrimaryStage(primaryStage);
        stageManager.switchScene(FxmlView.LOGIN);
    }

    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
