package com.craftmend.treadful.command.executor;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MiddleCommandExecutor {

    private CommandExecutor executor;

    public MiddleCommandExecutor(CommandExecutor plugin) {
        this.executor = plugin;
    }

    public MiddleCommandExecutor(Command setCommand) {
        this.executor = (commandSender, command, s, strings) -> setCommand.execute(commandSender, s, strings);
    }

    public void execute(CommandSender sender, Command command, String label, String[] args) {
        executor.onCommand(sender, command, label, args);
    }

}
