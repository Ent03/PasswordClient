package fi.samppa.client.events.bus;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EventBus {
    private static Map<Class, List<EventListener>> listeners = new HashMap<>();

    public static void send(Event event){
        Class eventClass = event.getClass();
        if(!listeners.containsKey(eventClass)) return;
        List<EventListener> eventListeners = listeners.get(eventClass);
        for(EventListener e : eventListeners){
            e.handle(event);
        }
    }

    public static <T extends Event> void subscribe(Class<T> eventClass, EventListener<T> listener){
        if(!listeners.containsKey(eventClass)){
            listeners.put(eventClass, new LinkedList<>());
        }
        listeners.get(eventClass).add(listener);
    }
}
