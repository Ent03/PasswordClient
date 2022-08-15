package fi.samppa.client.events;

import fi.samppa.client.PasswordData;
import fi.samppa.client.ServerConnection;

public class PasswordDataReceivedEvent extends ConnectionEvent {
    private PasswordData passwordData;
    public PasswordDataReceivedEvent(ServerConnection connection, PasswordData passwordData){
        super(connection);
        this.passwordData = passwordData;

    }

    public PasswordData getPasswordData() {
        return passwordData;
    }
}
