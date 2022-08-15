package fi.samppa.client.events;

import fi.samppa.client.ServerConnection;
import fi.samppa.client.encryption.AESSecurityCap;
import fi.samppa.client.events.bus.Event;

public class KeyExhangeCompletedEvent extends ConnectionEvent {
    public KeyExhangeCompletedEvent(ServerConnection connection){
        super(connection);
    }
}
