package cn.hamster3.service.bungee.event;

import cn.hamster3.service.bungee.service.ServiceConnection;
import cn.hamster3.service.bungee.service.ServiceGroup;
import net.md_5.bungee.api.plugin.Cancellable;

public class ServiceGroupReceiveEvent extends ServiceMessageEvent implements Cancellable {
    private final ServiceConnection connection;

    private boolean cancelled;

    public ServiceGroupReceiveEvent(String message, ServiceGroup group, ServiceConnection connection) {
        super(message, group);
        this.connection = connection;
        cancelled = false;
    }

    public ServiceConnection getConnection() {
        return connection;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
