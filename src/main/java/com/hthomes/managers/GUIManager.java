package com.hthomes.managers;

import com.hthomes.HTHomes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GUIManager {
    private static HTHomes plugin;
    
    // İkon Seçenekleri
    private static final Material[] ICON_OPTIONS = {
        Material.CRAFTING_TABLE, Material.OAK_LOG, Material.GOLDEN_APPLE, Material.ENCHANTED_GOLDEN_APPLE,
        Material.WOODEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE, 
        Material.RED_BED, Material.LIME_BED, Material.BLUE_BED, Material.YELLOW_BED, Material.CYAN_BED,
        Material.ENDER_PEARL, Material.BOOKSHELF, Material.CHEST, Material.BEACON
    };

    public static void setPlugin(HTHomes p) { plugin = p; }

    // --- ANA MENÜ (Sayfalamalı) ---
    public static void openHomeList(Player p, int page) {
        FileConfiguration config = plugin.getConfig();
        String title = ChatColor.translateAlternateColorCodes('&', config.getString("gui.title", "Homes ({page})").replace("{page}", String.valueOf(page)));
        Inventory inv = Bukkit.createInventory(null, 54, title);

        Map<String, org.bukkit.Location> homes = plugin.getHomeManager().getHomes(p);
        List<String> homeNames = new ArrayList<>(homes.keySet());

        int start = (page - 1) * 28;
        int end = Math.min(start + 28, homeNames.size());
        int[] slots = {10,11,12,13,14,15,16, 19,20,21,22,23,24,25, 28,29,30,31,32,33,34, 37,38,39,40,41,42,43};

        // Evleri Döngüye Sok
        for (int i = 0; i < (end - start); i++) {
            String homeName = homeNames.get(start + i);
            
            // YENİ: Sabit ikon yerine kayıtlı ikonu çekiyoruz
            Material iconMat = plugin.getHomeManager().getHomeIcon(p, homeName);
            
            inv.setItem(slots[i], createItem(iconMat, "§a" + homeName, ChatColor.translateAlternateColorCodes('&', config.getString("gui.home-lore", "&7Click to manage"))));
        }

        // Boş Slotlar (Yeni Ev Ekle)
        Material setIcon = Material.matchMaterial(config.getString("gui.set-home-icon", "RED_BED"));
        for (int i = (end - start); i < slots.length; i++) {
            inv.setItem(slots[i], createItem(setIcon, ChatColor.translateAlternateColorCodes('&', config.getString("gui.set-home-name", "&cEmpty Slot")), ChatColor.translateAlternateColorCodes('&', config.getString("gui.set-home-lore", "&7Click to set home"))));
        }

        // Sayfa Butonları
        if (page > 1) inv.setItem(45, createItem(Material.ARROW, ChatColor.translateAlternateColorCodes('&', config.getString("gui.prev-page", "&ePrevious"))));
        if (end < homeNames.size()) inv.setItem(53, createItem(Material.ARROW, ChatColor.translateAlternateColorCodes('&', config.getString("gui.next-page", "&eNext"))));

        p.openInventory(inv);
    }

    // --- YÖNETİM MENÜSÜ ---
    public static void openSelection(Player p, String homeName) {
        FileConfiguration config = plugin.getConfig();
        Inventory inv = Bukkit.createInventory(null, 27, "Manage: " + homeName);

        Material tpMat = Material.matchMaterial(config.getString("selection-menu.teleport-icon", "ENDER_PEARL"));
        inv.setItem(11, createItem(tpMat, "§aTeleport", "§7Go to home"));

        // YENİ: İkon Değiştirme Butonu
        inv.setItem(13, createItem(Material.PAINTING, "§eChange Icon", "§7Customize icon"));

        Material delMat = Material.matchMaterial(config.getString("selection-menu.delete-icon", "SKELETON_SKULL"));
        inv.setItem(15, createItem(delMat, "§cDelete", "§7Delete home"));

        Material backMat = Material.matchMaterial(config.getString("selection-menu.back-icon", "BARRIER"));
        inv.setItem(26, createItem(backMat, "§cBack", "§7Go back"));

        p.openInventory(inv);
    }

    // --- YENİ: İKON SEÇİM MENÜSÜ ---
    public static void openIconSelection(Player p, String homeName) {
        Inventory inv = Bukkit.createInventory(null, 36, "Select Icon: " + homeName);
        for (Material m : ICON_OPTIONS) {
            inv.addItem(createItem(m, "§eSelect", "§7Click to use this icon"));
        }
        inv.setItem(35, createItem(Material.BARRIER, "§cCancel"));
        p.openInventory(inv);
    }

    private static ItemStack createItem(Material m, String name, String... lore) {
        ItemStack i = new ItemStack(m != null ? m : Material.STONE);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(name);
        List<String> l = new ArrayList<>();
        for (String s : lore) l.add(s);
        meta.setLore(l);
        i.setItemMeta(meta);
        return i;
    }
        }
