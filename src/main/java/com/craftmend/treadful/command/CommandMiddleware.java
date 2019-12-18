package com.craftmend.treadful.command;

import com.craftmend.treadful.command.executor.MiddleCommandExecutor;
import com.craftmend.treadful.plugin.enums.Message;
import com.craftmend.treadful.util.EventInterceptor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class CommandMiddleware {

    private Set<String> specialCases = new HashSet<>(Arrays.asList("ScriptCommand", "LukkitCommand"));

    private Map<String, MiddleCommandExecutor> routableCommands = new HashMap<>();
    private Set<EventInterceptor> interceptors = new HashSet<>();

    public CommandMiddleware() throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        update();

        EventInterceptor enableListener = EventInterceptor.create(PluginEnableEvent.class);
        enableListener.setMiddleware((e, next) -> {
            PluginEnableEvent event = (PluginEnableEvent) e;
            Message.toOp(Message.PREFIX.getMessage() + "Scanning plugin " + event.getPlugin().getName() + " for commands and baking them a nice warm proxy.");

            update();

            next.run();
        });

        EventInterceptor serverListener = EventInterceptor.create(ServerCommandEvent.class);
        serverListener.setMiddleware((e, next) -> {
            ServerCommandEvent event = (ServerCommandEvent) e;
            String command = null;
            if (event.getCommand().startsWith("/")) {
                command = event.getCommand().substring(1);
            } else {
                command = event.getCommand();
            }

            String[] parts = command.split(" ");
            String[] args = Arrays.copyOfRange(parts, 1, parts.length);

            if (handle(event.getCommand(), event.getSender(), args)) {
                next.run();
            } else {
                event.setCancelled(true);
            }
        });


        EventInterceptor playerListener = EventInterceptor.create(PlayerCommandPreprocessEvent.class);
        playerListener.setMiddleware((e, next) -> {
            PlayerCommandPreprocessEvent event = (PlayerCommandPreprocessEvent) e;

            String command = null;
            if (event.getMessage().startsWith("/")) {
                command = event.getMessage().substring(1);
            } else {
                command = event.getMessage();
            }

            String[] parts = command.split(" ");
            String[] args = Arrays.copyOfRange(parts, 1, parts.length);

            if (handle(parts[0].toLowerCase(), event.getPlayer(), args)) {
                next.run();
            } else {
                event.setCancelled(true);
            }
        });

        interceptors.add(playerListener);
        interceptors.add(serverListener);
        interceptors.add(enableListener);
    }

    public void disable() {
        for (EventInterceptor interceptor : interceptors) {
            interceptor.reset();
        }
    }

    public void update() {
        CraftServer craftServer = (CraftServer) Bukkit.getServer();

        for (Command command : craftServer.getCommandMap().getCommands()) {
            if (command instanceof PluginCommand) {
                PluginCommand pluginCommand = (PluginCommand) command;
                if (!(pluginCommand.getExecutor() instanceof JavaPlugin) ) continue;

                // refactor the fucker
                if (!routableCommands.containsKey(command.getName())) routableCommands.put(command.getName(), new MiddleCommandExecutor(pluginCommand.getPlugin()));
            } else if (specialCases.contains(command.getClass().getSimpleName())) {
                if (!routableCommands.containsKey(command.getName())) {
                    Message.toOp(Message.PREFIX.getMessage() + "Registering Lukkit/Skript command " + command.getName());
                    routableCommands.put(command.getName(), new MiddleCommandExecutor(command));
                }
            } else {
                continue;
            }
        }
    }

    public boolean handle(String commandName, CommandSender commandSender, String[] args) {
        MiddleCommandExecutor executor = routableCommands.get(commandName);
        if (executor == null) return true;

        Command command = Bukkit.getPluginCommand(commandName);

        if (command == null) {
            executor.execute(commandSender, null, commandName, args);
        } else {
            executor.execute(commandSender, command, command.getName(), args);
        }
        return false;
    }

}
