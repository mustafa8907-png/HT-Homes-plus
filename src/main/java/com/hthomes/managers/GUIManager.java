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
        
        List<String> homeNames = new ArrayList<>(plugin.getHomeManager().getHomes(p).keySet());
        Collections.sort(homeNames);

        String title = plugin.getLanguageManager().getRawString("gui.title").replace("{page}", String.valueOf(page));
        Inventory inv = Bukkit.createInventory(null, 27, title);

        // 1. Kenarlıkları Doldur
        String fillStr = plugin.getConfig().getString("gui.fill-item", "GRAY_STAINED_GLASS_PANE");
        Material fillMat = Material.matchMaterial(fillStr);
        if (fillMat == null) fillMat = Material.GRAY_STAINED_GLASS_PANE;
        ItemStack filler = createItem(fillMat, " ");
        
        for (int i = 0; i < 9; i++) inv.setItem(i, filler);
        for (int i = 18; i < 27; i++) inv.setItem(i, filler);

        // 2. Yatak ve Oluşturma Eşyası
        String bedStr = plugin.getConfig().getString("gui.home-item", "RED_BED");
        Material bedMat = Material.matchMaterial(bedStr);
        if (bedMat == null) bedMat = Material.RED_BED; // Varsayılan Kırmızı

        // 3. Sayfa Matematiği (Her sayfada 7 ev + 2 buton yeri gibi düşünmeyelim, 9 slot var)
        int slotsPerPage = 9;
        int startIndex = (page - 1) * slotsPerPage;
        
        // Bu sayfada gösterilecek slotlar (9-17 arası)
        int guiSlot = 9;
        
        for (int i = 0; i < slotsPerPage; i++) {
            int realHomeIndex = startIndex + i;
            
            // Eğer ev varsa -> YATAK KOY
            if (realHomeIndex < homeNames.size()) {
                String hName = homeNames.get(realHomeIndex);
                inv.setItem(guiSlot, createItem(bedMat, "§6" + hName, "§7Tıkla ve Işınlan!", "§cSilmek için Shift+Sağ Tık"));
            } 
            // Eğer ev yoksa ama limit dolmamışsa -> OLUŞTUR BUTONU KOY
            else if (realHomeIndex == homeNames.size() && homeNames.size() < plugin.getHomeManager().getPlayerLimit(p)) {
                inv.setItem(guiSlot, createItem(Material.LIME_DYE, "§a+ Yeni Ev Oluştur", "§7Tıkla ve bu noktaya ev kaydet"));
            }
            
            guiSlot++;
        }

        // 4. Sayfa Butonları
        if (page > 1) {
            inv.setItem(18, createItem(Material.ARROW, "§eÖnceki Sayfa"));
        }
        
        // Eğer daha fazla ev varsa "Sonraki" butonunu koy
        if (homeNames.size() > (startIndex + slotsPerPage)) {
            inv.setItem(26, createItem(Material.ARROW, "§eSonraki Sayfa"));
        }

        inv.setItem(22, createItem(Material.BARRIER, "§cKapat"));

        p.openInventory(inv);
    }

    public static void playSound(Player p, String path) {
        try {
            String s = HTHomes.getInstance().getLanguageManager().getRawString(path);
            if (s != null && !s.isEmpty()) p.playSound(p.getLocation(), Sound.valueOf(s), 1f, 1f);
        } catch (Exception ignored) {}
    }

    private static ItemStack createItem(Material m, String name, String... lore) {
        ItemStack item = new ItemStack(m);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            List<String> l = new ArrayList<>();
            for (String line : lore) l.add(line);
            meta.setLore(l);
            item.setItemMeta(meta);
        }
        return item;
    }
    }
