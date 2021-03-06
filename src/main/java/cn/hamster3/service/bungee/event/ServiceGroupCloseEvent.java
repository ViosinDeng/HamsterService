package cn.hamster3.service.bungee.event;

import cn.hamster3.service.bungee.service.ServiceGroup;
import net.md_5.bungee.api.plugin.Event;

/**
 * 桥接组关闭事件
 */
public class ServiceGroupCloseEvent extends Event {
    private final ServiceGroup group;

    private final boolean success;
    private Throwable cause;

    public ServiceGroupCloseEvent(ServiceGroup group, boolean success) {
        this.group = group;
        this.success = success;
    }

    public ServiceGroupCloseEvent(ServiceGroup group, boolean success, Throwable cause) {
        this.group = group;
        this.success = success;
        this.cause = cause;
    }

    public ServiceGroup getGroup() {
        return group;
    }

    public boolean isSuccess() {
        return success;
    }

    public Throwable getCause() {
        return cause;
    }
}
