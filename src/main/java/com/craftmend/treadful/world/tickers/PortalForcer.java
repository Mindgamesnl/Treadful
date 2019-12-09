package com.craftmend.treadful.world.tickers;

import com.craftmend.treadful.world.interfaces.InjectedTickable;
import com.craftmend.treadful.world.wrappers.CustomWorldServer;

public class PortalForcer implements InjectedTickable {
    @Override
    public void tick(CustomWorldServer instance) {
        instance.methodProfiler.c("portalForcer");
        instance.timings.doPortalForcer.startTiming();
        instance.getTravelAgent().a(instance.getTime());
        instance.timings.doPortalForcer.stopTiming();
    }
}
