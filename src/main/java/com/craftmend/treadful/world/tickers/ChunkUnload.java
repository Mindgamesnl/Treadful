package com.craftmend.treadful.world.tickers;

import com.craftmend.treadful.plugin.enums.Message;
import com.craftmend.treadful.world.interfaces.InjectedTickable;
import com.craftmend.treadful.world.wrappers.CustomWorldServer;
import org.bukkit.Bukkit;

public class ChunkUnload implements InjectedTickable {

    private Boolean isRunning = false;

    @Override
    public void tick(CustomWorldServer instance) {

        instance.timings.doChunkUnload.startTiming();
        instance.methodProfiler.c("chunkSource");

        if (!isRunning) {
            isRunning = true;
            instance.scheduleSync(() -> {
                try {
                    instance.getChunkProvider().unloadChunks();
                } catch (Exception e) {
                    Message.toOp(Message.PREFIX.getMessage()  + "Exception while handling unloadChunks. The server recovered, but please notify the plugin developer.");
                }
                isRunning = false;
            });
        }

        int j = instance.a(1.0F);
        if (j != instance.ah()) {
            instance.c(j);
        }

        instance.worldData.setTime(instance.worldData.getTime() + 1L);
        if (instance.getGameRules().getBoolean("doDaylightCycle")) {
            instance.worldData.setDayTime(instance.worldData.getDayTime() + 1L);
        }
        instance.timings.doChunkUnload.stopTiming();
    }
}
