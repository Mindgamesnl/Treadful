package com.craftmend.treadful.plugin.menu;

import com.craftmend.treadful.Treadful;
import com.craftmend.treadful.plugin.clicklib.Item;
import com.craftmend.treadful.plugin.clicklib.menu.Menu;
import com.craftmend.treadful.plugin.enums.Message;
import com.craftmend.treadful.scheduler.enums.ExecutorType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PluginsConfigurationMenu extends Menu {

    public PluginsConfigurationMenu(int page) {
        super(Treadful.getInstance(), Message.PREFIX.getMessage() + "Plugins" + ChatColor.RESET + " - page " + page + " / " + ((int) Math.ceil(Bukkit.getPluginManager().getPlugins().length / 45) + 1), 6 * 9);

        int pages = (int) Math.ceil(Bukkit.getPluginManager().getPlugins().length / 45) + 1;

        // get 45 items
        Plugin[] plugins = new Plugin[45];

        // amount on this page
        int count;
        if (pages == page) {
            count = Bukkit.getPluginManager().getPlugins().length - ((page - 1) * 45);
        } else {
            count = 45;
        }
        // copy
        System.arraycopy(Bukkit.getPluginManager().getPlugins(), ((page - 1) * 45), plugins, 0, count);

        int slot = 0;
        for (Plugin plugin : plugins) {
            if (plugin != null) {
                Item item = new Item(Material.LAVA_BUCKET);

                item.setName(plugin.getName());
                ExecutorType current = Treadful.getInstance().getStorageModule().getTypeByPlugin(plugin);

                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GREEN + "Version: " + ChatColor.RESET + plugin.getDescription().getVersion());
                lore.add("");
                lore.add(ChatColor.GREEN + "Threading mode:");
                lore.add(threadingModeName(ExecutorType.DEFAULT, current.getLevel()));
                lore.add(threadingModeName(ExecutorType.FORCE_ASYNC, current.getLevel()));
                lore.add(threadingModeName(ExecutorType.ISOLATED, current.getLevel()));
                lore.add("");
                lore.add(ChatColor.GREEN + "About " + current.getName());
                Collections.addAll(lore, Message.toLore(current.getDescription()));

                item.setLore(lore.toArray(new String[lore.size() - 1]));

                item.onClick((player, copyOfItem) -> {
                    int next = current.getLevel() + 1;
                    if (next > ExecutorType.HIGHEST) next = ExecutorType.LOWEST;
                    Treadful.getInstance().getStorageModule().setTypeForPlugin(plugin, next);
                    new PluginsConfigurationMenu(page).openFor(player);
                });

                setItem(slot, item);
                slot++;
            }
        }

        Item cancelItem = new Item(Material.TNT);

        cancelItem.setName(ChatColor.RED + "UNSAVED CHANGES!");

        cancelItem.setLore(Message.toLore("You have unsaved changes to your threading model. To apply, close this menu and do a full restart of your server. If you want to discard the changes, click here."));

        cancelItem.onClick((player, copyOfItem) -> {
            Treadful.getInstance().getStorageModule().reset();
            new PluginsConfigurationMenu(page).openFor(player);
        });

        if (Treadful.getInstance().getStorageModule().hasChanges()) {
            setItem(46, cancelItem);
            setItem(47, cancelItem);
            setItem(48, cancelItem);
            setItem(50, cancelItem);
            setItem(51, cancelItem);
            setItem(52, cancelItem);
        }

        // buttons
        if (pages > page) {
            setItem(53, new Item(Material.LEVER).setName("Next page").onClick((player, item) -> new PluginsConfigurationMenu(page + 1).openFor(player)));
        } else {
            setItem(53, new Item(Material.BARRIER).setName(ChatColor.RED + "No next page"));
        }

        // page back
        if ((page - 1) != 0) {
            setItem(45, new Item(Material.ARROW).setName("Previous page").onClick((player, item) -> new PluginsConfigurationMenu(page - 1).openFor(player)));
        } else {
            setItem(45, new Item(Material.BARRIER).setName(ChatColor.RED + "Not available"));
        }

        setItem(49, new Item(Material.CHEST).setName("Back to home").onClick((player, item) -> new TreadfulHomeMenu().openFor(player)));
    }

    private String threadingModeName(ExecutorType type, int current) {
        if (current == type.getLevel())
            return ChatColor.GOLD + "> " + ChatColor.AQUA + type.getName() +  ChatColor.GOLD + " <";
        return "  " + ChatColor.AQUA + type.getName() + "  ";
    }
}