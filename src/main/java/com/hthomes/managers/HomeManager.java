package com.hthomes.managers;

import com.hthomes.HTHomes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
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
    private File file;
    private FileConfiguration data;
    private final Map<UUID, Map<String, Location>> cache = new HashMap<>();

    public HomeManager(HTHomes plugin) {
        this.plugin = plugin;
        loadHomes();
    }

    public int getPlayerLimit(Player p) {
        if (p.hasPermission("homegui.admin")) return 100;
        int max = plugin.getConfig().getInt("groups.default", 3);
        ConfigurationSection groups = plugin.getConfig().getConfigurationSection("groups");
        if (groups != null) {
            for (String key : groups.getKeys(false)) {
                if (p.hasPermission("homegui.group." + key)) {
                    max = Math.max(max, groups.getInt(key));
                }
            }
        }
        return max;
    }

    public void setHome(Player p, String n, Location l) {
        cache.computeIfAbsent(p.getUniqueId(), k -> new HashMap<>()).put(n, l);
        saveHomes();
    }

    public void deleteHome(Player p, String n) {
        if (cache.containsKey(p.getUniqueId())) {
            cache.get(p.getUniqueId()).remove(n);
            saveHomes();
        }
    }

    public Map<String, Location> getHomes(Player p) {
        return cache.getOrDefault(p.getUniqueId(), new HashMap<>());
    }

    private void loadHomes() {
        file = new File(plugin.getDataFolder(), "data/homes.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try { file.createNewFile(); } catch (IOException ignored) {}
        }
        data = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection homesSec = data.getConfigurationSection("homes");
        if (homesSec != null) {
            for (String uStr : homesSec.getKeys(false)) {
                UUID uuid = UUID.fromString(uStr);
                Map<String, Location> userHomes = new HashMap<>();
                ConfigurationSection userSec = homesSec.getConfigurationSection(uStr);
                
                for (String hName : userSec.getKeys(false)) {
                    String wName = userSec.getString(hName + ".world");
                    if (wName != null) {
                        World w = Bukkit.getWorld(wName);
                        if (w != null) {
                            double x = userSec.getDouble(hName + ".x");
                            double y = userSec.getDouble(hName + ".y");
                            double z = userSec.getDouble(hName + ".z");
                            float yaw = (float) userSec.getDouble(hName + ".yaw");
                            float pitch = (float) userSec.getDouble(hName + ".pitch");
                            userHomes.put(hName, new Location(w, x, y, z, yaw, pitch));
                        }
                    }
                }
                cache.put(uuid, userHomes);
            }
        }
    }

    public void saveHomes() {
        data.set("homes", null);
        for (Map.Entry<UUID, Map<String, Location>> entry : cache.entrySet()) {
            for (Map.Entry<String, Location> home : entry.getValue().entrySet()) {
                String path = "homes." + entry.getKey().toString() + "." + home.getKey();
                Location loc = home.getValue();
                data.set(path + ".world", loc.getWorld().getName());
                data.set(path + ".x", loc.getX());
                data.set(path + ".y", loc.getY());
                data.set(path + ".z", loc.getZ());
                data.set(path + ".yaw", loc.getYaw());
                data.set(path + ".pitch", loc.getPitch());
            }
        }
        try { data.save(file); } catch (IOException ignored) {}
    }
    }
