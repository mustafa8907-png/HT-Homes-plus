package com.hthomes.managers;

import com.hthomes.HTHomes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GUIManager {

    public static void open(Player p, int page) {
        HTHomes plugin = HTHomes.getInstance();
        
        // Evlerin listesini al
        List<String> homeNames = new ArrayList<>(plugin.getHomeManager().getHomes(p).keySet());
        Collections.sort(homeNames);

        // GUI Ayarları
        int rows = 3; 
        Inventory inv = Bukkit.createInventory(null, rows * 9, 
                plugin.getLanguageManager().getRawString("gui.title").replace("{page}", String.valueOf(page)));

        // Kenarlıkları Camla Doldur
        String fillMatName = plugin.getConfig().getString("gui.fill-item", "GRAY_STAINED_GLASS_PANE");
        ItemStack filler = createItem(Material.matchMaterial(fillMatName), " ");
        
        for (int i = 0; i < 9; i++) inv.setItem(i, filler); // Üst sıra
        for (int i = 18; i < 27; i++) inv.setItem(i, filler); // Alt sıra

        // Sayfalama Mantığı
        int itemsPerPage = 9; // Orta sıra (9-17 arası)
        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, homeNames.size());

        String bedMatName = plugin.getConfig().getString("gui.home-item", "RED_BED");
        Material bedMat = Material.matchMaterial(bedMatName);
        if (bedMat == null) bedMat = Material.RED_BED;

        int slot = 9; // Ortadaki sıranın başı
        for (int i = startIndex; i < endIndex; i++) {
            String homeName = homeNames.get(i);
            inv.setItem(slot, createItem(bedMat, "§6" + homeName, "§7Tıkla ve ışınlan!", "§cSilmek için Shift+Sağ Tık"));
            slot++;
        }

        // Sayfa Butonları
        if (page > 1) {
            inv.setItem(18, createItem(Material.ARROW, "§eÖnceki Sayfa"));
        }
        if (endIndex < homeNames.size()) {
            inv.setItem(26, createItem(Material.ARROW, "§eSonraki Sayfa"));
        }

        // Ortaya 'Bilgi' veya 'Kapat' butonu
        inv.setItem(22, createItem(Material.BARRIER, "§cKapat"));

        p.openInventory(inv);
    }

    public static void playSound(Player p, String path) {
        try {
            String soundName = HTHomes.getInstance().getLanguageManager().getRawString(path);
            if (soundName != null && !soundName.isEmpty()) {
                p.playSound(p.getLocation(), Sound.valueOf(soundName), 1f, 1f);
            }
        } catch (Exception ignored) {}
    }

    private static ItemStack createItem(Material m, String name, String... lore) {
        ItemStack item = new ItemStack(m);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            List<String> loreList = new ArrayList<>();
            for (String l : lore) loreList.add(l);
            meta.setLore(loreList);
            item.setItemMeta(meta);
        }
        return item;
    }
}
