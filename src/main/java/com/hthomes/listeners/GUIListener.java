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

    public GUIListener(HTHomes pl) {
        this.pl = pl;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        String title = ChatColor.stripColor(e.getView().getTitle());
        if (!title.startsWith("Evlerim")) return;

        e.setCancelled(true);
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

        Player p = (Player) e.getWhoClicked();
        String displayName = e.getCurrentItem().getItemMeta().getDisplayName();
        
        // Sayfa numarasını başlıktan çekme
        int currentPage = 1;
        try {
            String pageStr = title.substring(title.indexOf("Sayfa") + 6).replace(")", "").trim();
            currentPage = Integer.parseInt(pageStr);
        } catch (Exception ignored) {}

        if (displayName.equals("§cKapat")) {
            p.closeInventory();
        } 
        else if (displayName.equals("§eSonraki Sayfa")) {
            GUIManager.open(p, currentPage + 1);
        } 
        else if (displayName.equals("§eÖnceki Sayfa")) {
            GUIManager.open(p, currentPage - 1);
        } 
        else if (displayName.startsWith("§6")) {
            // Ev seçildi
            String homeName = ChatColor.stripColor(displayName);
            
            // Shift + Sağ Tık ise sil
            if (e.getClick() == ClickType.SHIFT_RIGHT) {
                p.closeInventory();
                p.performCommand("delhome " + homeName);
            } else {
                // Normal tık ise ışınla
                p.closeInventory();
                Location loc = pl.getHomeManager().getHomes(p).get(homeName);
                if (loc != null) {
                    teleport(p, loc, homeName);
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
            GUIManager.playSound(p, "gui.sounds.click_teleport");
            return;
        }

        Location startLoc = p.getLocation();

        new BukkitRunnable() {
            int time = delay;

            @Override
            public void run() {
                // Oyuncu hareket ederse iptal et
                if (!p.isOnline() || startLoc.distance(p.getLocation()) > 0.5) {
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

                // Mesajı oluştur
                String msg = pl.getLanguageManager().getMessage("messages.teleporting")
                        .replace("{home}", n)
                        .replace("{time}", String.valueOf(time));

                // Action Bar veya Chat'ten gönder
                if (useActionBar) {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
                } else {
                    p.sendMessage(msg);
                }

                time--;
            }
        }.runTaskTimer(pl, 0, 20); // Her saniye (20 tick) çalışır
    }
}
