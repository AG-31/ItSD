package redis.eventsystem.events;

import io.lettuce.core.StreamMessage;
import redis.eventsystem.Event;

import java.util.Map;

public class StreamEvent extends Event {
    StreamMessage<String, String> message;


    public StreamEvent(StreamMessage<String, String> message) {
        this.message = message;
    }

    public String getStreamName() {
        return message.getStream();
    }

    public Map<String, String> getMessage() {
        return message.getBody();
    }


}