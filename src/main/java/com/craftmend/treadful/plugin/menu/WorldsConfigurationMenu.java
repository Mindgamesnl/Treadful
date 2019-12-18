package com.craftmend.treadful.plugin.menu;

import com.craftmend.treadful.Treadful;
import com.craftmend.treadful.plugin.clicklib.Item;
import com.craftmend.treadful.plugin.clicklib.menu.Menu;
import com.craftmend.treadful.plugin.enums.Message;
import com.craftmend.treadful.world.WorldFactory;
import com.craftmend.treadful.world.implementation.WorldImplementation;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import net.minecraft.server.v1_12_R1.WorldServer;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.event.world.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldsConfigurationMenu extends Menu {
    public WorldsConfigurationMenu() {
        super(Treadful.getInstance(), Message.PREFIX.getMessage() + "Worlds",  3 * 9);

        int i = 0;
        for (World world : Bukkit.getWorlds()) {

            CraftWorld cw = (CraftWorld) world;

            boolean isInjected = (cw.getHandle() instanceof WorldImplementation);

            List<String> lore = new ArrayList<>();

            lore.addAll(Arrays.asList(Message.toLore("Click to toggle the state of the world. DEFAULT is the normal way of minecraft world handling, and INJECTED is the optimized Treadful version.")));
            lore.add("");
            lore.addAll(Arrays.asList(Message.toLore("Please restart after updating since it may cause issues.")));
            lore.add("");
            lore.add(ChatColor.WHITE + "Current State: " + (isInjected ? (
                    ChatColor.GREEN + "INJECTED"
                    ) : (
                        ChatColor.RED + "DEFAULT"
                    )));

            setItem(i, new Item(Material.GRASS)
                    .setName(ChatColor.GREEN + world.getName())
                    .setLore(lore.toArray(new String[lore.size()]))
                    .onClick((clickingPlayer, item) -> {
                                if (isInjected) {
                                    Message.toOp(Message.PREFIX.getMessage() + clickingPlayer.getName() + " updated world " + world.getName() + " to be handled by WorldServer.class");
                                    new WorldFactory().overwriteWorld(cw, WorldServer.class);
                                    fakeReloadWorld(world);
                                } else {
                                    Message.toOp(Message.PREFIX.getMessage() + clickingPlayer.getName() + " updated world " + world.getName() + " to be handled by WorldImplementation.class");

                                    new WorldFactory().overwriteWorld(cw, WorldImplementation.class);

                                    fakeReloadWorld(world);
                                }
                                new WorldsConfigurationMenu().openFor(clickingPlayer);
                            }
                    ));

            i++;
        }

        setItem(((3 * 9) - 1), new Item(Material.CHEST).setName("Back to home").onClick((player, item) -> new TreadfulHomeMenu().openFor(player)));
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
