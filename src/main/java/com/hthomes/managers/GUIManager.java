package com.hthomes.managers;

import com.hthomes.HTHomes;
import com.hthomes.utils.MessageUtils;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
        
        // Başlığı MiniMessage olarak gönderiyoruz
        // Not: Bazı sürümler GUI başlığında Component desteklemediği için 
        // buradaki görünüm kullandığın Spigot/Paper forkuna bağlıdır.
        Inventory gui = Bukkit.createInventory(null, 27, LegacyComponentSerializer.legacySection().serialize(MessageUtils.parse(player, titleRaw)));

        List<String> homes = plugin.getHomeManager().getPlayerHomes(player.getUniqueId());
        
        // 1.12 için materyal güvenliği
        Material bedIcon = Material.matchMaterial("RED_BED") != null ? Material.matchMaterial("RED_BED") : Material.BED;

        for (int i = 0; i < 27; i++) {
            if (i < homes.size()) {
                String homeName = homes.get(i);
                gui.setItem(i, createItem(player, bedIcon, "<green>" + homeName, plugin.getLangManager().getRaw("gui.home-lore")));
            } else {
                // Boş slotlara Kırmızı Yatak ve Yeni Ev Kur mesajı
                gui.setItem(i, createItem(player, bedIcon, "<red>Yeni Ev Kur", "<gray>Tıkla ve sethome kur!"));
            }
        }
        player.openInventory(gui);
    }

    private static ItemStack createItem(Player p, Material m, String n, String l) {
        ItemStack i = new ItemStack(m != null ? m : Material.STONE);
        ItemMeta mt = i.getItemMeta();
        if (mt != null) {
            // Eşya isimlerinde gradientleri korumak için
            mt.setDisplayName(LegacyComponentSerializer.legacySection().serialize(MessageUtils.parse(p, n)));
            if (l != null && !l.isEmpty()) {
                mt.setLore(Collections.singletonList(LegacyComponentSerializer.legacySection().serialize(MessageUtils.parse(p, l))));
            }
            i.setItemMeta(mt);
        }
        return i;
    }
}
