package com.hthomes.commands;

import com.hthomes.HTHomes;
import com.hthomes.managers.GUIManager;
import com.hthomes.managers.HomeManager;
import com.hthomes.managers.LanguageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor {
    private final HTHomes plugin;

    public HomeCommand(HTHomes plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Bu komutu sadece oyuncular kullanabilir.");
            return true;
        }

        Player p = (Player) sender;
        HomeManager hm = plugin.getHomeManager();
        LanguageManager lm = plugin.getLanguageManager();
        String cmdName = label.toLowerCase();

        // /sethome komutu
        if (cmdName.equals("sethome")) {
            String homeName = (args.length > 0) ? args[0] : "home" + (hm.getHomes(p).size() + 1);
            handleSetHome(p, homeName);
            return true;
        }

        // /delhome komutu
        if (cmdName.equals("delhome")) {
            if (args.length > 0) {
                handleDelHome(p, args[0]);
            } else {
                p.sendMessage(lm.getMessage("messages.usage"));
            }
            return true;
        }

        // /home komutu (Args yoksa menü açar)
        if (args.length == 0) {
            GUIManager.open(p, 1);
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("set")) {
            String name = (args.length > 1) ? args[1] : "home" + (hm.getHomes(p).size() + 1);
            handleSetHome(p, name);
        } else if (sub.equals("delete")) {
            if (args.length > 1) {
                handleDelHome(p, args[1]);
            } else {
                p.sendMessage(lm.getMessage("messages.usage"));
            }
        } else if (sub.equals("reload")) {
            if (p.hasPermission("homegui.admin")) {
                plugin.reloadConfig();
                plugin.getLanguageManager().loadLanguage();
                p.sendMessage(lm.getMessage("messages.reload-success"));
            } else {
                p.sendMessage(lm.getMessage("messages.no-permission"));
            }
        } else {
            // Eğer oyuncu direkt /home <isim> yazdıysa ışınla (Opsiyonel)
             p.sendMessage(lm.getMessage("messages.usage"));
        }
        return true;
    }

    private void handleSetHome(Player p, String name) {
        HomeManager hm = plugin.getHomeManager();
        LanguageManager lm = plugin.getLanguageManager();
        int limit = hm.getPlayerLimit(p);

        if (hm.getHomes(p).size() >= limit && !hm.getHomes(p).containsKey(name)) {
            p.sendMessage(lm.getMessage("messages.max-homes").replace("{limit}", String.valueOf(limit)));
            GUIManager.playSound(p, "gui.sounds.error");
            return;
        }

        hm.setHome(p, name, p.getLocation());
        p.sendMessage(lm.getMessage("messages.home-set").replace("{home}", name));
        GUIManager.playSound(p, "gui.sounds.click_set");
    }

    private void handleDelHome(Player p, String name) {
        HomeManager hm = plugin.getHomeManager();
        LanguageManager lm = plugin.getLanguageManager();

        if (hm.getHomes(p).get(name) == null) {
            p.sendMessage(lm.getMessage("messages.home-not-found").replace("{home}", name));
            return;
        }

        hm.deleteHome(p, name);
        p.sendMessage(lm.getMessage("messages.home-deleted").replace("{home}", name));
        GUIManager.playSound(p, "gui.sounds.click_delete");
    }
        }
