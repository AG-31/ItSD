package redis.eventsystem;

import io.lettuce.core.api.async.RedisAsyncCommands;

public abstract class Listener {
    public Listener(String streamName, RedisAsyncCommands<String, String> connection, RedisEventManager eventManager) {
        this.streamName = streamName;
        this.connection = connection;
        this.eventManager = eventManager;
    }
    protected String streamName;
    protected RedisAsyncCommands<String, String> connection;
    protected RedisEventManager eventManager;

}
