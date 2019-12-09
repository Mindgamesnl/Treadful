package com.craftmend.treadful.world.tickers;

import com.craftmend.treadful.world.interfaces.InjectedTickable;
import com.craftmend.treadful.world.wrappers.CustomWorldServer;

public class ChunkMap implements InjectedTickable {
    @Override
    public void tick(CustomWorldServer instance) {
        instance.scheduleSync(() -> {
            instance.methodProfiler.c("chunkMap");
            instance.timings.doChunkMap.startTiming();
            instance.getManager().flush();
            instance.timings.doChunkMap.stopTiming();
        });
    }
}
