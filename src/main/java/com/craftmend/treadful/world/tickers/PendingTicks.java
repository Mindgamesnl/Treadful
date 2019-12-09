package com.craftmend.treadful.world.tickers;

import com.craftmend.treadful.world.interfaces.InjectedTickable;
import com.craftmend.treadful.world.wrappers.CustomWorldServer;

public class PendingTicks implements InjectedTickable {
    @Override
    public void tick(CustomWorldServer instance) {
        instance.scheduleSync(() -> {
            instance.methodProfiler.c("tickPending");
            instance.timings.doTickPending.startTiming();
            instance.a(false);
            instance.timings.doTickPending.stopTiming();
        });
    }
}
