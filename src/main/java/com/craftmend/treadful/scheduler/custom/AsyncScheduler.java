package com.craftmend.treadful.scheduler.custom;

import org.bukkit.craftbukkit.v1_12_R1.scheduler.CraftScheduler;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncScheduler {

    private CraftScheduler scheduler;
    private AtomicInteger ids;

    public AsyncScheduler() throws IllegalAccessException, NoSuchFieldException {
        scheduler = new CraftScheduler();
        Field officialTaskid = CraftScheduler.class.getDeclaredField("ids");
        officialTaskid.setAccessible(true);
        ids = (AtomicInteger) officialTaskid.get(scheduler);
    }

    public CraftScheduler getScheduler() {
        return scheduler;
    }

    public AtomicInteger getIds() {
        return ids;
    }

}
