package fi.samppa.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class CustomTitleBar extends ToolBar {
    private double xOffset = 0;
    private double yOffset = 0;
    private int height = 20;
    private static boolean maximized = false;
    private Label title;
    private Stage stage;

    static class WindowButtons extends AnchorPane {

        public WindowButtons(Stage stage, int height, Label title) {
            setPrefHeight(height);
            minWidthProperty().bind(stage.widthProperty());

            Circle circle = new Circle();
            circle.setId("exit-circle");
            circle.setRadius(5);
            Circle circle2 = new Circle();
            circle2.setId("fullscreen-circle");
            circle2.setRadius(5);
            Circle circle3 = new Circle();
            circle3.setId("minimize-circle");
            circle3.setRadius(5);

            AnchorPane.setLeftAnchor(title, -5.0);
            AnchorPane.setTopAnchor(title, 0.0);

            AnchorPane.setRightAnchor(circle, 10.0);
            AnchorPane.setTopAnchor(circle, 5.0);


            AnchorPane.setRightAnchor(circle2, 30.0);
            AnchorPane.setTopAnchor(circle2, 5.0);


            AnchorPane.setRightAnchor(circle3, 50.0);
            AnchorPane.setTopAnchor(circle3, 5.0);


            circle.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    stage.close();
                }
            });

            circle2.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    stage.setMaximized(maximized = !maximized);
                }
            });

            circle3.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    stage.setIconified(true);
                }
            });

            this.getChildren().add(title);
            this.getChildren().add(circle3);
            this.getChildren().add(circle2);
            this.getChildren().add(circle);
        }
    }

    public CustomTitleBar(String title, Stage stage){
        this.title = new Label(title);
        this.stage = stage;
        getStyleClass().add("title-bar");
        setPrefHeight(height);
        setMinHeight(height);
        setMaxHeight(height);
        getItems().add(new WindowButtons(stage, height, this.title));
        fxmlListener();
    }

    public void setTitle(String text){
        this.title.setText(text);
    }

    public String getTitle() {
        return title.getText();
    }

    private void fxmlListener(){
        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });


        setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
    }

}
