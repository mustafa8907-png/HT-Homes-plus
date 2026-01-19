package com.hthomes.managers;

import com.hthomes.HTHomes;
import com.hthomes.utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportManager {
    
    public static void teleportWithCountdown(Player player, Location loc) {
        HTHomes plugin = HTHomes.getInstance();
        // Configden süreyi al, yoksa 3 saniye yap
        int delay = plugin.getConfig().getInt("teleport-delay", 3);
        
        new BukkitRunnable() {
            int rem = delay;
            Location start = player.getLocation();
            
            @Override
            public void run() {
                // Oyuncu hareket ettiyse iptal et
                if (player.getLocation().distance(start) > 0.5) {
                    plugin.getLangManager().sendMessage(player, "messages.teleport-cancelled", null);
                    // İptal sesi
                    String cancelSound = plugin.getConfig().getString("sounds.cancel", "BLOCK_NOTE_BLOCK_BASS");
                    try {
                        player.playSound(player.getLocation(), Sound.valueOf(cancelSound), 1f, 1f);
                    } catch (Exception e) {}
                    this.cancel(); 
                    return;
                }
                
                // Süre bitti, ışınla
                if (rem <= 0) {
                    player.teleport(loc);
                    player.sendActionBar(MessageUtils.parse(player, plugin.getLangManager().getRaw(player, "messages.teleport-success")));
                    
                    // Başarı sesi
                    String successSound = plugin.getConfig().getString("sounds.teleport", "ENTITY_ENDERMAN_TELEPORT");
                    try {
                        player.playSound(player.getLocation(), Sound.valueOf(successSound), 1f, 1f);
                    } catch (Exception e) {}
                    
                    this.cancel(); 
                    return;
                }
                
                // Geri sayım action bar
                String msg = plugin.getLangManager().getRaw(player, "messages.teleporting-actionbar").replace("{time}", String.valueOf(rem));
                player.sendActionBar(MessageUtils.parse(player, msg));
                
                // Tık sesi
                String clickSound = plugin.getConfig().getString("sounds.click", "UI_BUTTON_CLICK");
                try {
                    player.playSound(player.getLocation(), Sound.valueOf(clickSound), 1f, 1f);
                } catch (Exception e) {}
                
                rem--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

}
