package com.craftmend.treadful.world.implementation;

import com.craftmend.treadful.plugin.enums.Message;
import com.craftmend.treadful.world.interfaces.InjectedTickable;
import com.craftmend.treadful.world.tickers.*;
import com.craftmend.treadful.world.wrappers.CustomWorldServer;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WorldImplementation extends CustomWorldServer {

    private InjectedTickable[] tickables;

    public WorldImplementation(MinecraftServer minecraftserver, IDataManager idatamanager, WorldData worlddata, int i, MethodProfiler methodprofiler, World.Environment env, ChunkGenerator gen) {
        super(minecraftserver, idatamanager, worlddata, i, methodprofiler, env, gen);
    }

    @Override
    public void onSwap() throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException {
        super.onSwap();

        tickables = new InjectedTickable[] {
                new TickWorld(),
                new ChunkUnload(),
                new PendingTicks(),
                new TickTiles(),
                new ChunkMap(),
                new VillageUpdates(),
                new PortalForcer(),
                new Sound(),
                new ChunkGC()
        };

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        scheduler.scheduleAtFixedRate(() -> {
            doActualTick();
            keepAlive();
        }, 50, 50, TimeUnit.MILLISECONDS);
    }

    private void doActualTick() {
        for (InjectedTickable tickable : tickables) {
            try {
                tickable.tick(this);
            } catch (Exception e) {
                Message.toOp(Message.PREFIX.getMessage()  + "Exception while ticking " + tickable.getClass().getSimpleName() + ". The server recovered, but please notify the plugin developer.");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void doTick() {
        // cancel normal sync tick
        super.tickScheduledInterceptions();
    }
}
