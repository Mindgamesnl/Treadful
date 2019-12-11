package com.craftmend.treadful.plugin.enums;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public enum Message {

    PREFIX(ChatColor.RED + "[" + ChatColor.DARK_RED + "Treadful" + ChatColor.RED + "] " + ChatColor.AQUA);

    public String getMessage() {
        return message;
    }

    private String message;
    Message(String s) {
        message = s;
    }

    public static void toOp(String message) {
        System.out.println(message);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("treadful.admin") || onlinePlayer.isOp()) onlinePlayer.sendMessage(message);
        }
    }

    public static String[] toLore(String message) {
        List<String> rows = new ArrayList<>();
        int i = 0;
        String part = "";
        for (String word : message.split(" ")) {
            i++;
            part += word + " ";
            if (i == 4) {
                i=0;
                rows.add(part);
                part = "";
            }
        }

        return rows.toArray(new String[rows.size() - 1]);
    }
}
