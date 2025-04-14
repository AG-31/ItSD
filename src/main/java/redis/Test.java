package redis;

import redis.eventsystem.RedisEventHandler;
import redis.eventsystem.events.StreamEvent;
import redis.eventsystem.listeners.StreamListener;
import redis.wrapper.RedisMain;

import static redis.wrapper.Config.redisHost;
import static redis.wrapper.Config.redisPort;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        RedisMain redis = new RedisMain(redisHost,redisPort);
        redis.subscribeListener("test", StreamListener.class);
        redis.getEventManager().register(new Test());
        redis.connectionWrapper().stream().add("test", "da", "pososi");
    }

    @RedisEventHandler(group = "test")
    public static void eventTest(StreamEvent event) {
        System.out.println(event.getMessage());
    }
}
