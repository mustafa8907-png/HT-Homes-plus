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
import java.util.*;

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
        if (p.isOp()) return 100;
        int max = plugin.getConfig().getInt("groups.default", 3);
        ConfigurationSection groups = plugin.getConfig().getConfigurationSection("groups");
        if (groups != null) {
            for (String key : groups.getKeys(false)) {
                if (p.hasPermission("hthomes.limit." + key)) {
                    int groupLimit = groups.getInt(key);
                    if (groupLimit > max) max = groupLimit;
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
        file = new File(plugin.getDataFolder(), "homes.yml");
        if (!file.exists()) {
            try { file.getParentFile().mkdirs(); file.createNewFile(); } catch (IOException ignored) {}
        }
        data = YamlConfiguration.loadConfiguration(file);
        if (data.contains("homes")) {
            for (String uuidStr : data.getConfigurationSection("homes").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidStr);
                Map<String, Location> userHomes = new HashMap<>();
                for (String hName : data.getConfigurationSection("homes." + uuidStr).getKeys(false)) {
                    String path = "homes." + uuidStr + "." + hName;
                    World w = Bukkit.getWorld(data.getString(path + ".world"));
                    if (w != null) {
                        userHomes.put(hName, new Location(w, data.getDouble(path + ".x"), data.getDouble(path + ".y"), data.getDouble(path + ".z"), (float)data.getDouble(path + ".yaw"), (float)data.getDouble(path + ".pitch")));
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
