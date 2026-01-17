// EKLENECEK IMPORTLAR (Dosyanın en tepesine):
import org.bukkit.scheduler.BukkitRunnable;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;

// 1. BU METODU ESKİSİYLE DEĞİŞTİR (Action Bar ve Süre Sorunu Çözümü):
public void teleportToHome(Player player, String homeName) {
    if (!homesConfig.contains(player.getUniqueId() + "." + homeName)) {
        player.sendMessage("§cEv bulunamadı!");
        return;
    }

    String path = player.getUniqueId() + "." + homeName;
    String worldName = homesConfig.getString(path + ".world");
    
    if (plugin.getServer().getWorld(worldName) == null) {
        player.sendMessage("§cBu evin dünyası yüklenemedi!");
        return;
    }

    double x = homesConfig.getDouble(path + ".x");
    double y = homesConfig.getDouble(path + ".y");
    double z = homesConfig.getDouble(path + ".z");
    float yaw = (float) homesConfig.getDouble(path + ".yaw");
    float pitch = (float) homesConfig.getDouble(path + ".pitch");

    org.bukkit.Location loc = new org.bukkit.Location(plugin.getServer().getWorld(worldName), x, y, z, yaw, pitch);

    player.closeInventory();
    player.sendMessage("§aIşınlanma başlatıldı, hareket etme!");

    // Geri sayım ve Action Bar sistemi
    new BukkitRunnable() {
        int timeLeft = 3;

        @Override
        public void run() {
            if (!player.isOnline()) { this.cancel(); return; }

            if (timeLeft <= 0) {
                player.teleport(loc);
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                
                // Action Bar Mesajı (Başarılı)
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
                        new TextComponent("§a§lEVİNE HOŞ GELDİN!"));
                
                this.cancel();
            } else {
                // Action Bar Mesajı (Sayım)
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
                        new TextComponent("§eIşınlanmaya §6" + timeLeft + " §esaniye..."));
                
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                timeLeft--;
            }
        }
    }.runTaskTimer(plugin, 0L, 20L);
}

// 2. DOSYANIN EN ALTINA BU YENİ METOTLARI EKLE (İkon Sistemi):
public void setHomeIcon(Player player, String homeName, org.bukkit.Material material) {
    homesConfig.set(player.getUniqueId() + "." + homeName + ".icon", material.name());
    saveHomes();
}

public org.bukkit.Material getHomeIcon(Player player, String homeName) {
    String iconName = homesConfig.getString(player.getUniqueId() + "." + homeName + ".icon");
    if (iconName == null) return org.bukkit.Material.LIME_BED;
    try {
        return org.bukkit.Material.valueOf(iconName);
    } catch (Exception e) {
        return org.bukkit.Material.LIME_BED;
    }
                           }
