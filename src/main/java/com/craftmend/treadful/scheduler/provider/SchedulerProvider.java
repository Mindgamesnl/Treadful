package com.craftmend.treadful.scheduler.provider;

import com.craftmend.treadful.plugin.enums.Message;
import com.craftmend.treadful.scheduler.custom.AsyncScheduler;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import net.minecraft.server.v1_12_R1.SystemUtils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.scheduler.CraftScheduler;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;
import org.spigotmc.AsyncCatcher;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class SchedulerProvider {

    private ExecutorService fakeSyncExecutor = Executors.newSingleThreadExecutor();
    private ConcurrentHashMap<String, AsyncScheduler> isolatedThreads = new ConcurrentHashMap<>();
    private AtomicInteger officialIds;
    private Thread realMainThread;
    private Thread realPrimaryThread;
    private Field mainThreadField;
    private Field primaryThreadField;
    private boolean isTicking = false;

    public SchedulerProvider() throws IllegalAccessException, NoSuchFieldException {
        Field officialTaskid = CraftScheduler.class.getDeclaredField("ids");
        officialTaskid.setAccessible(true);
        officialIds = (AtomicInteger) officialTaskid.get(Bukkit.getScheduler());

        mainThreadField = MinecraftServer.class.getDeclaredField("serverThread");
        mainThreadField.setAccessible(true);

        primaryThreadField = MinecraftServer.class.getDeclaredField("primaryThread");
        primaryThreadField.setAccessible(true);

        realMainThread = (Thread) mainThreadField.get(MinecraftServer.getServer());
        realPrimaryThread = (Thread) primaryThreadField.get(MinecraftServer.getServer());

        AsyncCatcher.enabled = false;
    }

    public void mainThreadHeartbeat(int currentTick) {
        // push ids to children
        for (AsyncScheduler value : isolatedThreads.values()) {
            value.getIds().set(officialIds.get());
        }

        if (isTicking) return;
        isTicking = true;

        fakeSyncExecutor.submit(() -> {
            try {
                // to tick everything, we must FAKE the main thread.
                // Hacky as all fuck, but it's what we have to do...

                for (AsyncScheduler value : isolatedThreads.values()) {
                    value.getScheduler().mainThreadHeartbeat(currentTick);

                    // check for updates, else push
                    if (value.getIds().get() > officialIds.get()) {
                        officialIds.set(value.getIds().get());
                        for (AsyncScheduler itteration : isolatedThreads.values()) {
                            itteration.getIds().set(value.getIds().get());
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            isTicking = false;
        });
    }

    public void register(Plugin plugin) {
        if (isolatedThreads.containsKey(plugin.getName())) return;
        try {
            isolatedThreads.put(plugin.getName(), new AsyncScheduler());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public void remove(Plugin plugin) {
        isolatedThreads.remove(plugin.getName());
    }

    public CraftScheduler getScheduler(Plugin plugin) {
        AsyncScheduler scheduler = isolatedThreads.get(plugin.getName());
        if (scheduler != null) {
            return scheduler.getScheduler();
        }
        return (CraftScheduler) Bukkit.getScheduler();
    }

    public void cancelAllTasks() {
        for (AsyncScheduler value : isolatedThreads.values()) {
            value.getScheduler().cancelAllTasks();
        }
    }

    public boolean isRunning(int task) {
        for (AsyncScheduler itteration : isolatedThreads.values()) {
            if (itteration.getScheduler().isCurrentlyRunning(task)) return true;
        }
        return false;
    }

    public boolean isQueued(int task) {
        for (AsyncScheduler itteration : isolatedThreads.values()) {
            if (itteration.getScheduler().isQueued(task)) return true;
        }
        return false;
    }

    public List<BukkitWorker> getWorkers() {
        List<BukkitWorker> workers = new ArrayList<>();
        for (AsyncScheduler itteration : isolatedThreads.values()) {
            workers.addAll(itteration.getScheduler().getActiveWorkers());
        }
        return workers;
    }

    public List<BukkitTask> getPendingTasks() {
        List<BukkitTask> workers = new ArrayList<>();
        for (AsyncScheduler itteration : isolatedThreads.values()) {
            workers.addAll(itteration.getScheduler().getPendingTasks());
        }
        return workers;
    }

    public void cancelTask(int taskId) {
        for (AsyncScheduler itteration : isolatedThreads.values()) {
            itteration.getScheduler().cancelTask(taskId);
        }
    }

}
