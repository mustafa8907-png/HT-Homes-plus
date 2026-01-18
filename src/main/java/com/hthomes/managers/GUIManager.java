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

    public static void openHomeList(Player player, int page) {
        String titleRaw = plugin.getLangManager().getRaw("gui.title").replace("{page}", String.valueOf(page));
        // FIX: 27 slots = 3 rows as requested. parseToLegacy used for title gradients.
        Inventory gui = Bukkit.createInventory(null, 27, MessageUtils.parseToLegacy(player, titleRaw));

        // Background filler (Optional)
        for (int i = 0; i < 27; i++) {
            gui.setItem(i, createItem(Material.GRAY_STAINED_GLASS_PANE, " ", ""));
        }

        player.openInventory(gui);
    }

    public static void openSelectionMenu(Player p, String homeName) {
        String title = MessageUtils.parseToLegacy(p, plugin.getLangManager().getRaw("selection-menu.title").replace("{home}", homeName));
        Inventory inv = Bukkit.createInventory(null, 27, title);

        inv.setItem(11, createItem(Material.matchMaterial(plugin.getConfig().getString("selection-menu.teleport-icon", "ENDER_PEARL")), 
                plugin.getLangManager().getRaw("selection-menu.teleport-name"), ""));
        
        inv.setItem(15, createItem(Material.matchMaterial(plugin.getConfig().getString("selection-menu.delete-icon", "BARRIER")), 
                plugin.getLangManager().getRaw("selection-menu.delete-name"), ""));
        
        p.openInventory(inv);
    }

    public static void openConfirmDelete(Player p, String homeName) {
        // Fix: Use parseToLegacy for gradient title support
        String title = MessageUtils.parseToLegacy(p, plugin.getLangManager().getRaw("confirm-gui.title").replace("{home}", homeName));
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
        if (mt != null) {
            // Fix: Using MessageUtils.color to handle gradient names and lore
            mt.setDisplayName(MessageUtils.color(n));
            if (l != null && !l.isEmpty()) {
                mt.setLore(Collections.singletonList(MessageUtils.color(l)));
            }
            i.setItemMeta(mt);
        }
        return i;
    }
}
