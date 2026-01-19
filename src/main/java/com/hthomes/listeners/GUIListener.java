package com.hthomes.listeners;

import com.hthomes.HTHomes;
import com.hthomes.managers.GUIManager;
import com.hthomes.managers.TeleportManager;
import com.hthomes.utils.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {
    private final HTHomes plugin;
    public GUIListener(HTHomes plugin) { this.plugin = plugin; }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null || e.getCurrentItem() == null) return;
        Player p = (Player) e.getWhoClicked();
        String title = e.getView().getTitle();
        Material mat = e.getCurrentItem().getType();
        String itemName = e.getCurrentItem().getItemMeta().getDisplayName();
        String cleanItemName = ChatColor.stripColor(itemName);

        // ANA LİSTE
        if (title.equals(MessageUtils.color(plugin.getLangManager().getRaw(p, "gui.title")))) {
            e.setCancelled(true);
            if (mat == Material.RED_BED) {
                plugin.getHomeManager().setHome(p.getUniqueId(), cleanItemName, p.getLocation());
                p.closeInventory();
                plugin.getLangManager().sendMessage(p, "messages.home-set", java.util.Map.of("{home}", cleanItemName));
            } else if (mat != Material.GRAY_STAINED_GLASS_PANE) {
                GUIManager.openSelection(p, cleanItemName);
            }
        } 
        // SEÇİM MENÜSÜ
        else if (title.contains(MessageUtils.color(plugin.getLangManager().getRaw(p, "selection-menu.title").split("\\{")[0]))) {
            e.setCancelled(true);
            String homeName = ChatColor.stripColor(title.split(": ")[1]);
            if (mat == Material.ENDER_PEARL) {
                p.closeInventory();
                TeleportManager.teleportWithCountdown(p, plugin.getHomeManager().getHome(p.getUniqueId(), homeName));
            } else if (mat == Material.PAINTING) {
                GUIManager.openIconMenu(p, homeName);
            } else if (mat == Material.BARRIER) {
                GUIManager.openConfirmDelete(p, homeName);
            }
        }
        // İKON MENÜSÜ
        else if (title.contains(MessageUtils.color(plugin.getLangManager().getRaw(p, "icon-menu.title").split("\\{")[0]))) {
            e.setCancelled(true);
            String homeName = ChatColor.stripColor(title.split(": ")[1]);
            plugin.getHomeManager().setIcon(p.getUniqueId(), homeName, mat);
            p.closeInventory();
            plugin.getLangManager().sendMessage(p, "messages.icon-changed", null);
            GUIManager.openHomeList(p);
        }
        // SİLME ONAYI
        else if (title.contains(MessageUtils.color(plugin.getLangManager().getRaw(p, "confirm-gui.title").split("\\{")[0]))) {
            e.setCancelled(true);
            String homeName = ChatColor.stripColor(title.split(": ")[1]);
            if (mat == Material.LIME_STAINED_GLASS_PANE) {
                plugin.getHomeManager().deleteHome(p.getUniqueId(), homeName);
                p.closeInventory();
                plugin.getLangManager().sendMessage(p, "messages.home-deleted", java.util.Map.of("{home}", homeName));
            } else {
                GUIManager.openSelection(p, homeName);
            }
        }
    }
}
