package com.hthomes.managers;

import com.hthomes.HTHomes;
import org.bukkit.Location;
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
        if (p.hasPermission("homegui.admin")) return 100;
        int max = plugin.getConfig().getInt("groups.default", 1);
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
        if (cache.containsKey(p.getUniqueId())) cache.get(p.getUniqueId()).remove(n);
        saveHomes();
    }

    public Map<String, Location> getHomes(Player p) { return cache.getOrDefault(p.getUniqueId(), new HashMap<>()); }

    private void loadHomes() {
        file = new File(plugin.getDataFolder(), "data/homes.yml");
        if (!file.exists()) { file.getParentFile().mkdirs(); try { file.createNewFile(); } catch (IOException ignored) {} }
        data = YamlConfiguration.loadConfiguration(file);
        if (data.contains("homes")) {
            for (String u : data.getConfigurationSection("homes").getKeys(false)) {
                Map<String, Location> h = new HashMap<>();
                ConfigurationSection s = data.getConfigurationSection("homes." + u);
                for (String k : s.getKeys(false)) h.put(k, s.getLocation(k));
                cache.put(UUID.fromString(u), h);
            }
        }
    }

    public void saveHomes() {
        data.set("homes", null);
        cache.forEach((u, m) -> m.forEach((n, l) -> data.set("homes." + u + "." + n, l)));
        try { data.save(file); } catch (IOException ignored) {}
    }
              }
