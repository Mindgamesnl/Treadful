package com.craftmend.treadful.plugin.tasks;

import com.craftmend.treadful.plugin.enums.Message;
import com.craftmend.treadful.world.implementation.WorldImplementation;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

import java.time.Duration;
import java.time.Instant;

public class WorldWatchdogTask implements Runnable {

    @Override
    public void run() {
        for (World world : Bukkit.getWorlds()) {
            CraftWorld craftWorld = (CraftWorld) world;
            if (craftWorld.getHandle() instanceof WorldImplementation) {
                WorldImplementation wi = (WorldImplementation) craftWorld.getHandle();

                if (Duration.between(wi.getLastTick(), Instant.now()).toMillis() > 1000) {
                    Bukkit.broadcastMessage(Message.PREFIX.getMessage() + "World " + world.getName() + " didn't keep up. Please check the console for errors or possible crashes.");
                }
            }
        }
    }

}
