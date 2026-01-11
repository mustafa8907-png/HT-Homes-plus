package com.hthomes.listeners;

import com.hthomes.HTHomes;
import com.hthomes.managers.GUIManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class GUIListener implements Listener {
    private final HTHomes pl;
    public GUIListener(HTHomes pl) { this.pl = pl; }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(pl.getLanguageManager().getRawString("gui.title"))) return;
        e.setCancelled(true);
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

        Player p = (Player) e.getWhoClicked();
        Material eMat = Material.matchMaterial(pl.getConfig().getString("gui.materials.empty", "RED_BED"));

        if (e.getCurrentItem().getType() == eMat) {
            p.closeInventory(); p.performCommand("home set");
        } else if (e.getCurrentItem().hasItemMeta()) {
            String n = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
            Location loc = pl.getHomeManager().getHomes(p).get(n);
            if (loc != null) {
                p.closeInventory();
                if (e.getClick() == ClickType.SHIFT_RIGHT) p.performCommand("home delete " + n);
                else teleport(p, loc, n);
            }
        }
    }

    private void teleport(Player p, Location t, String n) {
        int delay = pl.getConfig().getInt("homes.teleport-delay", 3);
        if (delay <= 0) { p.teleport(t); p.sendMessage(pl.getLanguageManager().getMessage("messages.teleport-success").replace("{home}", n)); return; }
        
        p.sendMessage(pl.getLanguageManager().getMessage("messages.teleporting").replace("{home}", n).replace("{time}", String.valueOf(delay)));
        Location start = p.getLocation().clone();
        new BukkitRunnable() {
            int time = delay;
            @Override
            public void run() {
                if (!p.isOnline() || p.getLocation().distance(start) > 0.5) { p.sendMessage(pl.getLanguageManager().getMessage("messages.teleport-cancelled")); cancel(); return; }
                if (time <= 0) { p.teleport(t); p.sendMessage(pl.getLanguageManager().getMessage("messages.teleport-success").replace("{home}", n)); GUIManager.playSound(p, "gui.sounds.click_teleport"); cancel(); return; }
                time--;
            }
        }.runTaskTimer(pl, 0, 20);
    }
                                           }
