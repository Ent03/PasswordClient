package fi.samppa.client;

import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.lang3.RandomStringUtils;

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

    public void setTextfieldTip(String tip, TextField textField){
        textField.setTooltip(new Tooltip(tip));
        textField.setPromptText(tip);
    }

    @Override
    public void build() {
        getStage().initStyle(StageStyle.UNDECORATED);
        getStylesheets().add("stylesheets/main.css");

        VBox vBox = new VBox();
        vBox.setMinHeight(180);
        addAllNodes(new CustomTitleBar("Save a new password", getStage()));

        TextField username = createIdentifiedNode("username", new TextField(""));


        HBox passwordPane = new HBox();
        passwordPane.setSpacing(10);
        Button randomGen = createIdentifiedNode("randomgen", new Button("Random"));
        TextField password = createIdentifiedNode("password", new TextField(""));

        randomGen.setOnAction(e -> {
            String random = RandomStringUtils.randomAlphabetic(12);
            password.setText(random);
            Utils.copyToClipboard(random);
        });


        passwordPane.getChildren().addAll(password, randomGen);

        TextField site = createIdentifiedNode("site", new TextField(""));

        HBox buttonPane = createIdentifiedNode("button-pane", new HBox());
        buttonPane.setSpacing(10);
        buttonPane.setAlignment(Pos.CENTER);

        Button save = createIdentifiedNode("save", new Button("Save"));
        buttonPane.getChildren().add(save);

        //getTypeRoot().add(username, 0, 1);

        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setSpacing(10);

        vBox.getChildren().addAll(username, passwordPane, site, buttonPane);

        setTextfieldTip("Password", password);
        setTextfieldTip("Username", username);
        setTextfieldTip("Site", site);

        vBox.getStyleClass().add("password-background");
        addIdentifiedNode("vbox", vBox);
        getStage().setScene(this);
        getStage().show();
    }
}
