package redis.eventsystem;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RedisEventManager {

    private static class Handler {
        final Object listener;
        final Method method;
        final String group;
        final EventPriority priority;
        final boolean ignoreCancelled;

        Handler(Object listener, Method method, String group, EventPriority priority, boolean ignoreCancelled) {
            this.listener = listener;
            this.method = method;
            this.group = group;
            this.priority = priority;
            this.ignoreCancelled = ignoreCancelled;
        }
    }
    private static final RedisEventManager INSTANCE = new RedisEventManager();

    private RedisEventManager() {}
//todo singleton стал редуцентом
    public static RedisEventManager getInstance() {
        return INSTANCE;
    }

    private final Map<Class<? extends Event>, List<Handler>> handlers = new ConcurrentHashMap<>();

    public void register(Object listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(RedisEventHandler.class) &&
                    method.getParameterCount() == 1 &&
                    Event.class.isAssignableFrom(method.getParameterTypes()[0])) {

                RedisEventHandler annotation = method.getAnnotation(RedisEventHandler.class);
                Class<? extends Event> eventClass = method.getParameterTypes()[0].asSubclass(Event.class);

                handlers.computeIfAbsent(eventClass, k -> new ArrayList<>())
                        .add(new Handler(listener, method, annotation.group(), annotation.priority(), annotation.ignoreCancelled()));

                handlers.get(eventClass).sort(Comparator.comparing((Handler h) -> h.priority.ordinal())
                        .thenComparingInt(handlers.get(eventClass)::indexOf));
            }
        }
    }

    public void unregisterGroup(String group) {
        for (List<Handler> handlerList : handlers.values()) {
            handlerList.removeIf(handler -> handler.group.equals(group));
        }
    }

    public void callEvent(Event event) {
        List<Handler> eventHandlers = handlers.get(event.getClass());
        if (eventHandlers == null) return;

        for (Handler handler : eventHandlers) {
            if (event.isCancelled() && handler.ignoreCancelled) continue;
            try {
                handler.method.invoke(handler.listener, event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
