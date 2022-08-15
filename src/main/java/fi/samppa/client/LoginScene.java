package fi.samppa.client;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginScene extends CustomScene<AnchorPane> {
    public LoginScene(Stage stage) {
        super(stage, new AnchorPane(), 640, 480);
    }

    public void build(){
        getTypeRoot().setBackground(new Background(new BackgroundImage(new Image("cloud3.png"), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT)));
        Label info = new Label("Login");
        Label errorInfo = new Label("");
        TextField username = new TextField("Username");
        TextField password = new TextField("Password");
        CheckBox checkBox = new CheckBox("Register");
        CheckBox remember = new CheckBox("Remember Me");
        Button login = new Button("Login");

        errorInfo.getStyleClass().add("error-info");
        getStylesheets().add("stylesheets/login.css");
        getStylesheets().add("stylesheets/main.css");
        info.getStyleClass().add("login-info");
        checkBox.getStyleClass().add("register-button");
        remember.getStyleClass().add("register-button");
        username.getStyleClass().add("input-field");
        password.getStyleClass().add("input-field");


        addAllNodes(info);
        addIdentifiedNode("username", username);
        addIdentifiedNode("password", password);
        addIdentifiedNode("login", login);
        addIdentifiedNode("register", checkBox);
        addIdentifiedNode("remember", remember);
        addIdentifiedNode("error", errorInfo);

        double heightOffset = 50;

        AnchorPane.setBottomAnchor(info, 330.0-heightOffset);
        AnchorPane.setLeftAnchor(info, 230.0);

        AnchorPane.setBottomAnchor(username, 300.0-heightOffset);
        AnchorPane.setLeftAnchor(username, 235.0);
        AnchorPane.setRightAnchor(username, 235.0);

        AnchorPane.setBottomAnchor(password, 265.0-heightOffset);
        AnchorPane.setLeftAnchor(password, 235.0);
        AnchorPane.setRightAnchor(password, 235.0);

        AnchorPane.setBottomAnchor(login, 225.0-heightOffset);
        AnchorPane.setLeftAnchor(login, 280.0);
        AnchorPane.setRightAnchor(login, 280.0);

        AnchorPane.setBottomAnchor(errorInfo, 200.0-heightOffset);
        AnchorPane.setLeftAnchor(errorInfo, 280.0);

        AnchorPane.setBottomAnchor(checkBox, 232.0-heightOffset);
        AnchorPane.setRightAnchor(checkBox, 100.0);
        AnchorPane.setLeftAnchor(checkBox, 375.0);

        AnchorPane.setBottomAnchor(remember, 202.0-heightOffset);
        AnchorPane.setRightAnchor(remember, 100.0);
        AnchorPane.setLeftAnchor(remember, 375.0);

        getTypeRoot().getChildren().add(new CustomTitleBar("Login", getStage()));
    }
}
