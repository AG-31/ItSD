import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import java.util.Map;

public class LettuceRedisStreamExample {
    public static void main(String[] args) throws InterruptedException {
        // Подключение к Redis
        RedisClient client = RedisClient.create("redis://127.0.0.1:6500");
        //RedisClient client = RedisClient.create("redis://senko.netcraze.club:6500");
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
            //Thread.sleep(500);
        }

        //List<StreamMessage<String, String>> messages = commands.xread(
        //        XReadArgs.Builder.block(0).count(1),
        //        XReadArgs.StreamOffset.from(streamName, "0-0")
        //);
//
        //if (!messages.isEmpty()) {
        //    StreamMessage<String, String> firstMessage = messages.get(0);
        //    System.out.println("\nПервая запись в потоке:");
        //    System.out.println("ID: " + firstMessage.getId());
        //    System.out.println("Данные:");
        //    firstMessage.getBody().forEach((key, value) ->
        //            System.out.println("  " + key + ": " + value)
        //    );
        //} else {
        //    System.out.println("Поток пуст");
        //}
//
        connection.close();
        client.shutdown();
    }
}