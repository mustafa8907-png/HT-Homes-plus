package com.hthomes.managers;

import com.hthomes.HTHomes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomeManager {
    private final HTHomes plugin;
    private FileConfiguration config;
    private final File file;

    public HomeManager(HTHomes plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "homes.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * GUIManager'ın hata vermesine sebep olan kritik metod.
     * Oyuncunun sahip olduğu evlerin isimlerini liste olarak döndürür.
     */
    public List<String> getPlayerHomes(UUID uuid) {
        if (config.getConfigurationSection(uuid.toString()) == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(config.getConfigurationSection(uuid.toString()).getKeys(false));
    }

    public void setHome(UUID uuid, String homeName, Location loc) {
        String path = uuid.toString() + "." + homeName;
        config.set(path + ".world", loc.getWorld().getName());
        config.set(path + ".x", loc.getX());
        config.set(path + ".y", loc.getY());
        config.set(path + ".z", loc.getZ());
        config.set(path + ".yaw", (double) loc.getYaw());
        config.set(path + ".pitch", (double) loc.getPitch());
        save();
    }

    public void deleteHome(UUID uuid, String homeName) {
        config.set(uuid.toString() + "." + homeName, null);
        save();
    }

    public Location getHome(UUID uuid, String homeName) {
        String path = uuid.toString() + "." + homeName;
        if (config.get(path) == null) return null;

        return new Location(
                Bukkit.getWorld(config.getString(path + ".world")),
                config.getDouble(path + ".x"),
                config.getDouble(path + ".y"),
                config.getDouble(path + ".z"),
                (float) config.getDouble(path + ".yaw"),
                (float) config.getDouble(path + ".pitch")
        );
    }

    public boolean exists(UUID uuid, String homeName) {
        return config.contains(uuid.toString() + "." + homeName);
    }

    public int getHomeCount(UUID uuid) {
        return getPlayerHomes(uuid).size();
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(file);
    }
}
