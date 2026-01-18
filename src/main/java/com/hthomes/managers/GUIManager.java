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

    public static void openHomeList(Player player, int page) {
        String titleRaw = plugin.getLangManager().getRaw("gui.title").replace("{page}", String.valueOf(page));
        Inventory gui = Bukkit.createInventory(null, 27, MessageUtils.color(titleRaw));

        List<String> homes = plugin.getHomeManager().getPlayerHomes(player.getUniqueId());
        
        // 1.12 ve 1.20 arası en güvenli materyal tespiti
        Material bedIcon = Material.matchMaterial("RED_BED") != null ? Material.matchMaterial("RED_BED") : 
                          (Material.matchMaterial("BED") != null ? Material.matchMaterial("BED") : Material.STONE);

        for (int i = 0; i < 27; i++) {
            if (i < homes.size()) {
                String homeName = homes.get(i);
                gui.setItem(i, createItem(bedIcon, "<green>" + homeName, plugin.getLangManager().getRaw("gui.home-lore")));
            } else {
                gui.setItem(i, createItem(bedIcon, "<red>Yeni Ev Kur", "<gray>Tıkla ve sethome kur!"));
            }
        }
        player.openInventory(gui);
    }

    // GUIListener'ın aradığı ana metodlar
    public static void openSelection(Player p, String homeName) {
        String title = MessageUtils.color(plugin.getLangManager().getRaw("selection-menu.title").replace("{home}", homeName));
        Inventory inv = Bukkit.createInventory(null, 27, title);
        inv.setItem(11, createItem(Material.ENDER_PEARL, plugin.getLangManager().getRaw("selection-menu.teleport-name"), ""));
        inv.setItem(15, createItem(Material.BARRIER, plugin.getLangManager().getRaw("selection-menu.delete-name"), ""));
        p.openInventory(inv);
    }

    public static void openConfirmDelete(Player p, String homeName) {
        String title = MessageUtils.color(plugin.getLangManager().getRaw("confirm-gui.title").replace("{home}", homeName));
        Inventory inv = Bukkit.createInventory(null, 27, title);
        
        Material yesMat = Material.matchMaterial("LIME_STAINED_GLASS_PANE") != null ? Material.matchMaterial("LIME_STAINED_GLASS_PANE") : Material.STONE;
        Material noMat = Material.matchMaterial("RED_STAINED_GLASS_PANE") != null ? Material.matchMaterial("RED_STAINED_GLASS_PANE") : Material.STONE;

        ItemStack yes = createItem(yesMat, plugin.getLangManager().getRaw("confirm-gui.yes-name"), "");
        ItemStack no = createItem(noMat, plugin.getLangManager().getRaw("confirm-gui.no-name"), "");

        inv.setItem(11, yes);
        inv.setItem(15, no);
        p.openInventory(inv);
    }

    private static ItemStack createItem(Material m, String n, String l) {
        ItemStack i = new ItemStack(m != null ? m : Material.STONE);
        ItemMeta mt = i.getItemMeta();
        if (mt != null) {
            mt.setDisplayName(MessageUtils.color(n));
            if (l != null && !l.isEmpty()) mt.setLore(Collections.singletonList(MessageUtils.color(l)));
            i.setItemMeta(mt);
        }
        return i;
    }
}
