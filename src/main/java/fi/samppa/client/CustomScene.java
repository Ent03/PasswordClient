package fi.samppa.client;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.HashMap;

public abstract class CustomScene<P extends Pane> extends Scene {
    private Stage stage;

    private HashMap<String, Node> nodes = new HashMap<>();

    public CustomScene(Stage stage, P root, int width, int height) {
        super(root, width, height);
        this.stage = stage;
    }

    public CustomScene(Stage stage, P root) {
        super(root);
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    public <T extends Node> T addIdentifiedNode(String id, Node node){
        nodes.put(id, node);
        getTypeRoot().getChildren().add(node);
        return (T) node;
    }

    public <T extends Node> T createIdentifiedNode(String id, Node node){
        nodes.put(id, node);
        return (T) node;
    }

    public boolean hasIdNode(String id){
        return nodes.containsKey(id);
    }

    public void addAllNodes(Node... nodeArr){
        getTypeRoot().getChildren().addAll(nodeArr);
    }

    public <T extends Node> T getNode(String id){
        return (T) nodes.get(id);
    }

    public <T extends Node> T getNode(String id, Class<T> tClass){
        return (T) nodes.get(id);
    }

    public P getTypeRoot(){
        return (P) getRoot();
    }

    public abstract void build();
}
