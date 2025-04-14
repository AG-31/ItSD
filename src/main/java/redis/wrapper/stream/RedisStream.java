package redis.wrapper.stream;

import io.lettuce.core.api.async.RedisAsyncCommands;


public class RedisStream {
    RedisAsyncCommands<String, String> connection;
    String name;

    public RedisStream(RedisAsyncCommands<String, String> connection, String name) {
        this.connection = connection;
        this.name = name;
    }

    public void add(String tableKey, String key, String value) {
        addRecord(tableKey, key, value);
    }

    public void add(String key, String value) {
        addRecord("global", key, value);
    }

    private void addRecord(String name, String key, String value) {
        connection.xadd(name, key, value);
    }
}
