package fi.samppa.client;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fi.samppa.client.encryption.AESSecurityCap;
import fi.samppa.client.events.AuthEvent;
import fi.samppa.client.events.KeyExhangeCompletedEvent;
import fi.samppa.client.events.PasswordDataReceivedEvent;
import fi.samppa.client.events.SessionKeyReceivedEvent;
import fi.samppa.client.events.bus.Event;
import fi.samppa.client.events.bus.EventBus;
import javafx.application.Platform;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class ServerConnection extends Thread{
    private final Socket serverSocket;
    private final DataOutputStream dOut;
    private final DataInputStream dIn;

    private boolean keyExchangeCompleted, authenticated;

    private final AESSecurityCap aesSecurityCap;

    private TextEncryptor textEncryptor; //used to encrypt/decrypt local files

    public ServerConnection(Socket socket) throws IOException {
        this.serverSocket = socket;
        this.dOut = new DataOutputStream(serverSocket.getOutputStream());
        this.dIn = new DataInputStream(serverSocket.getInputStream());
        this.aesSecurityCap = new AESSecurityCap();
    }

    public static ServerConnection createConnection(String hostname, int port){
        try {
            Socket socket = new Socket(hostname, port);
            return new ServerConnection(socket);
        }
        catch (IOException e){
            return null;
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public TextEncryptor getTextEncryptor() {
        return textEncryptor;
    }

    public boolean isConnected(){
        return serverSocket.isConnected();
    }

    public Socket getSocket(){
        return serverSocket;
    }

    public AESSecurityCap getAesSecurityCap() {
        return aesSecurityCap;
    }

    public void setKeyExchangeCompleted(boolean keyExchangeCompleted) {
        this.keyExchangeCompleted = keyExchangeCompleted;
    }

    public boolean isKeyExchangeCompleted() {
        return keyExchangeCompleted;
    }

    public void requestKey(){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("sendkey");
        sendData(out.toByteArray());
    }


    public void sendInstruction(String action){
        ByteArrayDataOutput input = ByteStreams.newDataOutput();
        input.writeUTF("instruction");
        input.writeUTF(action);
        sendData(input.toByteArray());
    }


    public void sendKey(){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("receivekey");
        out.writeUTF(AESSecurityCap.keyToString(aesSecurityCap.getPublickey()));
        sendData(out.toByteArray());
    }

    public void sendData(byte[] data){
        try {
            if(isKeyExchangeCompleted()) data = aesSecurityCap.encryptBytes(data);
            dOut.writeInt(data.length);
            dOut.write(data);
            dOut.flush();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean isClosed(){
        return serverSocket.isClosed();
    }

    public void close() throws IOException {
        serverSocket.close();
    }

    public void attemptLogin(String username, String password, boolean register){
        ByteArrayDataOutput input = ByteStreams.newDataOutput();
        input.writeUTF(!register ? "authentication" : "registration");
        input.writeUTF(username);
        input.writeUTF(password);
        sendData(input.toByteArray());
    }

    public void requestNewSession(String sessionID){
        ByteArrayDataOutput input = ByteStreams.newDataOutput();
        input.writeUTF("create-session");
        input.writeUTF(sessionID);
        sendData(input.toByteArray());
    }

    public void requestSessionKey(String username, String sessionID){
        ByteArrayDataOutput input = ByteStreams.newDataOutput();
        input.writeUTF("session-key");
        input.writeUTF(sessionID);
        input.writeUTF(username);
        sendData(input.toByteArray());
    }

    public void sendPassword(String password, String username, String site){
        ByteArrayDataOutput input = ByteStreams.newDataOutput();
        input.writeUTF("save-password");
        input.writeUTF(textEncryptor.encrypt(username));
        input.writeUTF(textEncryptor.encrypt(password));
        input.writeUTF(textEncryptor.encrypt(site));
        sendData(input.toByteArray());
    }

    public void deletePassword(PasswordData passwordData){
        ByteArrayDataOutput input = ByteStreams.newDataOutput();
        input.writeUTF("delete-password");
        input.writeUTF(passwordData.uuid.toString());
        sendData(input.toByteArray());
    }

    public void handleData(byte[] data) {
        if(isKeyExchangeCompleted()){
            data = getAesSecurityCap().decryptBytes(data);
        }

        ByteArrayDataInput input = ByteStreams.newDataInput(data);
        String channel = input.readUTF();
        System.out.println("received channel " + channel);

        if(channel.equals("key")){
            //setting the public key
            getAesSecurityCap().setReceiverPublicKey(AESSecurityCap.keyFromString(input.readUTF()));
            System.out.println("Key set");
            sendKey();
            System.out.println("Sent own key");
            setKeyExchangeCompleted(true);
            EventBus.send(new KeyExhangeCompletedEvent(this));
        }
        else if(channel.equals("message")){
            String msg = input.readUTF();
            //addNewLine(msg, "Server");
            System.out.println("Message: " + msg);
        }
        else if(channel.equals("authentication")){
            String status = input.readUTF();
            AuthEvent.AuthStatus authStatus = AuthEvent.AuthStatus.valueOf(status);
            String salt = null;
            String password = null;
            if(authStatus == AuthEvent.AuthStatus.OK){
                salt = input.readUTF();
                password = input.readUTF();
                textEncryptor = Encryptors.text(password, salt);
                setAuthenticated(true);
            }
            EventBus.send(new AuthEvent(this, authStatus, salt, password));
        }
        else if(channel.equals("session-key")){
            String salt = input.readUTF();
            String key = input.readUTF();
            EventBus.send(new SessionKeyReceivedEvent(this, salt, key));
        }
        else if(channel.equals("password-data")){
            String password = textEncryptor.decrypt(input.readUTF());
            String user = textEncryptor.decrypt(input.readUTF());
            String site = textEncryptor.decrypt(input.readUTF());
            UUID uuid = UUID.fromString(input.readUTF());

            EventBus.send(new PasswordDataReceivedEvent(this, new PasswordData(password, user, site, uuid)));
        }
    }

    public void run() {
        requestKey();
        while (!isClosed()){
            try {
                int length = dIn.readInt();
                if(length == 0) continue;
                byte[] message = new byte[length];
                dIn.readFully(message, 0, message.length);
                handleData(message);
            }
            catch (IOException e) {
                try {
                    close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
//                reset();
//                sendBytes("reset".getBytes());
            }
        }
    }
}
