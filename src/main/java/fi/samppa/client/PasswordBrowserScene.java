package fi.samppa.client;

import fi.samppa.client.events.DeleteButtonPressed;
import fi.samppa.client.events.bus.Event;
import fi.samppa.client.events.bus.EventBus;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.lang3.StringUtils;


import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

public class PasswordBrowserScene extends CustomScene<VBox> {
    private List<PasswordData> data = new ArrayList<>();
    private ServerConnection connection;
    public PasswordBrowserScene(ServerConnection connection) {
        super(new Stage(), new VBox(), 540,640);
        this.connection = connection;
    }

    public void setData(List<PasswordData> data) {
        this.data = data;
    }

    public List<PasswordData> getData() {
        return data;
    }

    private Label getCopiableLabel(String text, boolean censor){
        Label label = new Label(!censor ? text : StringUtils.repeat("*", text.length()));
        label.getStyleClass().add("password-field");
        label.setTooltip(new Tooltip("Click to copy"));
        label.setMinWidth(180);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setOnMouseClicked(e -> {
            Utils.copyToClipboard(text);
        });
        return label;
    }

    public HBox getPasswordLine(PasswordData data){
        HBox parent = new HBox();
        parent.getStyleClass().add("password-line");
        HBox box = new HBox();
        box.getStyleClass().add("password-line");
        box.setSpacing(20);

        Button delButton = new Button("X");
        delButton.setOnAction(e -> {
            DeleteConfirmScene confirmScene = new DeleteConfirmScene(data.site);
            confirmScene.build();
            confirmScene.getNode("yes", Button.class).setOnAction(de -> {
                EventBus.send(new DeleteButtonPressed(data));
                confirmScene.getStage().close();
                this.data.remove(data);
                reConstructPasswordView(this.data);
            });
        });
        Label user = getCopiableLabel(data.username, false);
        Label pass = getCopiableLabel(data.password, true);
        Label site = getCopiableLabel(data.site, false);
        box.getChildren().addAll(site, user, pass);

        parent.getChildren().addAll(delButton, box);
        return parent;
    }

    public void addPasswordLine(PasswordData data){
        HBox box = getPasswordLine(data);
        getNode("content-holder", VBox.class).getChildren().add(box);
    }

    public void reConstructPasswordView(List<PasswordData> newData){
        VBox vBox = getNode("content-holder");
        vBox.getChildren().clear();
        for(PasswordData data : newData){
            addPasswordLine(data);
        }
        Button button = new Button("Add new");
        button.minWidthProperty().bind(getStage().widthProperty());
        vBox.getChildren().add(button);
        button.setOnAction(e -> {
            PasswordSaveScene scene = new PasswordSaveScene();
            scene.build();
            scene.getNode("save", Button.class).setOnAction(b -> {
                scene.save(connection);
            });
        });
    }

    @Override
    public void build() {
        getStage().initStyle(StageStyle.UNDECORATED);
        getStylesheets().add("stylesheets/browser.css");
        getStylesheets().add("stylesheets/titlebar.css");

        addAllNodes(new CustomTitleBar("Browse Passwords", getStage()));

        VBox vBox = createIdentifiedNode("content-holder", new VBox());
        vBox.setSpacing(5);
        ScrollPane scrollPane = addIdentifiedNode("scroll-pane", new ScrollPane(vBox));

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setMinHeight(600-20);
        scrollPane.setFitToWidth(true);

        scrollPane.getStyleClass().add("scrollpane");


        reConstructPasswordView(data);
        VBox searchArea = new VBox();
        searchArea.setSpacing(10);

        TextField searchField = addIdentifiedNode("search", new TextField("Search"));
        searchField.setMinHeight(40);
        searchArea.getChildren().add(searchField);

        addAllNodes(searchArea);

        searchField.setOnKeyReleased(e -> {
            List<PasswordData> matches = new ArrayList<>(data);
            matches.removeIf(d -> !d.site.startsWith(searchField.getText()));
            reConstructPasswordView(matches);
        });

        getStage().setScene(this);
        getStage().show();
    }
}
