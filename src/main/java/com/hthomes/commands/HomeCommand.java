package com.hthomes.commands;

import com.hthomes.HTHomes;
import com.hthomes.managers.GUIManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Map;

public class HomeCommand implements CommandExecutor {
    private final HTHomes plugin;

    public HomeCommand(HTHomes plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Bu komut sadece oyuncular içindir!");
            return true;
        }
        
        Player p = (Player) sender;

        // /home - /ev
        if (cmd.getName().equalsIgnoreCase("home")) {
            if (!p.hasPermission("hthomes.user.home")) {
                noPerm(p);
                return true;
            }
            GUIManager.openHomeList(p, 1);
            return true;
        }

        // /sethome - /evayarla
        if (cmd.getName().equalsIgnoreCase("sethome")) {
            if (!p.hasPermission("hthomes.user.sethome")) {
                noPerm(p);
                return true;
            }
            if (args.length == 0) {
                p.sendMessage("§cKullanım: /" + label + " <isim>");
                return true;
            }
            
            String name = args[0];
            if (!plugin.getHookManager().canBuild(p, p.getLocation())) {
                plugin.getLangManager().sendMessage(p, "messages.unsafe-area", null);
                return true;
            }

            plugin.getHomeManager().setHome(p, name, p.getLocation());
            plugin.getLangManager().sendMessage(p, "messages.home-set", Map.of("{home}", name));
            return true;
        }

        // /delhome - /evsil
        if (cmd.getName().equalsIgnoreCase("delhome")) {
            if (!p.hasPermission("hthomes.user.delhome")) {
                noPerm(p);
                return true;
            }
            if (args.length == 0) {
                p.sendMessage("§cKullanım: /" + label + " <isim>");
                return true;
            }
            String name = args[0];
            if (plugin.getHomeManager().getHomes(p).containsKey(name)) {
                plugin.getHomeManager().deleteHome(p, name);
                plugin.getLangManager().sendMessage(p, "messages.home-deleted", Map.of("{home}", name));
            } else {
                p.sendMessage("§cEv bulunamadı.");
            }
            return true;
        }

        return true;
    }

    private void noPerm(Player p) {
        // Dil dosyasından "no-permission" mesajını çeker
        plugin.getLangManager().sendMessage(p, "messages.no-permission", null);
    }
    }
