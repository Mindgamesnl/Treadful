package com.craftmend.treadful.plugin.listeners;

import com.craftmend.treadful.Treadful;
import com.craftmend.treadful.plugin.enums.Message;
import com.craftmend.treadful.world.WorldFactory;
import com.craftmend.treadful.world.implementation.WorldImplementation;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

public class WorldLoadListener implements Listener {

    private Treadful main;

    public WorldLoadListener(Treadful main) {
        this.main = main;
    }

    @EventHandler
    public void onLoad(WorldInitEvent event) {
        Bukkit.getScheduler().runTaskLater(main, () -> {
            Message.toOp(Message.PREFIX.getMessage() + "Injecting into world " + event.getWorld().getName());
            new WorldFactory().overwriteWorld((CraftWorld) event.getWorld(), WorldImplementation.class);
        }, 20 * 3);
    }

}
