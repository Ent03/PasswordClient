package fi.samppa.client.events;

import fi.samppa.client.ServerConnection;

public class SessionKeyReceivedEvent extends ConnectionEvent {
    private String sessionKey, salt;
    public SessionKeyReceivedEvent(ServerConnection connection, String salt, String sessionKey){
        super(connection);
        this.salt = salt;
        this.sessionKey = sessionKey;
    }

    public String getSalt() {
        return salt;
    }

    public String getSessionKey() {
        return sessionKey;
    }
}
