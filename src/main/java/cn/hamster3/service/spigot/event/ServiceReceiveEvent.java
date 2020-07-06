package cn.hamster3.service.spigot.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class ServiceReceiveEvent extends ServiceMessageEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    public ServiceReceiveEvent(String message) {
        super(message);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
