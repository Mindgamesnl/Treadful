package com.craftmend.treadful.plugin.storage;

import com.craftmend.treadful.Treadful;
import com.craftmend.treadful.plugin.enums.Message;
import com.craftmend.treadful.scheduler.enums.ExecutorType;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

import static com.craftmend.treadful.scheduler.enums.ExecutorType.*;

public class StorageModule {

    private Map<String, Integer> pluginThreadingType = new HashMap<>();
    private boolean hasChanges = false;

    public ExecutorType getTypeByPlugin(Plugin plugin) {
        if (pluginThreadingType.containsKey(plugin.getName())) return byId(pluginThreadingType.getOrDefault(plugin.getName(), 0));
        ExecutorType type = byId(Treadful.getInstance().getConfig().getInt("data." + plugin.getName() + ".type"));
        updateThreads(plugin, type, false);
        pluginThreadingType.put(plugin.getName(), type.getLevel());
        return type;
    }

    public void setTypeForPlugin(Plugin plugin, int type) {
        pluginThreadingType.put(plugin.getName(), type);
        updateThreads(plugin, ExecutorType.byId(type), true);
        hasChanges = true;
    }

    private void updateThreads(Plugin plugin, ExecutorType executorType, Boolean announce) {
        if (executorType == ISOLATED) {
            Message.toOp(Message.PREFIX.getMessage() + "Creating thread pool for plugin " + plugin.getName());
            Treadful.getInstance().getSchedulerProvider().register(plugin);
        } else {
            Treadful.getInstance().getSchedulerProvider().remove(plugin);
        }
        if (announce) Message.toOp(Message.PREFIX.getMessage() + "The plugin " + plugin.getName() + " now operates in " + executorType.name() + " mode.");
    }

    public void save() {
        if (hasChanges) {
            for (Map.Entry<String, Integer> entry : pluginThreadingType.entrySet()) {
                String plugin = entry.getKey();
                Integer level = entry.getValue();

                Treadful.getInstance().getConfig().set("data." + plugin + ".type", level);
                Treadful.getInstance().saveConfig();
            }
        }
    }

    public void reset() {
        pluginThreadingType.clear();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            int id = Treadful.getInstance().getConfig().getInt("data." + plugin.getName() + ".type");
            pluginThreadingType.put(plugin.getName(), byId(id).getLevel());
        }
        hasChanges = false;
    }

    public boolean hasChanges() {
        return hasChanges;
    }

}
