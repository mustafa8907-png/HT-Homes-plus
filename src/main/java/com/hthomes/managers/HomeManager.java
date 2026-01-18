package com.hthomes.managers;

import com.hthomes.HTHomes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeManager {
    private final HTHomes plugin;
    private FileConfiguration homesConfig;
    private File homesFile;

    public HomeManager(HTHomes plugin) {
        this.plugin = plugin;
        setup();
    }

    private void setup() {
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        homesFile = new File(plugin.getDataFolder(), "homes.yml");
        if (!homesFile.exists()) {
            try { homesFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        homesConfig = YamlConfiguration.loadConfiguration(homesFile);
    }

    public void setHome(Player p, String name, Location loc) {
        String path = p.getUniqueId() + "." + name;
        homesConfig.set(path + ".world", loc.getWorld().getName());
        homesConfig.set(path + ".x", loc.getX());
        homesConfig.set(path + ".y", loc.getY());
        homesConfig.set(path + ".z", loc.getZ());
        homesConfig.set(path + ".yaw", loc.getYaw());
        homesConfig.set(path + ".pitch", loc.getPitch());
        homesConfig.set(path + ".icon", "RED_BED");
        save();
    }

    public Map<String, Location> getHomes(Player p) {
        Map<String, Location> homes = new HashMap<>();
        String uuid = p.getUniqueId().toString();
        if (homesConfig.contains(uuid)) {
            for (String key : homesConfig.getConfigurationSection(uuid).getKeys(false)) {
                String path = uuid + "." + key;
                homes.put(key, new Location(
                        Bukkit.getWorld(homesConfig.getString(path + ".world")),
                        homesConfig.getDouble(path + ".x"),
                        homesConfig.getDouble(path + ".y"),
                        homesConfig.getDouble(path + ".z"),
                        (float) homesConfig.getDouble(path + ".yaw"),
                        (float) homesConfig.getDouble(path + ".pitch")
                ));
            }
        }
        return homes;
    }

    public Material getHomeIcon(Player p, String name) {
        String iconName = homesConfig.getString(p.getUniqueId() + "." + name + ".icon", "RED_BED");
        Material mat = Material.matchMaterial(iconName);
        return mat != null ? mat : Material.RED_BED;
    }

    public void deleteHome(Player p, String name) {
        homesConfig.set(p.getUniqueId() + "." + name, null);
        save();
    }

    private void save() {
        try { homesConfig.save(homesFile); } catch (IOException e) { e.printStackTrace(); }
    }
            }
