package com.hthomes.commands;

import com.hthomes.HTHomes;
import com.hthomes.managers.GUIManager;
import com.hthomes.managers.TeleportManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor {
    private final HTHomes plugin;
    public HomeCommand(HTHomes plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;

        if (label.equalsIgnoreCase("sethome")) {
            String name = args.length > 0 ? args[0] : "Ev-1";
            plugin.getHomeManager().setHome(p.getUniqueId(), name, p.getLocation());
            plugin.getLangManager().sendMessage(p, "messages.home-set", java.util.Map.of("{home}", name));
            return true;
        }

        if (label.equalsIgnoreCase("delhome") && args.length > 0) {
            plugin.getHomeManager().deleteHome(p.getUniqueId(), args[0]);
            plugin.getLangManager().sendMessage(p, "messages.home-deleted", java.util.Map.of("{home}", args[0]));
            return true;
        }

        if (label.equalsIgnoreCase("home") && args.length > 0) {
            if (plugin.getHomeManager().exists(p.getUniqueId(), args[0])) {
                TeleportManager.teleportWithCountdown(p, plugin.getHomeManager().getHome(p.getUniqueId(), args[0]));
            } else {
                plugin.getLangManager().sendMessage(p, "messages.home-not-found", null);
            }
            return true;
        }

        GUIManager.openHomeList(p);
        return true;
 
    }
}
