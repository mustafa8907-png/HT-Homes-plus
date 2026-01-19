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
        Inventory inv = Bukkit.createInventory(null, 27, MessageUtils.color("&8Ev Menüsü"));
        for (int i = 10; i <= 16; i++) {
            String homeName = "Ev-" + (i - 9);
            if (plugin.getHomeManager().exists(p.getUniqueId(), homeName)) {
                inv.setItem(i, createItem(Material.LIME_BED, "&a" + homeName, "&7Işınlanmak için tıkla."));
            } else {
                inv.setItem(i, createItem(Material.RED_BED, "&c" + homeName, "&7Buraya ev kurmak için tıkla."));
            }
        }
        p.openInventory(inv);
    }

    public static void openSelection(Player p, String homeName) {
        Inventory inv = Bukkit.createInventory(null, 27, MessageUtils.color("&8Yönet: " + homeName));
        inv.setItem(11, createItem(Material.ENDER_PEARL, "&bIşınlan", ""));
        inv.setItem(15, createItem(Material.BARRIER, "&cSil", ""));
        p.openInventory(inv);
    }

    public static void openConfirmDelete(Player p, String homeName) {
        Inventory inv = Bukkit.createInventory(null, 27, MessageUtils.color("&cSilme Onayı: " + homeName));
        inv.setItem(11, createItem(Material.LIME_STAINED_GLASS_PANE, "&aOnayla", ""));
        inv.setItem(15, createItem(Material.RED_STAINED_GLASS_PANE, "&cVazgeç", ""));
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
