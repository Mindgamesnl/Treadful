package com.craftmend.treadful.world.tickers;

import com.craftmend.treadful.world.interfaces.InjectedTickable;
import com.craftmend.treadful.world.wrappers.CustomWorldServer;

public class Sound implements InjectedTickable {
    @Override
    public void tick(CustomWorldServer instance) {
        instance.methodProfiler.b();

        instance.timings.doSounds.startTiming();
        instance.tickSounds();
        instance.timings.doSounds.stopTiming();
    }
}
