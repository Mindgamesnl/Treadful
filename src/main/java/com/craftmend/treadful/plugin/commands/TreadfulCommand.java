package com.craftmend.treadful.plugin.commands;

import com.craftmend.treadful.plugin.menu.TreadfulHomeMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TreadfulCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            if (commandSender.isOp()) new TreadfulHomeMenu().openFor((Player) commandSender);
        }
        return true;
    }
}
