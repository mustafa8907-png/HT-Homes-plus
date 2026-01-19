package com.hthomes.managers;

import com.hthomes.HTHomes;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;

public class LimitManager {
    private final HTHomes plugin;

    public LimitManager(HTHomes plugin) { this.plugin = plugin; }

    public int getLimit(Player player) {
        if (player.hasPermission("hthomes.limit.admin")) return 100;
        
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("home-limits");
        if (section == null) return 3;

        int max = 0;
        for (String group : section.getKeys(false)) {
            if (player.hasPermission("hthomes.limit." + group)) {
                int limit = section.getInt(group);
                if (limit > max) max = limit;
            }
        }
        return max == 0 ? section.getInt("default", 3) : max;
    }
}
