package com.craftmend.treadful.command.listeners;

import com.craftmend.treadful.command.CommandMiddleware;

public class CommandListener {

    private CommandMiddleware commandMiddleware;

    public CommandListener(CommandMiddleware commandMiddleware) {
        this.commandMiddleware = commandMiddleware;
    }

}
