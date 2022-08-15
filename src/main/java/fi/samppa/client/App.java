package fi.samppa.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.Arrays;


public class App extends Application{
    private Handler handler;

    public App(){
        this.handler = new Handler();
    }

    public void run(){
        launch();
    }

    @Override
    public void start(Stage stage) {
        Platform.setImplicitExit(false);
        stage.initStyle(StageStyle.UNDECORATED);

        handler.init(stage);

        stage.setOnCloseRequest(e -> {
            System.exit(0);
        });
    }

}
