package cn.hamster3.service.bungee.command;

import net.md_5.bungee.api.CommandSender;

public abstract class ServiceCommandExecutor {
    public abstract void execute(CommandSender sender, String[] args);
}
