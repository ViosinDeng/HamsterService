package cn.hamster3.service.bungee.service;

import cn.hamster3.service.bungee.BungeeService;
import cn.hamster3.service.bungee.event.ServiceClientDisconnectedEvent;
import cn.hamster3.service.bungee.event.ServiceClientRegisterEvent;
import cn.hamster3.service.bungee.event.ServiceGroupReceiveEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.md_5.bungee.api.ProxyServer;

import java.net.InetSocketAddress;

class ServiceReadHandler extends SimpleChannelInboundHandler<String> {
    private final ServiceGroup group;
    private final ServiceConnection connection;

    public ServiceReadHandler(ServiceGroup group, ServiceConnection connection) {
        this.group = group;
        this.connection = connection;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, String msg) {
        if (!connection.isRegistered()) {
            if (!msg.startsWith("HamsterService:")) {
                connection.disconnect();
                ServiceClientRegisterEvent event = new ServiceClientRegisterEvent(group, connection, false,
                        "未验证服务器信息");
                ProxyServer.getInstance().getPluginManager().callEvent(event);
                return;
            }
        }
        ServiceGroupReceiveEvent event = new ServiceGroupReceiveEvent(msg, group, connection);
        ProxyServer.getInstance().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        if ("HamsterService".equals(event.getTag())) {
            if (executeRegister(event.getMessage())) {
                return;
            }
            String[] args = event.getMessage().split(" ");
            if ("executeConsoleCommand".equals(args[0]) && "global".equals(args[1])) {
                for (ServiceGroup group : ServiceManager.getGroups()) {
                    group.broadcast("HamsterService", event.getMessage());
                }
                return;
            }
        }
        group.broadcast(event.getTag(), event.getMessage());
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) {
        context.close();
        group.removeConnection(connection);
        ServiceClientDisconnectedEvent disconnectionEvent = new ServiceClientDisconnectedEvent(group, connection);
        ProxyServer.getInstance().getPluginManager().callEvent(disconnectionEvent);
        BungeeService.warning("服务器 %s 断开了与 服务组 %s 的连接!", connection.getName(), group.getName());

        if (!connection.isRegistered()) {
            return;
        }
        group.broadcast("HamsterService", "serverDisconnected %s", connection.getName());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        BungeeService.warning("与客户端 %s 的通信中出现了一个错误: ", connection.getName());
        cause.printStackTrace();
    }

    private boolean executeRegister(String message) {
        String[] args = message.split(" ");
        if (args[0].equalsIgnoreCase("register")) {
            if (connection.isRegistered()) {
                return true;
            }
            String serverID = group.getServerID(args[1]);
            if (serverID == null) {
                connection.sendMessage("HamsterService", "registerFailed 服务组未设定密码:" + args[1]);
                connection.disconnect();
                ServiceClientRegisterEvent event = new ServiceClientRegisterEvent(group, connection, false, "服务组未设定该密码");
                ProxyServer.getInstance().getPluginManager().callEvent(event);
                return true;
            }
            for (ServiceConnection serviceConnection : group.getConnections()) {
                if (serviceConnection.getName().equalsIgnoreCase(serverID)) {
                    connection.sendMessage("HamsterService", "registerFailed 其他服务器已使用该ID. 请检查password是否设置正确.");
                    connection.disconnect();
                    return true;
                }
            }
            try {
                InetSocketAddress address = new InetSocketAddress(args[2], Integer.parseInt(args[3]));
                connection.setName(serverID);
                connection.setBukkitPort(address.getPort());
                connection.setBukkitAddress(address.getHostString());
                connection.setRegistered();
                group.addConnection(connection);
                connection.sendMessage("HamsterService", "registered " + serverID + " " + group.getName());
                ProxyServer.getInstance().getPluginManager().callEvent(new ServiceClientRegisterEvent(group, connection, true));
                group.broadcast("HamsterService", "serverRegistered %s", serverID);
            } catch (Exception e) {
                connection.sendMessage("HamsterService", "registerFailed 验证参数不正确");
                connection.disconnect();
                e.printStackTrace();
                ServiceClientRegisterEvent event = new ServiceClientRegisterEvent(group, connection, false,
                        "验证参数不正确");
                ProxyServer.getInstance().getPluginManager().callEvent(event);
            }
            return true;
        }
        return false;
    }
}
