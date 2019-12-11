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

        setItem(6, new Item(Material.LEVER)
                .setName(ChatColor.RED + "Plugins")
                .setLore(Message.toLore("Configure threading modules for every individual plugin"))
                .onClick((clickingPlayer, item) -> {
                    new PluginsConfigurationMenu(1).openFor(clickingPlayer);
                }
        ));

    }
}
