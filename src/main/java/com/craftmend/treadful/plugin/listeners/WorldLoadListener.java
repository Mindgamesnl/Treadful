package com.craftmend.treadful.plugin.listeners;

import com.craftmend.treadful.Treadful;
import com.craftmend.treadful.plugin.enums.Message;
import com.craftmend.treadful.util.EventInterceptor;
import com.craftmend.treadful.world.WorldFactory;
import com.craftmend.treadful.world.implementation.WorldImplementation;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class WorldLoadListener {

    private Set<String> handledWorlds = new HashSet<>();

    private Runnable worldLoadInterceptor = () -> Message.toOp(Message.PREFIX.getMessage() + "Worlds loaded with default priority.");
    private boolean hasInitialized = false;
    private boolean postponedInitialize = false;

    public WorldLoadListener() throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        EventInterceptor.create(WorldInitEvent.class).setMiddleware((event, callback) -> onLoad((WorldInitEvent) event, callback));

        EventInterceptor.create(WorldLoadEvent.class).setMiddleware((event, callback) -> {
            if (!hasInitialized) {
                Message.toOp(Message.PREFIX.getMessage() + "Worlds haven't initialized yet. Thus the world loader should be delayed");
                postponedInitialize = true;
                worldLoadInterceptor = callback;
            } else {
                callback.run();
            }
        });
    }

    private void onLoad(WorldInitEvent event, Runnable callback) {
        if (handledWorlds.contains(event.getWorld().getName())) return;
        handledWorlds.add(event.getWorld().getName());

        CompletableFuture.runAsync(() -> {
            Message.toOp(Message.PREFIX.getMessage() + "Injecting into world " + event.getWorld().getName());

            new WorldFactory().overwriteWorld((CraftWorld) event.getWorld(), WorldImplementation.class);

            Bukkit.getScheduler().runTask(Treadful.getInstance(), () -> {
                callback.run();
                hasInitialized = true;
                if (postponedInitialize) {
                    Message.toOp(Message.PREFIX.getMessage() + "World load has been postponed. Continuing startup procedure.");
                }
                worldLoadInterceptor.run();
            });
        });
    }

}
