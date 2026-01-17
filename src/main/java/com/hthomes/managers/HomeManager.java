package com.hthomes.managers;

import com.hthomes.HTHomes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HomeManager {
    private final HTHomes plugin;
    private FileConfiguration homesConfig;
    private File homesFile;

    public HomeManager(HTHomes plugin) {
        this.plugin = plugin;
        setup();
    }

    public void setup() {
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
        if (!homesConfig.contains(path + ".icon")) homesConfig.set(path + ".icon", "LIME_BED");
        save();
    }

    public void setHomeIcon(Player p, String name, Material mat) {
        homesConfig.set(p.getUniqueId() + "." + name + ".icon", mat.name());
        save();
    }

    public Material getHomeIcon(Player p, String name) {
        String matName = homesConfig.getString(p.getUniqueId() + "." + name + ".icon", "LIME_BED");
        return Material.matchMaterial(matName);
    }

    public Map<String, Location> getHomes(Player p) {
        Map<String, Location> homes = new HashMap<>();
        if (homesConfig.contains(p.getUniqueId().toString())) {
            for (String key : homesConfig.getConfigurationSection(p.getUniqueId().toString()).getKeys(false)) {
                String path = p.getUniqueId() + "." + key;
                Location loc = new Location(
                        plugin.getServer().getWorld(homesConfig.getString(path + ".world")),
                        homesConfig.getDouble(path + ".x"),
                        homesConfig.getDouble(path + ".y"),
                        homesConfig.getDouble(path + ".z"),
                        (float) homesConfig.getDouble(path + ".yaw"),
                        (float) homesConfig.getDouble(path + ".pitch")
                );
                homes.put(key, loc);
            }
        }
        return homes;
    }

    public void deleteHome(Player p, String name) {
        homesConfig.set(p.getUniqueId() + "." + name, null);
        save();
    }

    public void save() { try { homesConfig.save(homesFile); } catch (IOException e) { e.printStackTrace(); } }
}
