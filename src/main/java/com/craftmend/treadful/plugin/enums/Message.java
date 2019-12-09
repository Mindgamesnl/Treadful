package com.craftmend.treadful.plugin.enums;

import org.bukkit.ChatColor;

public enum Message {

    PREFIX(ChatColor.RED + "[" + ChatColor.DARK_RED + "Treadful" + ChatColor.RED + "] " + ChatColor.AQUA);

    public String getMessage() {
        return message;
    }

    private String message;
    Message(String s) {
        message = s;
    }
}
