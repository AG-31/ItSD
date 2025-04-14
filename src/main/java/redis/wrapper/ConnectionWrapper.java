package redis.wrapper;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.RedisException;
import io.lettuce.core.api.async.RedisAsyncCommands;
import redis.wrapper.stream.RedisStream;

public class ConnectionWrapper {
    private final RedisClient client;
    private final RedisURI uri;
    private volatile RedisAsyncCommands<String, String> connection;
    private String name;

    public ConnectionWrapper(RedisURI uri, String name) {
        this.uri = uri;
        this.client = RedisClient.create(uri);
        this.connection = createConnection();
        this.name = name;
    }

    private RedisAsyncCommands<String, String> createConnection() {
        try {
            return client.connect().async();
        } catch (RedisException e) {
            throw new RuntimeException("Не удалось подключиться к Redis при инициализации", e);
        }
    }

    public synchronized RedisAsyncCommands<String, String> connection() {
        if (connection == null || !connection.isOpen()) {
            connection = createConnection();
        }
        return connection;
    }
    public RedisStream stream() {
        return new RedisStream(connection, name);
    }

    public synchronized void close() {
        client.shutdown();
    }

}
