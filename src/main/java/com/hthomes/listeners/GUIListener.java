package com.hthomes.listeners;

import com.hthomes.HTHomes;
import com.hthomes.managers.GUIManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
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
        String title = ChatColor.stripColor(e.getView().getTitle());
        
        // Menü kontrolü
        if (title.startsWith("Evlerim")) {
            e.setCancelled(true); // Eşya çalmayı engelle
            
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
            Player p = (Player) e.getWhoClicked();
            if (e.getClickedInventory() == p.getInventory()) return;

            String disp = e.getCurrentItem().getItemMeta().getDisplayName();

            // Sayfa numarası bulma
            int page = 1;
            try {
                String pStr = title.substring(title.indexOf("Sayfa") + 6).replace(")", "").trim();
                page = Integer.parseInt(pStr);
            } catch (Exception ignored) {}

            // Buton İşlemleri
            if (disp.equals("§cKapat")) {
                p.closeInventory();
            } 
            else if (disp.equals("§eSonraki Sayfa")) {
                GUIManager.open(p, page + 1);
            } 
            else if (disp.equals("§eÖnceki Sayfa")) {
                GUIManager.open(p, page - 1);
            } 
            // YENİ ÖZELLİK: Tıkla ve Oluştur
            else if (disp.equals("§a+ Yeni Ev Oluştur")) {
                p.closeInventory();
                int nextNum = pl.getHomeManager().getHomes(p).size() + 1;
                p.performCommand("sethome home" + nextNum);
            }
            // Mevcut Evler (Yataklar)
            else if (disp.startsWith("§6")) {
                String hName = ChatColor.stripColor(disp);
                
                if (e.getClick() == ClickType.SHIFT_RIGHT) {
                    p.closeInventory();
                    p.performCommand("delhome " + hName);
                } else {
                    p.closeInventory();
                    Location loc = pl.getHomeManager().getHomes(p).get(hName);
                    if (loc != null) teleport(p, loc, hName);
                }
            }
        }
    }

    private void teleport(Player p, Location t, String n) {
        int delay = pl.getConfig().getInt("teleport.delay", 3);
        boolean useActionBar = pl.getConfig().getBoolean("teleport.use-action-bar", true);

        if (delay <= 0) {
            p.teleport(t);
            p.sendMessage(pl.getLanguageManager().getMessage("messages.teleport-success").replace("{home}", n));
            return;
        }

        Location start = p.getLocation();
        new BukkitRunnable() {
            int time = delay;
            @Override
            public void run() {
                if (!p.isOnline() || start.distance(p.getLocation()) > 0.5) {
                    p.sendMessage(pl.getLanguageManager().getMessage("messages.teleport-cancelled"));
                    this.cancel();
                    return;
                }
                if (time <= 0) {
                    p.teleport(t);
                    p.sendMessage(pl.getLanguageManager().getMessage("messages.teleport-success").replace("{home}", n));
                    GUIManager.playSound(p, "gui.sounds.click_teleport");
                    this.cancel();
                    return;
                }

                String msg = pl.getLanguageManager().getMessage("messages.teleporting")
                        .replace("{home}", n)
                        .replace("{time}", String.valueOf(time));

                if (useActionBar) {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
                } else {
                    p.sendMessage(msg);
                }
                time--;
            }
        }.runTaskTimer(pl, 0, 20);
    }
            }
