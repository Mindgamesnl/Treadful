package com.craftmend.treadful.plugin.clicklib.menu;

import com.craftmend.treadful.plugin.clicklib.Item;
import com.craftmend.treadful.plugin.clicklib.managers.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public abstract class Menu implements InventoryHolder {

    private Inventory inventory;
    private Map<Integer, Item> slotMap = new HashMap<>();

    public Menu(JavaPlugin javaPlugin, String name, int size) {
        inventory = Bukkit.createInventory(this, size, name);
        InventoryManager.getInstance(javaPlugin);
    }

    public void onClose(Player player) {

    }

    public Menu setItem(int slot, Item item) {
        slotMap.put(slot, item);
        inventory.setItem(slot, item.getItem());
        return this;
    }

    void handleClick(int slot, Player player) {
        Item itemAtSlot = slotMap.get(slot);
        if (itemAtSlot == null) return;
        itemAtSlot.getOnClick().accept(player, itemAtSlot);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void openFor(Player player) {
        player.openInventory(this.inventory);
    }

}
