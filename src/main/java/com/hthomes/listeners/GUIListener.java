package com.hthomes.listeners;

import com.hthomes.HTHomes;
import com.hthomes.managers.GUIManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class GUIListener implements Listener {
    private final HTHomes plugin;

    public GUIListener(HTHomes plugin) {
        this.plugin = plugin;
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

        Player p = (Player) e.getWhoClicked();
        String title = e.getView().getTitle();
        FileConfiguration config = plugin.getConfig();
        String disp = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());

        // --- DURUM 1: Ana Liste Menüsü ---
        if (title.contains("Homes")) {
            e.setCancelled(true);
            int page = 1; // Basitleştirilmiş sayfa kontrolü
            
            String nextTxt = ChatColor.stripColor(color(config.getString("gui.next-page")));
            String prevTxt = ChatColor.stripColor(color(config.getString("gui.prev-page")));

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

            // Herhangi bir ev ikonuna tıklandığında (Yönetim Menüsünü Aç)
            if (e.getCurrentItem().getItemMeta().hasDisplayName()) {
                GUIManager.openSelection(p, disp);
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
                return;
            }

            // İkon Değiştir (Painting) - YENİ
            if (type == Material.PAINTING) {
                GUIManager.openIconSelection(p, homeName);
                return;
            }

            // Sil (Skeleton Skull)
            Material delMat = Material.matchMaterial(config.getString("selection-menu.delete-icon", "SKELETON_SKULL"));
            if (type == delMat) {
                plugin.getHomeManager().deleteHome(p, homeName);
                p.closeInventory();
                p.sendMessage(color(config.getString("messages.home-deleted").replace("{home}", homeName)));
                p.playSound(p.getLocation(), org.bukkit.Sound.BLOCK_ANVIL_BREAK, 1f, 1f);
                GUIManager.openHomeList(p, 1);
            }

            // Geri Dön
            if (type == Material.BARRIER) {
                GUIManager.openHomeList(p, 1);
            }
        }
        // --- DURUM 3: İkon Seçim Menüsü ---
        else if (title.startsWith("Select Icon: ")) {
            e.setCancelled(true);
            String homeName = title.replace("Select Icon: ", "").trim();
            if (e.getCurrentItem().getType() == Material.BARRIER) {
                GUIManager.openSelection(p, homeName);
            } else {
                plugin.getHomeManager().setHomeIcon(p, homeName, e.getCurrentItem().getType());
                p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                GUIManager.openHomeList(p, 1);
            }
        }
    }

    private void startTeleport(Player p, Location loc, String homeName) {
        FileConfiguration config = plugin.getConfig();
        int delay = config.getInt("teleport.delay", 3);
        boolean useActionBar = config.getBoolean("teleport.use-action-bar", true);
        String prefix = color(config.getString("messages.prefix"));

        Location startLoc = p.getLocation();

        new BukkitRunnable() {
            int time = delay;
            @Override
            public void run() {
                if (startLoc.distance(p.getLocation()) > 0.5) {
                    p.sendMessage(prefix + color(config.getString("messages.teleport-cancelled")));
                    this.cancel();
                    return;
                }
                if (time <= 0) {
                    p.teleport(loc);
                    p.sendMessage(prefix + color(config.getString("messages.teleport-success").replace("{home}", homeName)));
                    if (useActionBar) p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a§lARRIVED!"));
                    this.cancel();
                } else {
                    if (useActionBar) p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§eTeleporting in: §6" + time));
                    p.playSound(p.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                    time--;
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
            }
