package com.craftmend.treadful.world.implementation;

import com.craftmend.treadful.world.wrappers.CustomWorldServer;
import net.minecraft.server.v1_12_R1.*;

public class ImplementedWorldManager extends WorldManager {

    private CustomWorldServer customWorldServer;

    public ImplementedWorldManager(MinecraftServer minecraftserver, CustomWorldServer worldserver) {
        super(minecraftserver, worldserver);
        customWorldServer = worldserver;
    }

    @Override
    public void b(Entity entity) {
        if (MinecraftServer.getServer().isMainThread()) {
            super.b(entity);
            return;
        }

        customWorldServer.scheduleSync(() -> {
            super.b(entity);
        });
    }

    @Override
    public void a(Entity entity) {
        if (MinecraftServer.getServer().isMainThread()) {
            if (!customWorldServer.getTracker().trackedEntities.b(entity.getId())) super.a(entity);
            return;
        }

        if (customWorldServer.getTracker().trackedEntities.b(entity.getId())) return;
        customWorldServer.scheduleSync(() -> {
            if (!customWorldServer.getTracker().trackedEntities.b(entity.getId())) super.a(entity);
        });

    }

}
