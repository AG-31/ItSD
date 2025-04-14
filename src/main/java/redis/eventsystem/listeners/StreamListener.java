package redis.eventsystem.listeners;

import io.lettuce.core.*;
import io.lettuce.core.api.async.RedisAsyncCommands;
import redis.eventsystem.Listener;
import redis.eventsystem.RedisEventManager;
import redis.eventsystem.events.StreamEvent;
import redis.wrapper.Config;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StreamListener extends Listener {
    private final String[] lastId = new String[1];
    private static final ExecutorService streamScheduler = Executors.newScheduledThreadPool(1);


    public StreamListener(String streamName, RedisAsyncCommands<String, String> connection, RedisEventManager eventManager) {
        super(streamName, connection, eventManager);
        lastId[0] = getLastRecord();
        listener();
    }

    private String getLastRecord() {
        try {
            List<StreamMessage<String, String>> lastMessage = connection.xrevrange(
                    streamName,
                    Range.create("-", "+"),
                    Limit.from(1)).get();//лимит тк нужна лишь 1 запись, в redis априори должна быть 1 запись для существования stream
            return lastMessage.getFirst().getId();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Get last record fail");//todo logger
            throw new RuntimeException(e);//p.s. я хуй знает что должно произойти чтоб случилось
        }
    }


    private void listener() {
        streamScheduler.submit(()-> {
            while (!Thread.currentThread().isInterrupted()) {
                long startTime = System.nanoTime();

                XReadArgs.StreamOffset<String> streamOffset = XReadArgs.StreamOffset.from("test", lastId[0]);

                @SuppressWarnings("unchecked")//похуй, ваще похуй
                RedisFuture<List<StreamMessage<String, String>>> futureMessages = connection.xread(streamOffset);

                List<StreamMessage<String, String>> messages;
                try {
                    messages = futureMessages.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
                if (!messages.isEmpty()) {
                    lastId[0] = messages.getLast().getId();
                    for (StreamMessage<String, String> message : messages) {
                        RedisEventManager.getInstance().callEvent(new StreamEvent(message));
                    }
                }


                long elapsed = System.nanoTime() - startTime;
                long sleepTime = Config.updateIntervalNano - elapsed;
                //System.out.println("elapsed: " + elapsed/1000000 + "; sleepTime: " + sleepTime/1000000);//todo сделать в будущем сводку по среднему времени
                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime/1000000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void terminate() {
        streamScheduler.shutdownNow();
    }

}
