package com.craftmend.treadful.plugin.menu;

import com.craftmend.treadful.Treadful;
import com.craftmend.treadful.plugin.clicklib.Item;
import com.craftmend.treadful.plugin.clicklib.menu.Menu;
import com.craftmend.treadful.plugin.enums.Message;
import com.craftmend.treadful.plugin.enums.OptionalFeature;
import org.bukkit.*;
import org.bukkit.event.world.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExperimentalFeaturesMenu extends Menu {
    public ExperimentalFeaturesMenu() {
        super(Treadful.getInstance(), Message.PREFIX.getMessage() + "Worlds",  9);

        int i = 0;
        for (OptionalFeature feature : OptionalFeature.values()) {

            boolean isEnabled = feature.getFeature().isEnabled();

            List<String> lore = new ArrayList<>();

            lore.addAll(Arrays.asList(Message.toLore(feature.getDescription())));
            lore.add("");
            lore.addAll(Arrays.asList(Message.toLore("This feature is still experimental. Please disable it and restart if it causes problems.")));
            lore.add("");
            lore.add(ChatColor.WHITE + "Current State: " + (isEnabled ? (
                    ChatColor.GREEN + "Enabled"
                    ) : (
                        ChatColor.RED + "Disabled"
                    )));

            setItem(i, new Item(feature.getVisualizer())
                    .setName(ChatColor.GREEN + feature.getName())
                    .setLore(lore.toArray(new String[lore.size()]))
                    .onClick((clickingPlayer, item) -> {
                                if (isEnabled) {
                                    feature.getFeature().disable();
                                } else {
                                    feature.getFeature().enable();
                                }
                                new ExperimentalFeaturesMenu().openFor(clickingPlayer);
                            }
                    ));

            i++;
        }

        setItem(8, new Item(Material.CHEST).setName("Back to home").onClick((player, item) -> new TreadfulHomeMenu().openFor(player)));
    }

    private void fakeReloadWorld(World world) {
        Message.toOp(Message.PREFIX.getMessage() + "Updating external plugins of " + world.getName() + "'s reload");

        // fake unload chunks
        for (Chunk loadedChunk : world.getLoadedChunks().clone()) {
            Bukkit.getPluginManager().callEvent(new ChunkUnloadEvent(loadedChunk, true));
        }

        // fake unload wold
        Bukkit.getPluginManager().callEvent(new WorldUnloadEvent(world));

        // fake init world
        Bukkit.getPluginManager().callEvent(new WorldInitEvent(world));

        // fake load world
        Bukkit.getPluginManager().callEvent(new WorldLoadEvent(world));

        // fake load chunk
        for (Chunk loadedChunk : world.getLoadedChunks().clone()) {
            Bukkit.getPluginManager().callEvent(new ChunkLoadEvent(loadedChunk, false));
        }

        Message.toOp(Message.PREFIX.getMessage() + "World  " + world.getName() + " reloaded. Please restart your server if you encounter weird behaviour.");
    }
}
