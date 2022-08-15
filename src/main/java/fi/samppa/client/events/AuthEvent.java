package fi.samppa.client.events;


import fi.samppa.client.ServerConnection;
import fi.samppa.client.events.bus.Event;

import javax.annotation.Nullable;

public class AuthEvent extends ConnectionEvent {
    private AuthStatus status;
    private String salt, password;

    public AuthEvent(ServerConnection connection, AuthStatus status, @Nullable String salt, @Nullable String password){
        super(connection);
        this.status = status;
        this.salt = salt;
        this.password = password;
    }

    public @Nullable String getPassword() {
        return password;
    }

    public @Nullable String getSalt() {
        return salt;
    }

    public AuthStatus getStatus() {
        return status;
    }

    public enum AuthStatus {
        OK,FAILED,ALREADY_REGISTERED;
    }

}
