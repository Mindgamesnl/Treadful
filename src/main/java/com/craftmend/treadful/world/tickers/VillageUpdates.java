package com.craftmend.treadful.world.tickers;

import com.craftmend.treadful.world.interfaces.InjectedTickable;
import com.craftmend.treadful.world.wrappers.CustomWorldServer;

public class VillageUpdates implements InjectedTickable {
    @Override
    public void tick(CustomWorldServer instance) {
        instance.methodProfiler.c("village");
        instance.timings.doVillages.startTiming();
        instance.getVillages().tick();
        instance.getSiegeManager().a();
        instance.timings.doVillages.stopTiming();
    }
}
