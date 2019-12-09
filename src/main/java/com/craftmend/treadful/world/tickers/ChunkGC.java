package com.craftmend.treadful.world.tickers;

import com.craftmend.treadful.world.interfaces.InjectedTickable;
import com.craftmend.treadful.world.wrappers.CustomWorldServer;

public class ChunkGC implements InjectedTickable {
    @Override
    public void tick(CustomWorldServer instance) {
        instance.timings.doChunkGC.startTiming();
        instance.getWorld().processChunkGC();
        instance.timings.doChunkGC.stopTiming();
    }
}
