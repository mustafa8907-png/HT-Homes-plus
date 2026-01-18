package com.hthomes.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HookManager {
    // Hooks check logic here (WorldGuard/GriefPrevention)
    // Keeping it simple as per request to maintain structure
    public boolean canBuild(Player p, Location loc) {
        return true; // Simplified for this snippet, add WG/GP logic if needed
    }
}
