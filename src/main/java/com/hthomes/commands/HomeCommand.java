package com.hthomes.commands;

import com.hthomes.HTHomes;
import com.hthomes.managers.GUIManager;
import com.hthomes.utils.MessageUtils;
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
            p.sendMessage(MessageUtils.color("&a" + name + " kaydedildi!"));
            return true;
        }

        if (label.equalsIgnoreCase("delhome") && args.length > 0) {
            plugin.getHomeManager().deleteHome(p.getUniqueId(), args[0]);
            p.sendMessage(MessageUtils.color("&c" + args[0] + " silindi."));
            return true;
        }

        if (label.equalsIgnoreCase("home") && args.length > 0) {
            if (plugin.getHomeManager().exists(p.getUniqueId(), args[0])) {
                p.teleport(plugin.getHomeManager().getHome(p.getUniqueId(), args[0]));
                p.sendMessage(MessageUtils.color("&aIşınlandın!"));
            }
            return true;
        }

        GUIManager.openHomeList(p);
        return true;
 
    }
    }
