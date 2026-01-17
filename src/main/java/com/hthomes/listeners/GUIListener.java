package com.hthomes.listeners;

import com.hthomes.HTHomes;
import com.hthomes.managers.GUIManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class GUIListener implements Listener {
    private final HTHomes plugin;

    public GUIListener(HTHomes plugin) { this.plugin = plugin; }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

        Player p = (Player) e.getWhoClicked();
        String title = e.getView().getTitle();
        FileConfiguration config = plugin.getConfig();
        String disp = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());

        // 1. ANA LİSTE MENÜSÜ
        if (title.contains("Homes")) {
            e.setCancelled(true);
            int page = 1; 
            try { page = Integer.parseInt(title.replaceAll("[^0-9]", "")); } catch (Exception ignored) {}

            String next = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', config.getString("gui.next-page", "Next")));
            String prev = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', config.getString("gui.prev-page", "Previous")));

            if (disp.contains(next)) { GUIManager.openHomeList(p, page + 1); return; }
            if (disp.contains(prev)) { GUIManager.openHomeList(p, page - 1); return; }

            Material setMat = Material.matchMaterial(config.getString("gui.set-home-icon", "RED_BED"));
            if (e.getCurrentItem().getType() == setMat) {
                p.closeInventory();
                int num = plugin.getHomeManager().getHomes(p).size() + 1;
                plugin.getHomeManager().setHome(p, "Home-" + num, p.getLocation());
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.home-set", "&aHome set!").replace("{home}", "Home-" + num)));
                return;
            }

            // Ev İkonuna Tıklayınca Yönetimi Aç
            if (e.getCurrentItem().getItemMeta().hasDisplayName()) {
                GUIManager.openSelection(p, disp);
            }
        }

        // 2. YÖNETİM MENÜSÜ
        else if (title.startsWith("Manage: ")) {
            e.setCancelled(true);
            String homeName = title.replace("Manage: ", "").trim();
            Material type = e.getCurrentItem().getType();

            Material tpMat = Material.matchMaterial(config.getString("selection-menu.teleport-icon", "ENDER_PEARL"));
            if (type == tpMat) {
                p.closeInventory();
                Location loc = plugin.getHomeManager().getHomes(p).get(homeName);
                if (loc != null) startTeleport(p, loc, homeName);
                return;
            }

            // YENİ: İkon Değiştir Butonu
            if (type == Material.PAINTING) {
                GUIManager.openIconSelection(p, homeName);
                return;
            }

            Material delMat = Material.matchMaterial(config.getString("selection-menu.delete-icon", "SKELETON_SKULL"));
            if (type == delMat) {
                plugin.getHomeManager().deleteHome(p, homeName);
                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1f, 1f);
                GUIManager.openHomeList(p, 1);
                return;
            }

            Material backMat = Material.matchMaterial(config.getString("selection-menu.back-icon", "BARRIER"));
            if (type == backMat) {
                GUIManager.openHomeList(p, 1);
            }
        }

        // 3. YENİ: İKON SEÇİM MENÜSÜ
        else if (title.startsWith("Select Icon: ")) {
            e.setCancelled(true);
            String homeName = title.replace("Select Icon: ", "").trim();

            if (e.getCurrentItem().getType() == Material.BARRIER) {
                GUIManager.openSelection(p, homeName);
            } else {
                // Seçilen ikonu kaydet
                plugin.getHomeManager().setHomeIcon(p, homeName, e.getCurrentItem().getType());
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.icon-changed", "&aIcon changed!")));
                GUIManager.openHomeList(p, 1);
            }
        }
    }

    // GÜNCELLENMİŞ: Action Bar Destekli Işınlanma
    private void startTeleport(Player p, Location loc, String homeName) {
        FileConfiguration config = plugin.getConfig();
        int delay = config.getInt("teleport.delay", 3);
        boolean useActionBar = config.getBoolean("teleport.use-action-bar", true);
        String prefix = ChatColor.translateAlternateColorCodes('&', config.getString("messages.prefix", "&8[&aHomes&8] "));

        if (p.hasPermission("hthomes.bypass.delay")) delay = 0;

        if (delay <= 0) {
            p.teleport(loc);
            p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', config.getString("messages.teleport-success", "&aTeleported!").replace("{home}", homeName)));
            return;
        }

        Location startLoc = p.getLocation();
        p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', config.getString("messages.teleport-starting", "&aTeleporting... Don't move!")));

        new BukkitRunnable() {
            int time = config.getInt("teleport.delay", 3);
            @Override
            public void run() {
                if (!p.isOnline()) { this.cancel(); return; }
                
                // Hareket Kontrolü
                if (startLoc.distance(p.getLocation()) > 0.5) {
                    p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', config.getString("messages.teleport-cancelled", "&cCancelled!")));
                    if (useActionBar) p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§c❌ MOVED!"));
                    this.cancel();
                    return;
                }

                if (time <= 0) {
                    p.teleport(loc);
                    p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                    p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', config.getString("messages.teleport-success", "&aArrived!").replace("{home}", homeName)));
                    if (useActionBar) p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a✔ WELCOME HOME!"));
                    this.cancel();
                } else {
                    if (useActionBar) p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§eTeleporting in: §6" + time));
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                    time--;
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}
