package com.hthomes.managers;

import com.hthomes.HTHomes;
import com.hthomes.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Collections;
import java.util.List;

public class GUIManager {
    private static HTHomes plugin;
    public static void setPlugin(HTHomes p) { plugin = p; }

    public static void openHomeList(Player p) {
        String title = plugin.getLangManager().getRaw(p, "gui.title");
        Inventory inv = Bukkit.createInventory(null, 27, MessageUtils.color(title));
        
        for (int i = 0; i < 27; i++) inv.setItem(i, createItem(Material.GRAY_STAINED_GLASS_PANE, " ", ""));

        for (int i = 1; i <= 7; i++) {
            String name = "Ev-" + i;
            if (plugin.getHomeManager().exists(p.getUniqueId(), name)) {
                Material icon = plugin.getHomeManager().getIcon(p.getUniqueId(), name);
                inv.setItem(9+i, createItem(icon, "<green>" + name, plugin.getLangManager().getRaw(p, "gui.click-teleport")));
            } else {
                inv.setItem(9+i, createItem(Material.RED_BED, "<red>" + name, plugin.getLangManager().getRaw(p, "gui.click-create")));
            }
        }
        p.openInventory(inv);
    }

    public static void openSelection(Player p, String homeName) {
        String title = plugin.getLangManager().getRaw(p, "selection-menu.title").replace("{home}", homeName);
        Inventory inv = Bukkit.createInventory(null, 27, MessageUtils.color(title));
        
        inv.setItem(10, createItem(Material.ENDER_PEARL, plugin.getLangManager().getRaw(p, "selection-menu.teleport-name"), ""));
        inv.setItem(13, createItem(Material.PAINTING, plugin.getLangManager().getRaw(p, "selection-menu.change-icon-name"), ""));
        inv.setItem(16, createItem(Material.BARRIER, plugin.getLangManager().getRaw(p, "selection-menu.delete-name"), ""));
        p.openInventory(inv);
    }

    public static void openIconMenu(Player p, String homeName) {
        String title = plugin.getLangManager().getRaw(p, "icon-menu.title").replace("{home}", homeName);
        Inventory inv = Bukkit.createInventory(null, 36, MessageUtils.color(title));
        
        Material[] icons = {Material.GRASS_BLOCK, Material.DIAMOND, Material.GOLD_INGOT, Material.IRON_SWORD, Material.BOOK, Material.LANTERN, Material.CAMPFIRE, Material.CRAFTING_TABLE, Material.CHEST};
        for (Material m : icons) inv.addItem(createItem(m, "<yellow>" + m.name(), ""));
        
        p.openInventory(inv);
    }

    public static void openConfirmDelete(Player p, String homeName) {
        String title = plugin.getLangManager().getRaw(p, "confirm-gui.title").replace("{home}", homeName);
        Inventory inv = Bukkit.createInventory(null, 27, MessageUtils.color(title));
        inv.setItem(11, createItem(Material.LIME_STAINED_GLASS_PANE, plugin.getLangManager().getRaw(p, "confirm-gui.yes-name"), ""));
        inv.setItem(15, createItem(Material.RED_STAINED_GLASS_PANE, plugin.getLangManager().getRaw(p, "confirm-gui.no-name"), ""));
        p.openInventory(inv);
    }

    private static ItemStack createItem(Material m, String n, String l) {
        ItemStack i = new ItemStack(m != null ? m : Material.STONE);
        ItemMeta mt = i.getItemMeta();
        if (mt != null) {
            mt.setDisplayName(MessageUtils.color(n));
            if(!l.isEmpty()) mt.setLore(Collections.singletonList(MessageUtils.color(l)));
            i.setItemMeta(mt);
        }
        return i;
    }
}
