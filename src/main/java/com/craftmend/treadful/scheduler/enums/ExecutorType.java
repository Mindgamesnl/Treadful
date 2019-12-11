package com.craftmend.treadful.scheduler.enums;

import org.bukkit.ChatColor;

public enum ExecutorType {

    DEFAULT(1, "Default", "The normal minecraft scheduler." + ChatColor.RED + " WARNING! CHANGING THREADING MODE TO ANYTHING OTHER THAN DEFAULT MAY CAUSE PROBLEMS"),
    FORCE_ASYNC(2, "Force async", "Forces all the plugin tasks to be asynchronous"),
    ISOLATED(3, "Isolated", "Runs the plugin in its own thread sandbox.");

    public static final int LOWEST = 1;
    public static final int HIGHEST = 3;

    public static ExecutorType byId(int id) {
        for (ExecutorType value : values()) if (id == value.getLevel()) return value;
        return DEFAULT;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    private int level;
    private String name;
    private String description;

    ExecutorType(int i, String aDefault, String s) {
        this.level = i;
        this.name = aDefault;
        this.description = s;
    }
}
