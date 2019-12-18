package com.craftmend.treadful;

import com.craftmend.treadful.plugin.commands.TreadfulCommand;
import com.craftmend.treadful.plugin.enums.Message;
import com.craftmend.treadful.plugin.enums.OptionalFeature;
import com.craftmend.treadful.plugin.listeners.WorldLoadListener;
import com.craftmend.treadful.plugin.storage.StorageModule;
import com.craftmend.treadful.plugin.tasks.WorldWatchdogTask;
import com.craftmend.treadful.scheduler.SchedulerFactory;
import com.craftmend.treadful.scheduler.provider.SchedulerProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Treadful extends JavaPlugin {

    private static Treadful instance;

    private StorageModule storageModule;
    private SchedulerProvider schedulerProvider;

    @Override
    public void onEnable() {
        getCommand("treadful").setExecutor(new TreadfulCommand());
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new WorldWatchdogTask(), 20, 20);
    }

    @Override
    public void onLoad() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();

        storageModule = new StorageModule();

        try {
            new WorldLoadListener();
        } catch (NoSuchFieldException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        Message.toOp(Message.PREFIX.getMessage() + "Injecting scheduler");
        try {
            new SchedulerFactory().inject();
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        // handle exerimental features
        for (OptionalFeature value : OptionalFeature.values()) {
            value.getFeature().init();
        }
    }

    @Override
    public void onDisable() {
        storageModule.save();
    }

    public void initializeProvider() throws NoSuchFieldException, IllegalAccessException {
        if (schedulerProvider == null) schedulerProvider = new SchedulerProvider();
    }

    public static Treadful getInstance() {
        return instance;
    }

    public StorageModule getStorageModule() {
        return storageModule;
    }

    public SchedulerProvider getSchedulerProvider() {
        return schedulerProvider;
    }
}
