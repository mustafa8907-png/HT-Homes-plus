package com.hthomes.commands;

import com.hthomes.HTHomes;
import com.hthomes.managers.GUIManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor {
    private final HTHomes plugin;
    public HomeCommand(HTHomes plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) return true;
        Player p = (Player) s;

        if (l.equalsIgnoreCase("sethome")) {
            int limit = plugin.getHomeManager().getPlayerLimit(p);
            if (plugin.getHomeManager().getHomes(p).size() >= limit) {
                p.sendMessage("§cLimit reached!");
                return true;
            }
            String name = (args.length > 0) ? args[0] : "Home-" + (plugin.getHomeManager().getHomes(p).size() + 1);
            plugin.getHomeManager().setHome(p, name, p.getLocation());
            p.sendMessage("§aHome set: " + name);
            return true;
        }
        
        GUIManager.openHomeList(p, 1);
        return true;
    }
            }
