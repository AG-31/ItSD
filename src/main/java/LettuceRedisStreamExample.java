import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import java.util.Map;

public class LettuceRedisStreamExample {
    public static void main(String[] args) throws InterruptedException {
        RedisClient client = RedisClient.create("redis://127.0.0.1:6500");
        StatefulRedisConnection<String, String> connection = client.connect();
        RedisCommands<String, String> commands = connection.sync();

        String streamName = "test";
        String type = commands.type(streamName);
        System.out.println("Тип данных для ключа '" + streamName + "': " + type);
        Map<String, String> fields = new java.util.HashMap<>(Map.of(
                "da", "lo",
                "net", "do"
        ));

        for (int i = 0; i < 1500; i++) {
            fields.put("da", "lo" + i);
            fields.put("net", "do" + i);
            commands.xadd(streamName, fields);
        }
        connection.close();
        client.shutdown();
    }
}