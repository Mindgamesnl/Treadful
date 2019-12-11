package com.craftmend.treadful.scheduler.custom;

import com.craftmend.treadful.Treadful;
import com.craftmend.treadful.plugin.storage.StorageModule;
import com.craftmend.treadful.scheduler.enums.ExecutorType;
import com.craftmend.treadful.scheduler.provider.SchedulerProvider;
import com.craftmend.treadful.world.interfaces.HotSwappable;
import org.bukkit.craftbukkit.v1_12_R1.scheduler.CraftScheduler;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class SchedulerImplementation extends CraftScheduler {

    private StorageModule storageModule;
    private SchedulerProvider provider;

    public SchedulerImplementation() {
        storageModule = Treadful.getInstance().getStorageModule();
        provider = Treadful.getInstance().getSchedulerProvider();
    }

    private CraftScheduler fromPlugin(Plugin plugin) {
        return provider.getScheduler(plugin);
    }

    @Override
    public void mainThreadHeartbeat(int currentTick) {
        super.mainThreadHeartbeat(currentTick);
        provider.mainThreadHeartbeat(currentTick);
    }

    @Override
    public void cancelTask(int taskId) {
        provider.cancelTask(taskId);
        super.cancelTask(taskId);
    }

    @Override
    public void cancelTasks(Plugin plugin) {
        if (storageModule.getTypeByPlugin(plugin) == ExecutorType.ISOLATED) {
            fromPlugin(plugin).cancelTasks(plugin);
        } else {
            super.cancelTasks(plugin);
        }
    }

    @Override
    public void cancelAllTasks() {
        provider.cancelAllTasks();
        super.cancelAllTasks();
    }

    @Override
    public boolean isCurrentlyRunning(int taskId) {
        boolean isOfficial = super.isCurrentlyRunning(taskId);
        if (isOfficial) return isOfficial;
        return provider.isRunning(taskId);
    }

    @Override
    public boolean isQueued(int taskId) {
        boolean isOfficial = super.isQueued(taskId);
        if (isOfficial) return isOfficial;
        return provider.isQueued(taskId);
    }

    @Override
    public List<BukkitWorker> getActiveWorkers() {
        List<BukkitWorker> workers = super.getActiveWorkers();
        workers.addAll(provider.getWorkers());
        return workers;
    }

    @Override
    public List<BukkitTask> getPendingTasks() {
        List<BukkitTask> tasks = super.getPendingTasks();
        tasks.addAll(provider.getPendingTasks());
        return tasks;
    }

    @Override
    public int scheduleSyncDelayedTask(Plugin plugin, Runnable task) {
        switch (storageModule.getTypeByPlugin(plugin)) {
            case FORCE_ASYNC:
                return super.scheduleSyncDelayedTask(plugin, task, 0L);
            case ISOLATED:
                return fromPlugin(plugin).scheduleSyncDelayedTask(plugin, task, 0L);
            default:
                return super.scheduleSyncDelayedTask(plugin, task, 0L);
        }
    }

    @Override
    public BukkitTask runTask(Plugin plugin, Runnable runnable) {
        if (storageModule.getTypeByPlugin(plugin) == ExecutorType.ISOLATED) {
            return fromPlugin(plugin).runTaskLater(plugin, runnable, 0L);
        }
        return super.runTaskLater(plugin, runnable, 0L);
    }

    @Override
    public int scheduleAsyncDelayedTask(Plugin plugin, Runnable task) {
        if (storageModule.getTypeByPlugin(plugin) == ExecutorType.ISOLATED) {
            return fromPlugin(plugin).scheduleAsyncDelayedTask(plugin, task, 0L);
        }
        return super.scheduleAsyncDelayedTask(plugin, task, 0L);
    }

    @Override
    public BukkitTask runTaskAsynchronously(Plugin plugin, Runnable runnable) {
        if (storageModule.getTypeByPlugin(plugin) == ExecutorType.ISOLATED) {
            return fromPlugin(plugin).runTaskLaterAsynchronously(plugin, runnable, 0L);
        }
        return super.runTaskLaterAsynchronously(plugin, runnable, 0L);
    }

    // VANAF HIER FOUT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    @Override
    public int scheduleSyncDelayedTask(Plugin plugin, Runnable task, long delay) {
        switch (storageModule.getTypeByPlugin(plugin)) {
            case FORCE_ASYNC:
                return super.scheduleAsyncRepeatingTask(plugin, task, delay, -1L);
            case ISOLATED:
                return fromPlugin(plugin).scheduleSyncRepeatingTask(plugin, task, delay,-1L);
            default:
                return super.scheduleSyncRepeatingTask(plugin, task, delay, -1L);
        }
    }

    @Override
    public BukkitTask runTaskLater(Plugin plugin, Runnable runnable, long delay) {
        switch (storageModule.getTypeByPlugin(plugin)) {
            case FORCE_ASYNC:
                return super.runTaskTimerAsynchronously(plugin, runnable, delay, -1L);
            case ISOLATED:
                return fromPlugin(plugin).runTaskTimer(plugin, runnable, delay, -1L);
            default:
                return super.runTaskTimer(plugin, runnable, delay, -1L);
        }
    }

    @Override
    public int scheduleAsyncDelayedTask(Plugin plugin, Runnable task, long delay) {
        if (storageModule.getTypeByPlugin(plugin) == ExecutorType.ISOLATED) {
            return fromPlugin(plugin).scheduleAsyncRepeatingTask(plugin, task, delay, -1L);
        }
        return super.scheduleAsyncRepeatingTask(plugin, task, delay, -1L);
    }

    @Override
    public BukkitTask runTaskLaterAsynchronously(Plugin plugin, Runnable runnable, long delay) {
        if (storageModule.getTypeByPlugin(plugin) == ExecutorType.ISOLATED) {
            return fromPlugin(plugin).runTaskTimerAsynchronously(plugin, runnable, delay, -1L);
        }
        return super.runTaskTimerAsynchronously(plugin, runnable, delay, -1L);
    }

    @Override
    public int scheduleSyncRepeatingTask(Plugin plugin, Runnable runnable, long delay, long period) {
        switch (storageModule.getTypeByPlugin(plugin)) {
            case FORCE_ASYNC:
                return super.runTaskTimerAsynchronously(plugin, runnable, delay, period).getTaskId();
            case ISOLATED:
                return fromPlugin(plugin).runTaskTimer(plugin, runnable, delay, period).getTaskId();
            default:
                return super.runTaskTimer(plugin, runnable, delay, period).getTaskId();
        }
    }

    @Override
    public BukkitTask runTaskTimer(Plugin plugin, Runnable runnable, long delay, long period) {
        switch (storageModule.getTypeByPlugin(plugin)) {
            case FORCE_ASYNC:
                return super.runTaskTimerAsynchronously(plugin, runnable, delay, period);
            case ISOLATED:
                return fromPlugin(plugin).runTaskTimer(plugin, runnable, delay, period);
            default:
                return super.runTaskTimer(plugin, runnable, delay, period);
        }
    }

    @Override
    public int scheduleAsyncRepeatingTask(Plugin plugin, Runnable runnable, long delay, long period) {
        if (storageModule.getTypeByPlugin(plugin) == ExecutorType.ISOLATED) {
            return fromPlugin(plugin).runTaskTimerAsynchronously(plugin, runnable, delay, period).getTaskId();
        }
        return super.runTaskTimerAsynchronously(plugin, runnable, delay, period).getTaskId();
    }

    @Override
    public BukkitTask runTaskTimerAsynchronously(Plugin plugin, Runnable runnable, long delay, long period) {
        if (storageModule.getTypeByPlugin(plugin) == ExecutorType.ISOLATED) {
            return fromPlugin(plugin).runTaskTimerAsynchronously(plugin, runnable, delay, period);
        }
        return super.runTaskTimerAsynchronously(plugin, runnable, delay, period);
    }

    @Override
    public <T> Future<T> callSyncMethod(Plugin plugin, Callable<T> task) {
        if (storageModule.getTypeByPlugin(plugin) == ExecutorType.ISOLATED) {
            return fromPlugin(plugin).callSyncMethod(plugin, task);
        }
        return super.callSyncMethod(plugin, task);
    }

    @Override
    public int scheduleSyncDelayedTask(Plugin plugin, BukkitRunnable task, long delay) {
        switch (storageModule.getTypeByPlugin(plugin)) {
            case FORCE_ASYNC:
                return super.scheduleAsyncDelayedTask(plugin, (Runnable) task, delay);
            case ISOLATED:
                return fromPlugin(plugin).scheduleSyncDelayedTask(plugin, (Runnable) task, delay);
            default:
                return super.scheduleSyncDelayedTask(plugin, (Runnable) task, delay);
        }
    }

    @Override
    public int scheduleSyncDelayedTask(Plugin plugin, BukkitRunnable task) {
        switch (storageModule.getTypeByPlugin(plugin)) {
            case FORCE_ASYNC:
                return super.scheduleAsyncDelayedTask(plugin, (Runnable) task);
            case ISOLATED:
                return fromPlugin(plugin).scheduleSyncDelayedTask(plugin, (Runnable) task);
            default:
                return super.scheduleSyncDelayedTask(plugin, (Runnable) task);
        }
    }

    @Override
    public int scheduleSyncRepeatingTask(Plugin plugin, BukkitRunnable task, long delay, long period) {
        switch (storageModule.getTypeByPlugin(plugin)) {
            case FORCE_ASYNC:
                return super.scheduleAsyncRepeatingTask(plugin, (Runnable) task, delay, period);
            case ISOLATED:
                return fromPlugin(plugin).scheduleSyncRepeatingTask(plugin, (Runnable) task, delay, period);
            default:
                return super.scheduleSyncRepeatingTask(plugin, (Runnable) task, delay, period);
        }
    }

    @Override
    public BukkitTask runTask(Plugin plugin, BukkitRunnable task) throws IllegalArgumentException {
        if (storageModule.getTypeByPlugin(plugin) == ExecutorType.ISOLATED) {
            return fromPlugin(plugin).runTask(plugin, (Runnable) task);
        }
        return super.runTask(plugin, (Runnable) task);
    }

    @Override
    public BukkitTask runTaskAsynchronously(Plugin plugin, BukkitRunnable task) throws IllegalArgumentException {
        if (storageModule.getTypeByPlugin(plugin) == ExecutorType.ISOLATED) {
            return fromPlugin(plugin).runTaskAsynchronously(plugin, (Runnable) task);
        }
        return super.runTaskAsynchronously(plugin, (Runnable) task);
    }

    @Override
    public BukkitTask runTaskLater(Plugin plugin, BukkitRunnable task, long delay) throws IllegalArgumentException {
        switch (storageModule.getTypeByPlugin(plugin)) {
            case FORCE_ASYNC:
                return super.runTaskLaterAsynchronously(plugin, (Runnable) task, delay);
            case ISOLATED:
                return fromPlugin(plugin).runTaskLater(plugin, (Runnable) task, delay);
            default:
                return super.runTaskLater(plugin, (Runnable) task, delay);
        }
    }

    @Override
    public BukkitTask runTaskLaterAsynchronously(Plugin plugin, BukkitRunnable task, long delay) throws IllegalArgumentException {
        if (storageModule.getTypeByPlugin(plugin) == ExecutorType.ISOLATED) {
            return fromPlugin(plugin).runTaskLaterAsynchronously(plugin, (Runnable) task, delay);
        }
        return super.runTaskLaterAsynchronously(plugin, (Runnable) task, delay);
    }

    @Override
    public BukkitTask runTaskTimer(Plugin plugin, BukkitRunnable task, long delay, long period) throws IllegalArgumentException {
        switch (storageModule.getTypeByPlugin(plugin)) {
            case FORCE_ASYNC:
                return super.runTaskTimerAsynchronously(plugin, (Runnable) task, delay, period);
            case ISOLATED:
                return fromPlugin(plugin).runTaskTimer(plugin, (Runnable) task, delay, period);
            default:
                return super.runTaskTimer(plugin, (Runnable) task, delay, period);
        }
    }

    @Override
    public BukkitTask runTaskTimerAsynchronously(Plugin plugin, BukkitRunnable task, long delay, long period) throws IllegalArgumentException {
        if (storageModule.getTypeByPlugin(plugin) == ExecutorType.ISOLATED) {
            return fromPlugin(plugin).runTaskTimerAsynchronously(plugin, (Runnable) task, delay, period);
        }
        return super.runTaskTimerAsynchronously(plugin, (Runnable) task, delay, period);
    }
}
