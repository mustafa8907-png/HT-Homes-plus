package com.hthomes.listeners;

import com.hthomes.HTHomes;
import com.hthomes.managers.GUIManager;
import com.hthomes.utils.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Map;

public class GUIListener implements Listener {
    private final HTHomes plugin;
    public GUIListener(HTHomes plugin) { this.plugin = plugin; }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        Player p = (Player) e.getWhoClicked();
        String title = e.getView().getTitle();

        if (title.contains("Ev Listesi")) {
            e.setCancelled(true);
            if (e.getCurrentItem().getType() != Material.ARROW) {
                String name = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                GUIManager.openSelection(p, name);
            }
        } else if (title.contains("Yönet:")) {
            e.setCancelled(true);
            String name = title.split(": ")[1];
            if (e.getCurrentItem().getType() == Material.ENDER_PEARL) {
                p.closeInventory();
                teleportPlayer(p, name);
            } else if (e.getCurrentItem().getType() == Material.BARRIER) {
                GUIManager.openConfirmDelete(p, name);
            }
        } else if (title.contains("Sil:")) {
            e.setCancelled(true);
            String name = title.split(": ")[1];
            if (e.getCurrentItem().getType() == Material.LIME_STAINED_GLASS_PANE) {
                plugin.getHomeManager().deleteHome(p, name);
                plugin.getLangManager().sendMessage(p, "messages.home-deleted", Map.of("{home}", name));
                p.closeInventory();
            } else if (e.getCurrentItem().getType() == Material.RED_STAINED_GLASS_PANE) {
                GUIManager.openSelection(p, name);
            }
        }
    }

    private void teleportPlayer(Player p, String homeName) {
        org.bukkit.Location loc = plugin.getHomeManager().getHomes(p).get(homeName);
        if (!plugin.getHookManager().canBuild(p, loc)) {
            plugin.getLangManager().sendMessage(p, "messages.unsafe-area", null);
            return;
        }
        int delay = plugin.getConfig().getInt("teleport.delay", 3);
        new BukkitRunnable() {
            int count = delay;
            @Override
            public void run() {
                if (count <= 0) {
                    p.teleport(loc);
                    plugin.getLangManager().sendMessage(p, "messages.teleport-success", Map.of("{home}", homeName));
                    cancel();
                } else {
                    plugin.getAdventure().player(p).sendActionBar(MessageUtils.parse(p, plugin.getLangManager().getRaw("messages.teleporting").replace("{time}", String.valueOf(count))));
                    count--;
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }
}
