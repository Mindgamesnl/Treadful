package com.craftmend.treadful.plugin.menu;

import com.craftmend.treadful.Treadful;
import com.craftmend.treadful.plugin.clicklib.Item;
import com.craftmend.treadful.plugin.clicklib.menu.Menu;
import com.craftmend.treadful.plugin.enums.Message;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class TreadfulHomeMenu extends Menu {
    public TreadfulHomeMenu() {
        super(Treadful.getInstance(), Message.PREFIX.getMessage(), 9);

        setItem(2, new Item(Material.GRASS)
                .setName(ChatColor.RED + "Worlds")
                .setLore(Message.toLore("Configure threading modules for every individual wrold"))
                .onClick((clickingPlayer, item) -> {
                            new WorldsConfigurationMenu().openFor(clickingPlayer);
                        }
                ));

        setItem(5, new Item(Material.NETHER_STAR)
                .setName(ChatColor.RED + "Experimental features")
                .setLore(Message.toLore("Configure experimental features. They are not guaranteed to work, but you may get some extra performance out of your server."))
                .onClick((clickingPlayer, item) -> {
                            new ExperimentalFeaturesMenu().openFor(clickingPlayer);
                        }
                ));

        setItem(6, new Item(Material.LEVER)
                .setName(ChatColor.RED + "Plugins")
                .setLore(Message.toLore("Configure threading modules for every individual plugin"))
                .onClick((clickingPlayer, item) -> {
                            new PluginsConfigurationMenu(1).openFor(clickingPlayer);
                        }
                ));

    }
}
