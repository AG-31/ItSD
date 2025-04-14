package redis.eventsystem;

public abstract class Event {
    boolean cancelled = false;

    boolean isCancelled() {
        return cancelled;
    }

    void setCancelled() {
        cancelled = true;
    }

}
