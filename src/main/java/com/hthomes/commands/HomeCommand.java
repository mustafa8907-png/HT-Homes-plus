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
    public HomeCommand(HTHomes plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!(s instanceof Player)) return true;
        Player p = (Player) s;
        HomeManager hm = plugin.getHomeManager();
        LanguageManager lm = plugin.getLanguageManager();
        String cmd = l.toLowerCase();

        if (cmd.equals("sethome")) {
            String name = (a.length > 0) ? a[0] : "home" + (hm.getHomes(p).size() + 1);
            setHome(p, name);
            return true;
        }
        if (cmd.equals("delhome")) {
            if (a.length > 0) delHome(p, a[0]);
            else p.sendMessage(lm.getMessage("messages.usage"));
            return true;
        }

        if (a.length == 0) {
            GUIManager.open(p, 1);
            return true;
        }

        String sub = a[0].toLowerCase();
        if (sub.equals("set")) {
            String name = (a.length > 1) ? a[1] : "home" + (hm.getHomes(p).size() + 1);
            setHome(p, name);
        } else if (sub.equals("delete")) {
            if (a.length > 1) delHome(p, a[1]);
        } else if (sub.equals("reload") && p.hasPermission("homegui.admin")) {
            plugin.reloadConfig();
            lm.loadLanguage();
            p.sendMessage(lm.getMessage("messages.reload-success"));
        } else {
            GUIManager.open(p, 1);
        }
        return true;
    }

    private void setHome(Player p, String n) {
        HomeManager hm = plugin.getHomeManager();
        int limit = hm.getPlayerLimit(p);
        if (hm.getHomes(p).size() >= limit && !hm.getHomes(p).containsKey(n)) {
            p.sendMessage(plugin.getLanguageManager().getMessage("messages.max-homes").replace("{limit}", String.valueOf(limit)));
            return;
        }
        hm.setHome(p, n, p.getLocation());
        p.sendMessage(plugin.getLanguageManager().getMessage("messages.home-set").replace("{home}", n));
        GUIManager.playSound(p, "gui.sounds.click_set");
        // Evi set ettikten sonra menüyü tekrar aç ki oyuncu görsün
        GUIManager.open(p, 1);
    }

    private void delHome(Player p, String n) {
        HomeManager hm = plugin.getHomeManager();
        if (hm.getHomes(p).get(n) == null) {
            p.sendMessage(plugin.getLanguageManager().getMessage("messages.home-not-found").replace("{home}", n));
            return;
        }
        hm.deleteHome(p, n);
        p.sendMessage(plugin.getLanguageManager().getMessage("messages.home-deleted").replace("{home}", n));
        GUIManager.playSound(p, "gui.sounds.click_delete");
        GUIManager.open(p, 1);
    }
            }
