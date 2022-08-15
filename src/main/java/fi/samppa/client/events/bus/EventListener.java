package fi.samppa.client.events.bus;

public interface EventListener<T extends Event> {
    void handle(T event);
}
