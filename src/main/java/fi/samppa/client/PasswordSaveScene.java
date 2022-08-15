package fi.samppa.client;

import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PasswordSaveScene extends CustomScene<VBox> {
    private boolean saved = false;
    public PasswordSaveScene() {
        super(new Stage(), new VBox(), 300, 200);
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public boolean isSaved() {
        return saved;
    }

    public void save(ServerConnection connection){
        if(isSaved()) return ;
        String user = getNode("username", TextField.class).getText();
        String pass = getNode("password", TextField.class).getText();
        String site = getNode("site", TextField.class).getText();
        connection.sendPassword(pass, user, site);
        Label saved = new Label("Saved!");
        saved.getStyleClass().add("custom-label");
        setSaved(true);
        getNode("button-pane", HBox.class).getChildren().add(saved);
    }

    @Override
    public void build() {
        getStage().initStyle(StageStyle.UNDECORATED);
        getStylesheets().add("stylesheets/Main.css");

        VBox vBox = new VBox();
        vBox.setMinHeight(180);
        addAllNodes(new CustomTitleBar("Save a new password", getStage()));

        TextField username = createIdentifiedNode("username", new TextField("Username"));
        TextField password = createIdentifiedNode("password", new TextField("Password"));
        TextField site = createIdentifiedNode("site", new TextField("Site"));

        HBox buttonPane = createIdentifiedNode("button-pane", new HBox());
        buttonPane.setSpacing(10);
        buttonPane.setAlignment(Pos.CENTER);

        Button save = createIdentifiedNode("save", new Button("Save"));
        buttonPane.getChildren().add(save);

        //getTypeRoot().add(username, 0, 1);

        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setSpacing(10);

        vBox.getChildren().addAll(username, password, site, buttonPane);

        vBox.getStyleClass().add("password-background");
        addIdentifiedNode("vbox", vBox);
        getStage().setScene(this);
        getStage().show();
    }
}
