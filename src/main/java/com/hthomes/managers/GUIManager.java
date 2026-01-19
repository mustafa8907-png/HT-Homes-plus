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

public class GUIManager {
    private static HTHomes plugin;
    public static void setPlugin(HTHomes p) { plugin = p; }

    public static void openHomeList(Player p) {
        // Dil dosyasından başlığı çekiyoruz
        String title = plugin.getLangManager().getRaw(p, "gui.title");
        Inventory inv = Bukkit.createInventory(null, 27, MessageUtils.color(title));
        
        String clickTeleport = plugin.getLangManager().getRaw(p, "gui.click-teleport");
        String clickCreate = plugin.getLangManager().getRaw(p, "gui.click-create");

        // 10'dan 16'ya kadar olan slotları doldur (Toplam 7 ev slotu)
        for (int i = 10; i <= 16; i++) {
            String homeName = "Ev-" + (i - 9);
            if (plugin.getHomeManager().exists(p.getUniqueId(), homeName)) {
                // EV VARSA YEŞİL YATAK (Açıklama dilden geliyor)
                inv.setItem(i, createItem(Material.LIME_BED, "&a" + homeName, clickTeleport));
            } else {
                // EV YOKSA KIRMIZI YATAK (Açıklama dilden geliyor)
                inv.setItem(i, createItem(Material.RED_BED, "&c" + homeName, clickCreate));
            }
        }
        p.openInventory(inv);
    }

    public static void openSelection(Player p, String homeName) {
        String title = plugin.getLangManager().getRaw(p, "selection-menu.title").replace("{home}", homeName);
        Inventory inv = Bukkit.createInventory(null, 27, MessageUtils.color(title));
        
        String teleportName = plugin.getLangManager().getRaw(p, "selection-menu.teleport-name");
        String deleteName = plugin.getLangManager().getRaw(p, "selection-menu.delete-name");

        inv.setItem(11, createItem(Material.ENDER_PEARL, teleportName, ""));
        inv.setItem(15, createItem(Material.BARRIER, deleteName, ""));
        p.openInventory(inv);
    }

    public static void openConfirmDelete(Player p, String homeName) {
        String title = plugin.getLangManager().getRaw(p, "confirm-gui.title").replace("{home}", homeName);
        Inventory inv = Bukkit.createInventory(null, 27, MessageUtils.color(title));
        
        String yesName = plugin.getLangManager().getRaw(p, "confirm-gui.yes-name");
        String noName = plugin.getLangManager().getRaw(p, "confirm-gui.no-name");

        inv.setItem(11, createItem(Material.LIME_STAINED_GLASS_PANE, yesName, ""));
        inv.setItem(15, createItem(Material.RED_STAINED_GLASS_PANE, noName, ""));
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
