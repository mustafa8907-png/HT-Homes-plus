package com.hthomes.managers;

import com.hthomes.HTHomes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class HomeManager {
    private final HTHomes plugin;
    private File file;
    private FileConfiguration config;

    public HomeManager(HTHomes plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "homes.yml");
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    // EKSİK OLAN METOTLAR BURADA:
    public void setIcon(UUID uuid, String name, Material mat) {
        config.set(uuid.toString() + "." + name + ".icon", mat.name());
        save();
    }

    public Material getIcon(UUID uuid, String name) {
        String matName = config.getString(uuid.toString() + "." + name + ".icon", "LIME_BED");
        Material mat = Material.getMaterial(matName);
        return (mat != null) ? mat : Material.LIME_BED;
    }

    public void setHome(UUID uuid, String name, Location loc) {
        String path = uuid.toString() + "." + name;
        config.set(path + ".world", loc.getWorld().getName());
        config.set(path + ".x", loc.getX());
        config.set(path + ".y", loc.getY());
        config.set(path + ".z", loc.getZ());
        config.set(path + ".yaw", loc.getYaw());
        config.set(path + ".pitch", loc.getPitch());
        // Varsayılan ikon ata
        if (!config.contains(path + ".icon")) config.set(path + ".icon", "LIME_BED");
        save();
    }

    public Location getHome(UUID uuid, String name) {
        String path = uuid.toString() + "." + name;
        if (!config.contains(path)) return null;
        return new Location(
            Bukkit.getWorld(config.getString(path + ".world")),
            config.getDouble(path + ".x"),
            config.getDouble(path + ".y"),
            config.getDouble(path + ".z"),
            (float) config.getDouble(path + ".yaw"),
            (float) config.getDouble(path + ".pitch")
        );
    }

    public boolean exists(UUID uuid, String name) { return config.contains(uuid.toString() + "." + name); }
    public void deleteHome(UUID uuid, String name) { config.set(uuid.toString() + "." + name, null); save(); }
    private void save() { try { config.save(file); } catch (IOException e) { e.printStackTrace(); } }
}
