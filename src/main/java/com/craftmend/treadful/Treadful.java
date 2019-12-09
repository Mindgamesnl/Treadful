package com.craftmend.treadful;
import com.craftmend.treadful.plugin.enums.Message;
import com.craftmend.treadful.plugin.tasks.WorldWatchdogTask;
import com.craftmend.treadful.world.WorldFactory;
import com.craftmend.treadful.world.implementation.WorldImplementation;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.plugin.java.JavaPlugin;

public final class Treadful extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (World world : Bukkit.getWorlds()) {
                Bukkit.broadcastMessage(Message.PREFIX.getMessage() + "Injecting into world " + world.getName());
                new WorldFactory().overwriteWorld((CraftWorld) world, WorldImplementation.class);
            }
        }, 20 * 5);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new WorldWatchdogTask(), 20, 20);
    }
}
