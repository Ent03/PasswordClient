package fi.samppa.client;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;



public class DeleteConfirmScene extends CustomScene<VBox> {
    private String name;

    public DeleteConfirmScene(String name) {
        super(new Stage(), new VBox(), 200, 100);
        this.name = name;
    }

    @Override
    public void build() {
        getTypeRoot().setAlignment(Pos.CENTER);
        getStage().initStyle(StageStyle.UNDECORATED);
        getStylesheets().add("stylesheets/Main.css");
        HBox hBox = new HBox();
        hBox.setMinHeight(80);
        addAllNodes(new CustomTitleBar("Delete " + name+"?", getStage()));

        Button yes = addIdentifiedNode("yes", new Button("Yes"));
        Button no = new Button("No");

        no.setOnAction(e -> {
            getStage().close();
        });

        hBox.getChildren().addAll(yes,no);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);

        getTypeRoot().getChildren().addAll(hBox);

        getStage().setScene(this);
        getStage().show();
    }
}
