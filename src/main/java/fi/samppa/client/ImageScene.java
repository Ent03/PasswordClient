package fi.samppa.client;

import javafx.application.Platform;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.ByteArrayInputStream;

public class ImageScene extends CustomScene<BorderPane> {
    private String name;
    private ImageView imageView;

    private boolean loaded = false;

    public ImageScene(Stage stage, String name) {
        super(stage, new BorderPane());
        this.name = name;
    }

    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void build() {
        getStage().initStyle(StageStyle.UNDECORATED);

        ProgressIndicator progressIndicator = new ProgressIndicator();

        getTypeRoot().setCenter(progressIndicator);

        getStylesheets().add("stylesheets/Main.css");
        CustomTitleBar titleBar = new CustomTitleBar(name, getStage());
        getTypeRoot().setTop(titleBar);

        getStage().setScene(this);
        getStage().setTitle(name);
        getStage().show();
    }

    public void setImageFromData(byte[] data){
        loaded = true;
        Platform.runLater(()->{
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Image image = new Image(new ByteArrayInputStream(data));
            imageView = new ImageView(image);
            imageView.setStyle("-fx-background-color: BLACK");

            imageView.setCache(true);
            imageView.setPreserveRatio(true);
            imageView.fitWidthProperty().bind(getTypeRoot().widthProperty());
            imageView.fitHeightProperty().bind(getTypeRoot().heightProperty());
            getTypeRoot().setMaxSize(screenSize.width, screenSize.height);
            getTypeRoot().setPrefSize(image.getWidth(), image.getHeight());
            getTypeRoot().setCenter(imageView);
            getStage().sizeToScene();
            getStage().setX(0);
            getStage().setY(0);
        });
    }
}
