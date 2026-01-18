package com.hthomes.managers;

import com.hthomes.HTHomes;
import com.hthomes.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GUIManager {
    private static HTHomes plugin;

    public static void setPlugin(HTHomes p) { plugin = p; }

    public static void openHomeList(Player p, int page) {
        String title = MessageUtils.color(plugin.getLangManager().getRaw("gui.title").replace("{page}", String.valueOf(page)));
        Inventory inv = Bukkit.createInventory(null, 54, title);

        Map<String, org.bukkit.Location> homesMap = plugin.getHomeManager().getHomes(p);
        List<String> homeNames = new ArrayList<>(homesMap.keySet());

        int start = (page - 1) * 28;
        int end = Math.min(start + 28, homeNames.size());
        int[] slots = {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34,37,38,39,40,41,42,43};

        for (int i = 0; i < (end - start); i++) {
            String name = homeNames.get(start + i);
            inv.setItem(slots[i], createItem(plugin.getHomeManager().getHomeIcon(p, name), "§a" + name, plugin.getLangManager().getRaw("gui.home-lore")));
        }

        // Cam dekorasyonu (Opsiyonel - Boşlukları doldurur)
        ItemStack filler = createItem(Material.GRAY_STAINED_GLASS_PANE, " ", "");
        for (int i = 0; i < 54; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, filler);
        }

        if (page > 1) inv.setItem(45, createItem(Material.ARROW, plugin.getLangManager().getRaw("gui.prev-page"), ""));
        if (end < homeNames.size()) inv.setItem(53, createItem(Material.ARROW, plugin.getLangManager().getRaw("gui.next-page"), ""));

        p.openInventory(inv);
    }

    public static void openSelection(Player p, String homeName) {
        String title = MessageUtils.color(plugin.getLangManager().getRaw("selection-menu.title").replace("{home}", homeName));
        Inventory inv = Bukkit.createInventory(null, 27, title);

        inv.setItem(11, createItem(Material.matchMaterial(plugin.getConfig().getString("selection-menu.teleport-icon", "ENDER_PEARL")), plugin.getLangManager().getRaw("selection-menu.teleport-name"), ""));
        inv.setItem(15, createItem(Material.matchMaterial(plugin.getConfig().getString("selection-menu.delete-icon", "BARRIER")), plugin.getLangManager().getRaw("selection-menu.delete-name"), ""));
        
        p.openInventory(inv);
    }

    public static void openConfirmDelete(Player p, String homeName) {
        String title = MessageUtils.color(plugin.getLangManager().getRaw("confirm-gui.title").replace("{home}", homeName));
        Inventory inv = Bukkit.createInventory(null, 27, title);

        ItemStack yes = createItem(Material.LIME_STAINED_GLASS_PANE, plugin.getLangManager().getRaw("confirm-gui.yes-name"), plugin.getLangManager().getRaw("confirm-gui.yes-lore"));
        ItemStack no = createItem(Material.RED_STAINED_GLASS_PANE, plugin.getLangManager().getRaw("confirm-gui.no-name"), plugin.getLangManager().getRaw("confirm-gui.no-lore"));

        for (int i : new int[]{10,11,12}) inv.setItem(i, yes);
        for (int i : new int[]{14,15,16}) inv.setItem(i, no);

        p.openInventory(inv);
    }

    private static ItemStack createItem(Material m, String n, String l) {
        ItemStack i = new ItemStack(m != null ? m : Material.STONE);
        ItemMeta mt = i.getItemMeta();
        mt.setDisplayName(MessageUtils.color(n));
        if (!l.isEmpty()) mt.setLore(Collections.singletonList(MessageUtils.color(l)));
        i.setItemMeta(mt);
        return i;
    }
}
