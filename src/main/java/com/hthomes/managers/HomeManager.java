package com.hthomes.managers;

import com.hthomes.HTHomes;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

public class HomeManager {
    private final HTHomes plugin;
    private FileConfiguration homesConfig;
    private File homesFile;

    public HomeManager(HTHomes plugin) {
        this.plugin = plugin;
        setupHomes();
    }

    private void setupHomes() {
        homesFile = new File(plugin.getDataFolder(), "homes.yml");
        if (!homesFile.exists()) {
            try { homesFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        homesConfig = YamlConfiguration.loadConfiguration(homesFile);
    }

    public void setHome(Player player, String homeName) {
        Location loc = player.getLocation();
        String path = player.getUniqueId() + "." + homeName;
        homesConfig.set(path + ".world", loc.getWorld().getName());
        homesConfig.set(path + ".x", loc.getX());
        homesConfig.set(path + ".y", loc.getY());
        homesConfig.set(path + ".z", loc.getZ());
        homesConfig.set(path + ".yaw", loc.getYaw());
        homesConfig.set(path + ".pitch", loc.getPitch());
        homesConfig.set(path + ".icon", Material.LIME_BED.name()); // Varsayılan ikon
        saveHomes();
    }

    public void deleteHome(Player player, String homeName) {
        homesConfig.set(player.getUniqueId() + "." + homeName, null);
        saveHomes();
    }

    public void teleportToHome(Player player, String homeName) {
        String path = player.getUniqueId() + "." + homeName;
        if (!homesConfig.contains(path)) return;

        Location loc = new Location(
                plugin.getServer().getWorld(homesConfig.getString(path + ".world")),
                homesConfig.getDouble(path + ".x"),
                homesConfig.getDouble(path + ".y"),
                homesConfig.getDouble(path + ".z"),
                (float) homesConfig.getDouble(path + ".yaw"),
                (float) homesConfig.getDouble(path + ".pitch")
        );

        player.closeInventory();
        player.sendMessage(plugin.getLanguageManager().getMessage("messages.teleport_starting"));

        new BukkitRunnable() {
            int timeLeft = 3;
            @Override
            public void run() {
                if (!player.isOnline()) { this.cancel(); return; }
                if (timeLeft <= 0) {
                    player.teleport(loc);
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a§lHT-HOMES+"));
                    this.cancel();
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§eTeleporting in: §6" + timeLeft));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                    timeLeft--;
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void setHomeIcon(Player player, String homeName, Material material) {
        homesConfig.set(player.getUniqueId() + "." + homeName + ".icon", material.name());
        saveHomes();
    }

    public Material getHomeIcon(Player player, String homeName) {
        String name = homesConfig.getString(player.getUniqueId() + "." + homeName + ".icon");
        return name != null ? Material.valueOf(name) : Material.LIME_BED;
    }

    public FileConfiguration getHomesConfig() { return homesConfig; }
    public void saveHomes() { try { homesConfig.save(homesFile); } catch (IOException e) { e.printStackTrace(); } }
    }
