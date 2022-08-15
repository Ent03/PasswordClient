package fi.samppa.client.events;

import fi.samppa.client.ServerConnection;
import fi.samppa.client.events.bus.Event;

public class ConnectionEvent extends Event {
    private ServerConnection serverConnection;
    public ConnectionEvent(ServerConnection serverConnection){
        this.serverConnection = serverConnection;
    }

    public ServerConnection getServerConnection() {
        return serverConnection;
    }
}
