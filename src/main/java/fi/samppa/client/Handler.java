package fi.samppa.client;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import fi.samppa.client.config.Config;
import fi.samppa.client.events.*;
import fi.samppa.client.events.bus.EventBus;
import fi.samppa.client.events.bus.Listener;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import oshi.SystemInfo;

import java.io.IOException;
import java.util.ArrayList;

public class Handler extends Thread implements Listener {
    private Stage stage;
    private Config config;
    private Config userData;


    private ServerConnection connection;

    private boolean rememberLogin = false;
    private String username, password;

    private ArrayList<PasswordData> passwordData = new ArrayList<>();

    private PasswordBrowserScene passwordBrowserScene;

    public String getProcessorID(){
        return new SystemInfo().getHardware().getComputerSystem().getSerialNumber();
    }

    public void openLoginScene(){
        Platform.runLater(()->{
            LoginScene loginScene = new LoginScene(stage);
            loginScene.build();
            stage.setScene(loginScene);
            stage.show();

            loginScene.getNode("login", Button.class).setOnAction(e -> {
                this.password = loginScene.getNode("password", TextField.class).getText();
                this.username = loginScene.getNode("username", TextField.class).getText();
                boolean register = loginScene.getNode("register", CheckBox.class).isSelected();
                this.rememberLogin = loginScene.getNode("remember", CheckBox.class).isSelected();
                userData.setProperty("remember-login", String.valueOf(rememberLogin));
                connection.attemptLogin(username, password, register);
            });
        });
    }

    public void init(Stage stage){
        this.stage = stage;
        EventBus.subscribe(AuthEvent.class, this::onAuthorizeEvent);
        EventBus.subscribe(KeyExhangeCompletedEvent.class, this::onKeyExchange);
        EventBus.subscribe(SessionKeyReceivedEvent.class, this::onSessionKeyReceive);
        EventBus.subscribe(PasswordDataReceivedEvent.class, this::onPasswordDataReceived);
        EventBus.subscribe(DeleteButtonPressed.class, this::onDelete);
        start();
    }

    public void onAuthorizeEvent(AuthEvent event){
        if(event.getStatus() == AuthEvent.AuthStatus.OK){
            if(rememberLogin){
                String id = KeyGenerators.string().generateKey();
                userData.setProperty("session-id", id);
                userData.setProperty("user", username);
                event.getServerConnection().requestNewSession(id);
                userData.save();
            }
            else {
                Platform.runLater(()->{
                    stage.close();
                    passwordBrowserScene = new PasswordBrowserScene(connection);
                    passwordBrowserScene.build();
                });
            }
            connection.sendInstruction("send-passwords");
            registerShortcuts();
        }
    }

    public void onDelete(DeleteButtonPressed event){
        passwordData.remove(event.getData());
        connection.deletePassword(event.getData());
    }

    public void onPasswordDataReceived(PasswordDataReceivedEvent event){
        passwordData.add(event.getPasswordData());
        if(passwordBrowserScene != null){
            Platform.runLater(()->{
                passwordBrowserScene.reConstructPasswordView(passwordData);
            });
        }
    }

    public void onSessionKeyReceive(SessionKeyReceivedEvent event){
        System.out.println("Received key " + event.getSessionKey());

        TextEncryptor encryptor = Encryptors.text(event.getSessionKey(), event.getSalt());
        if(connection.isAuthenticated()){
            //we encrypt
            userData.setProperty("password", encryptor.encrypt(this.password));
            userData.save();
        }
        else {
            String decryptedPass = encryptor.decrypt(userData.getProperty("password"));
            String username = userData.getProperty("user");
            connection.attemptLogin(username, decryptedPass, false);
        }
    }

    public void onKeyExchange(KeyExhangeCompletedEvent event){
        if(!userData.getBoolean("remember-login")){
            openLoginScene();
        }
        else {

            String username = userData.getProperty("user");
            String id = userData.getProperty("session-id");
            event.getServerConnection().requestSessionKey(username, id);
        }
    }

    private void registerShortcuts(){
        try {
            GlobalScreen.registerNativeHook();
            NativeKeyListener listener = new NativeKeyListener() {
                private boolean ctrlPressed = false;

                @Override
                public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
                    int k = nativeEvent.getKeyCode();

                    if(ctrlPressed && k == 66){ //F8
                        Platform.runLater(()->{
                            PasswordSaveScene scene = new PasswordSaveScene();
                            scene.build();

                            scene.getNode("save", Button.class).setOnAction(e -> {
                                scene.save(connection);
                            });
                        });
                    }
                    else if(ctrlPressed && k == 68){ //F10
                        Platform.runLater(()-> {
                            passwordBrowserScene = new PasswordBrowserScene(connection);
                            passwordBrowserScene.setData(passwordData);
                            passwordBrowserScene.build();
                        });
                    }
                    ctrlPressed = k == 29; //control == 29
                }

                @Override
                public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
                    if(nativeEvent.getKeyCode() == 29) ctrlPressed = false;
                }
            };
            GlobalScreen.addNativeKeyListener(listener);
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        config = Config.initConfig("data/", "config.properties");
        userData = Config.initConfig("data/", "user.properties");
        String host = config.getProperty("server-host");
        int port = config.getInt("server-port");

        connection = ServerConnection.createConnection(host, port);
        if(connection == null || !connection.isConnected()){
            System.out.println("Failed to connect");
            return;
        }
        connection.start();
    }
}
