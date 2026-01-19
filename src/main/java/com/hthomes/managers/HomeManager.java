package com.hthomes.managers;

import com.hthomes.HTHomes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
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
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public boolean exists(UUID uuid, String name) {
        return config.contains(uuid.toString() + "." + name);
    }

    public void setHome(UUID uuid, String name, Location loc) {
        String path = uuid.toString() + "." + name;
        config.set(path + ".world", loc.getWorld().getName());
        config.set(path + ".x", loc.getX());
        config.set(path + ".y", loc.getY());
        config.set(path + ".z", loc.getZ());
        config.set(path + ".yaw", loc.getYaw());
        config.set(path + ".pitch", loc.getPitch());
        save();
    }

    public Location getHome(UUID uuid, String name) {
        String path = uuid.toString() + "." + name;
        if (!config.contains(path)) return null;
        return new Location(Bukkit.getWorld(config.getString(path + ".world")), 
            config.getDouble(path + ".x"), config.getDouble(path + ".y"), config.getDouble(path + ".z"), 
            (float) config.getDouble(path + ".yaw"), (float) config.getDouble(path + ".pitch"));
    }

    public void deleteHome(UUID uuid, String name) {
        config.set(uuid.toString() + "." + name, null);
        save();
    }

    public List<String> getHomesList(UUID uuid) {
        if (config.getConfigurationSection(uuid.toString()) == null) return new ArrayList<>();
        return new ArrayList<>(config.getConfigurationSection(uuid.toString()).getKeys(false));
    }

    public void save() { try { config.save(file); } catch (IOException ignored
                                                          ) {} }
}
