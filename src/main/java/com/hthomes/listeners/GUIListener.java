package com.hthomes.listeners;

import com.hthomes.HTHomes;
import com.hthomes.managers.GUIManager;
import com.hthomes.managers.TeleportManager; // IMPORT EKLENDİ
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
        if (!e.getCurrentItem().hasItemMeta()) return;

        Material mat = e.getCurrentItem().getType();
        String itemName = e.getCurrentItem().getItemMeta().getDisplayName();
        String cleanItemName = ChatColor.stripColor(itemName);

        String guiTitle = MessageUtils.color(plugin.getLangManager().getRaw(p, "gui.title"));
        
        if (title.equals(guiTitle)) {
            e.setCancelled(true);
            if (mat == Material.RED_BED) {
                plugin.getHomeManager().setHome(p.getUniqueId(), cleanItemName, p.getLocation());
                p.closeInventory();
                plugin.getLangManager().sendMessage(p, "messages.home-set", java.util.Map.of("{home}", cleanItemName));
            } else if (mat == Material.LIME_BED) {
                GUIManager.openSelection(p, cleanItemName);
            }
        } 
        else if (title.contains(MessageUtils.color(plugin.getLangManager().getRaw(p, "selection-menu.title").split("\\{")[0]))) {
            e.setCancelled(true);
            String homeName = ChatColor.stripColor(title.substring(title.lastIndexOf(" ") + 1));
            
            String tpBtn = MessageUtils.color(plugin.getLangManager().getRaw(p, "selection-menu.teleport-name"));
            String delBtn = MessageUtils.color(plugin.getLangManager().getRaw(p, "selection-menu.delete-name"));

            if (itemName.equals(tpBtn)) {
                p.closeInventory();
                TeleportManager.teleportWithCountdown(p, plugin.getHomeManager().getHome(p.getUniqueId(), homeName));
            } else if (itemName.equals(delBtn)) {
                GUIManager.openConfirmDelete(p, homeName);
            }
        }
        else if (title.contains(MessageUtils.color(plugin.getLangManager().getRaw(p, "confirm-gui.title").split("\\{")[0]))) {
            e.setCancelled(true);
            String homeName = ChatColor.stripColor(title.substring(title.lastIndexOf(" ") + 1));
            
            String yesBtn = MessageUtils.color(plugin.getLangManager().getRaw(p, "confirm-gui.yes-name"));

            if (itemName.equals(yesBtn)) {
                plugin.getHomeManager().deleteHome(p.getUniqueId(), homeName);
                p.closeInventory();
                plugin.getLangManager().sendMessage(p, "messages.home-deleted", java.util.Map.of("{home}", homeName));
            } else {
                GUIManager.openSelection(p, homeName);
            }
        }
    }
}
