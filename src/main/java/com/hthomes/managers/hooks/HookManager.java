package com.hthomes.hooks;

import com.hthomes.HTHomes;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HookManager {
    private final HTHomes plugin;
    private boolean gp = false;

    public HookManager(HTHomes plugin) {
        this.plugin = plugin;
        if (Bukkit.getPluginManager().isPluginEnabled("GriefPrevention")) gp = true;
    }

    public boolean canBuild(Player p, Location loc) {
        if (!gp || !plugin.getConfig().getBoolean("hooks.grief-prevention")) return true;
        try {
            var claim = GriefPrevention.instance.dataStore.getClaimAt(loc, false, null);
            return claim == null || claim.allowAccess(p) == null;
        } catch (NoClassDefFoundError e) { return true; }
    }
}
