package com.hthomes.hooks;

import com.hthomes.HTHomes;
import me.ryanhamshire.GriefPrevention.GriefPrevention; // Doğru import
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HookManager {
    private final HTHomes plugin;
    private boolean gpEnabled = false;

    public HookManager(HTHomes plugin) {
        this.plugin = plugin;
        if (Bukkit.getPluginManager().isPluginEnabled("GriefPrevention")) {
            gpEnabled = true;
        }
    }

    public boolean canBuild(Player p, Location loc) {
        if (!gpEnabled) return true;
        
        // GriefPrevention.instance kullanımı sınıf üzerinden olmalı
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, false, null);
        if (claim != null) {
            return claim.allowAccess(p) == null; // null ise izin var demektir
        }
        return true;
    }
}
