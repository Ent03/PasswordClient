package fi.samppa.client.events;

import fi.samppa.client.PasswordData;
import fi.samppa.client.events.bus.Event;

public class DeleteButtonPressed extends Event {
    private PasswordData data;

    public DeleteButtonPressed(PasswordData data) {
        this.data = data;
    }

    public PasswordData getData() {
        return data;
    }
}
