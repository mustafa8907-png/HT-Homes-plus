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
        int delay = plugin.getConfig().getInt("teleport-delay", 3);
        
        new BukkitRunnable() {
            int rem = delay;
            Location start = player.getLocation();
            
            @Override
            public void run() {
                // Hareket kontrolü
                if (player.getLocation().distance(start) > 0.5) {
                    plugin.getLangManager().sendMessage(player, "messages.teleport-cancelled", null);
                    
                    String cancelSound = plugin.getConfig().getString("sounds.cancel", "BLOCK_NOTE_BLOCK_BASS");
                    try { player.playSound(player.getLocation(), Sound.valueOf(cancelSound), 1f, 1f); } catch (Exception ignored) {}
                    
                    this.cancel(); 
                    return;
                }
                
                // Süre tamamlandı
                if (rem <= 0) {
                    player.teleport(loc);
                    
                    // Action Bar gönderimi (Hata veren yer burasıydı, Adventure API ile düzeltildi)
                    String successMsg = plugin.getLangManager().getRaw(player, "messages.teleport-success");
                    plugin.getAdventure().player(player).sendActionBar(MessageUtils.parse(player, successMsg));
                    
                    String successSound = plugin.getConfig().getString("sounds.teleport", "ENTITY_ENDERMAN_TELEPORT");
                    try { player.playSound(player.getLocation(), Sound.valueOf(successSound), 1f, 1f); } catch (Exception ignored) {}
                    
                    this.cancel(); 
                    return;
                }
                
                // Geri sayım devam ediyor
                String msg = plugin.getLangManager().getRaw(player, "messages.teleporting-actionbar")
                        .replace("{time}", String.valueOf(rem));
                
                // Action Bar gönderimi (Hata veren yer burasıydı, Adventure API ile düzeltildi)
                plugin.getAdventure().player(player).sendActionBar(MessageUtils.parse(player, msg));
                
                String clickSound = plugin.getConfig().getString("sounds.click", "UI_BUTTON_CLICK");
                try { player.playSound(player.getLocation(), Sound.valueOf(clickSound), 1f, 1f); } catch (Exception ignored) {}
                
                rem--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}
