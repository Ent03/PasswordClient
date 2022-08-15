package fi.samppa.client;

import javafx.scene.text.Text;

public class ChatMessage {
    private int id;
    private Text textComponent;
    public ChatMessage(int id, Text textComponent){
        this.id = id;
        this.textComponent = textComponent;
    }

    public int getId() {
        return id;
    }

    public String getText(){
        return textComponent.getText();
    }

    public Text getTextComponent() {
        return textComponent;
    }

    public void setText(String text){
        textComponent.setText(text);
    }
}
