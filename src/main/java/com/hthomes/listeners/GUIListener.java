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
        
        if (!e.getCurrentItem().hasItemMeta()) return;
        String itemName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());

        if (title.contains("Ev Menüsü")) {
            e.setCancelled(true);
            if (mat == Material.RED_BED) {
                plugin.getHomeManager().setHome(p.getUniqueId(), itemName, p.getLocation());
                p.closeInventory();
                p.sendMessage(MessageUtils.color("&a" + itemName + " başarıyla kuruldu!"));
            } else if (mat == Material.LIME_BED) {
                GUIManager.openSelection(p, itemName);
            }
        } 
        else if (title.contains("Yönet:")) {
            e.setCancelled(true);
            String h = title.split(": ")[1];
            if (itemName.contains("Işınlan")) {
                p.closeInventory();
                TeleportManager.teleportWithCountdown(p, plugin.getHomeManager().getHome(p.getUniqueId(), h));
            } else if (itemName.contains("Sil")) {
                GUIManager.openConfirmDelete(p, h);
            }
        }
        else if (title.contains("Silme Onayı:")) {
            e.setCancelled(true);
            String h = title.split(": ")[1];
            if (itemName.contains("Onayla")) {
                plugin.getHomeManager().deleteHome(p.getUniqueId(), h);
                p.closeInventory();
                p.sendMessage(MessageUtils.color("&c" + h + " başarıyla silindi."));
            } else {
                GUIManager.openSelection(p, h);
            }
        }
    
    }
                                               }
