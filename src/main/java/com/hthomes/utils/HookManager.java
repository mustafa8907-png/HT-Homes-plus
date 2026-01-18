package com.hthomes.managers;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HookManager {
    private final boolean hasWG = Bukkit.getPluginManager().isPluginEnabled("WorldGuard");
    private final boolean hasGP = Bukkit.getPluginManager().isPluginEnabled("GriefPrevention");
    private final boolean hasPAPI = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

    public boolean canBuild(Player p, Location loc) {
        if (hasWG) {
            com.sk89q.worldguard.LocalPlayer lp = com.sk89q.worldguard.bukkit.WorldGuardPlugin.inst().wrapPlayer(p);
            if (!WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().testState(BukkitAdapter.adapt(loc), lp, Flags.BUILD)) return false;
        }
        if (hasGP) {
            me.ryanhamshire.GriefPrevention.Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, true, null);
            if (claim != null && claim.allowBuild(p, null) != null) return false;
        }
        return true;
    }

    public boolean hasPAPI() { return hasPAPI; }
}
