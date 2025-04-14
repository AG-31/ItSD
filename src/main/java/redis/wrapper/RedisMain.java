package redis.wrapper;

import io.lettuce.core.*;
import io.lettuce.core.api.async.RedisAsyncCommands;
import redis.eventsystem.Listener;
import redis.eventsystem.RedisEventManager;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class RedisMain {
    /**
     * name - как клиент так и название stream что будут использоваться
     */
    String host, name, password;
    int port;
    private ConnectionWrapper connectionWrapper;
    private RedisEventManager eventManager = RedisEventManager.getInstance();
    private final Map<String, Listener> listeners = new HashMap<String, Listener>();


    public RedisMain(String host, int port, String name, String password) {
        this.host = host;
        this.port = port;
        this.name = name;
        this.password = password;
        init();
    }

    public RedisMain(String host, int port) {
        this.host = host;
        this.port = port;
        this.name = "default";
        this.password = "";
        init();
    }

    private void init() {
        connectionWrapper = new ConnectionWrapper(RedisURI.Builder.
                redis(host, port).
                withAuthentication(name, password).
                build(), name);
    }

    public RedisEventManager getEventManager() {
        return eventManager;
    }

    public void subscribeListener(String channel, Class<? extends Listener> listener) {
        if (listeners.containsKey(channel)) {
            throw new RuntimeException("listener already exists");
        }
        try {
            Constructor<? extends Listener> constructor;
            constructor = listener.getConstructor(String.class, RedisAsyncCommands.class, RedisEventManager.class);

            listeners.put(channel,constructor.newInstance(channel, connectionWrapper.connection(), eventManager));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void unsubscribeListener(String channel) {

    }


    public ConnectionWrapper connectionWrapper() {
        return connectionWrapper;
    }
}
