package com.hthomes.listeners;

import com.hthomes.HTHomes;
import com.hthomes.managers.GUIManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
    public void onClick(InventoryClickEvent e) {
        String title = ChatColor.stripColor(e.getView().getTitle());
        FileConfiguration config = plugin.getConfig();
        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

        // --- DURUM 1: Ana Menü (Home List) ---
        if (title.contains("Home List")) {
            e.setCancelled(true);
            String disp = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());

            // Sayfalama
            int page = 1;
            try { page = Integer.parseInt(title.split("Page ")[1]); } catch (Exception ignored) {}
            
            String nextTxt = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', config.getString("gui.next-page")));
            String prevTxt = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', config.getString("gui.prev-page")));

            if (disp.contains(nextTxt)) { GUIManager.openHomeList(p, page + 1); return; }
            if (disp.contains(prevTxt)) { GUIManager.openHomeList(p, page - 1); return; }

            // Kırmızı Yatak -> Yeni Ev Kur
            Material redBed = Material.matchMaterial(config.getString("gui.set-home-icon", "RED_BED"));
            if (e.getCurrentItem().getType() == redBed) {
                p.closeInventory();
                int nextNum = plugin.getHomeManager().getHomes(p).size() + 1;
                String homeName = "Home-" + nextNum;
                plugin.getHomeManager().setHome(p, homeName, p.getLocation());
                p.sendMessage(color(config.getString("messages.home-set").replace("{home}", homeName)));
                p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                return;
            }

            // Yeşil Yatak -> Seçim Menüsünü Aç (Geyser Friendly)
            Material greenBed = Material.matchMaterial(config.getString("gui.home-icon", "LIME_BED"));
            if (e.getCurrentItem().getType() == greenBed) {
                // Yatağın ismi direkt ev ismidir (renk kodsuz)
                String homeName = disp; 
                GUIManager.openSelection(p, homeName);
            }
        }

        // --- DURUM 2: Seçim Menüsü (Manage: Home-1) ---
        else if (title.startsWith("Manage: ")) {
            e.setCancelled(true);
            String homeName = title.replace("Manage: ", "").trim();
            Material type = e.getCurrentItem().getType();

            // Işınlan (Ender Pearl)
            Material tpMat = Material.matchMaterial(config.getString("selection-menu.teleport-icon", "ENDER_PEARL"));
            if (type == tpMat) {
                p.closeInventory();
                Location loc = plugin.getHomeManager().getHomes(p).get(homeName);
                if (loc != null) startTeleport(p, loc, homeName);
            }

            // Sil (Skeleton Skull)
            Material delMat = Material.matchMaterial(config.getString("selection-menu.delete-icon", "SKELETON_SKULL"));
            if (type == delMat) {
                plugin.getHomeManager().deleteHome(p, homeName);
                p.closeInventory();
                p.sendMessage(color(config.getString("messages.home-deleted").replace("{home}", homeName)));
                p.playSound(p.getLocation(), org.bukkit.Sound.BLOCK_ANVIL_BREAK, 1f, 1f);
                // Silince listeye geri dön
                GUIManager.openHomeList(p, 1);
            }

            // Geri Dön (Back)
            Material backMat = Material.matchMaterial(config.getString("selection-menu.back-icon", "BARRIER"));
            if (type == backMat) {
                GUIManager.openHomeList(p, 1);
            }
        }
    }

    private void startTeleport(Player p, Location loc, String homeName) {
        FileConfiguration config = plugin.getConfig();
        int delay = config.getInt("teleport.delay", 5);
        boolean useActionBar = config.getBoolean("teleport.use-action-bar", true);
        boolean cancelOnMove = config.getBoolean("teleport.cancel-on-move", true);
        String prefix = color(config.getString("messages.prefix"));

        if (delay <= 0 || p.hasPermission("hthomes.bypass.delay")) {
            p.teleport(loc);
            p.sendMessage(prefix + color(config.getString("messages.teleport-success").replace("{home}", homeName)));
            return;
        }

        Location start = p.getLocation();
        new BukkitRunnable() {
            int time = delay;
            @Override
            public void run() {
                if (cancelOnMove && start.distance(p.getLocation()) > 0.5) {
                    p.sendMessage(prefix + color(config.getString("messages.teleport-cancelled")));
                    if (useActionBar) p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
                    this.cancel();
                    return;
                }
                if (time <= 0) {
                    p.teleport(loc);
                    p.sendMessage(prefix + color(config.getString("messages.teleport-success").replace("{home}", homeName)));
                    if (useActionBar) p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
                    this.cancel();
                    return;
                }
                String msg = color(config.getString("messages.teleporting").replace("{time}", String.valueOf(time)));
                if (useActionBar) p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
                else p.sendMessage(prefix + msg);
                time--;
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    private String color(String s) { return ChatColor.translateAlternateColorCodes('&', s); }
            }
