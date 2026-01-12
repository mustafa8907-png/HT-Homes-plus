package com.hthomes.managers;

import com.hthomes.HTHomes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GUIManager {

    // 1. Ana Menü (Ev Listesi)
    public static void openHomeList(Player p, int page) {
        HTHomes plugin = HTHomes.getInstance();
        LanguageManager lm = plugin.getLanguageManager();
        FileConfiguration config = plugin.getConfig();
        
        List<String> homeNames = new ArrayList<>(plugin.getHomeManager().getHomes(p).keySet());
        Collections.sort(homeNames);

        String title = lm.getMessage("gui.title").replace("{page}", String.valueOf(page));
        Inventory inv = Bukkit.createInventory(null, 27, title);

        // Arka plan dolgusu (2 parametreli çağırma)
        ItemStack filler = createItem(Material.matchMaterial(config.getString("gui.fill-item", "GRAY_STAINED_GLASS_PANE")), " ");
        for (int i = 0; i < 27; i++) inv.setItem(i, filler);

        int[] slots = {10, 11, 12, 13, 14, 15, 16};
        int startIndex = (page - 1) * 7;
        int limit = plugin.getHomeManager().getPlayerLimit(p);

        for (int i = 0; i < 7; i++) {
            int index = startIndex + i;
            if (index < homeNames.size()) {
                // Kayıtlı Ev (3 parametreli çağırma)
                inv.setItem(slots[i], createItem(Material.LIME_BED, "§a" + homeNames.get(index), lm.getMessage("gui.home-lore")));
            } else if (index == homeNames.size() && homeNames.size() < limit) {
                // Yeni Ev Ekleme (3 parametreli çağırma)
                inv.setItem(slots[i], createItem(Material.RED_BED, lm.getMessage("gui.set-home-name"), lm.getMessage("gui.set-home-lore")));
            }
        }

        // Sayfa okları (2 parametreli çağırma)
        if (page > 1) inv.setItem(18, createItem(Material.ARROW, lm.getMessage("gui.prev-page")));
        if (page < 5 && homeNames.size() > (startIndex + 7)) inv.setItem(26, createItem(Material.ARROW, lm.getMessage("gui.next-page")));

        p.openInventory(inv);
    }

    // 2. Seçim Menüsü (Yönetim)
    public static void openSelection(Player p, String homeName) {
        LanguageManager lm = HTHomes.getInstance().getLanguageManager();
        String title = lm.getMessage("selection-menu.title").replace("{home}", homeName);
        Inventory inv = Bukkit.createInventory(null, 9, title);

        inv.setItem(2, createItem(Material.ENDER_PEARL, lm.getMessage("selection-menu.teleport-name"), lm.getMessage("selection-menu.teleport-lore")));
        inv.setItem(6, createItem(Material.SKELETON_SKULL, lm.getMessage("selection-menu.delete-name"), lm.getMessage("selection-menu.delete-lore")));
        inv.setItem(4, createItem(Material.BARRIER, lm.getMessage("selection-menu.back-name"))); // 2 parametre

        p.openInventory(inv);
    }

    // --- YARDIMCI METOTLAR (Helper Methods) ---

    // 3 Parametreli Metot (Material, İsim, Lore)
    private static ItemStack createItem(Material m, String name, String lore) {
        ItemStack item = new ItemStack(m != null ? m : Material.STONE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null && !lore.isEmpty()) {
                List<String> l = new ArrayList<>();
                for (String line : lore.split("\n")) {
                    l.add(line);
                }
                meta.setLore(l);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    // 2 Parametreli Metot (Lore istemeyen eşyalar için)
    private static ItemStack createItem(Material m, String name) {
        return createItem(m, name, null);
    }
}
