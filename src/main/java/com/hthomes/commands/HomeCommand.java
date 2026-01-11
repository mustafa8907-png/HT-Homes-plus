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
    public boolean onCommand(CommandSender s, Command c, String lb, String[] a) {
        if (!(s instanceof Player)) return true;
        Player p = (Player) s;
        HomeManager hm = plugin.getHomeManager();
        LanguageManager lm = plugin.getLanguageManager();

        if (a.length == 0) { GUIManager.open(p); return true; }

        switch (a[0].toLowerCase()) {
            case "reload":
                if (!p.hasPermission("homegui.admin")) { p.sendMessage(lm.getMessage("messages.no-permission")); return true; }
                plugin.reloadConfig(); plugin.getLanguageManager().loadLanguage();
                p.sendMessage(lm.getMessage("messages.reload-success"));
                break;
            case "set":
                int limit = hm.getPlayerLimit(p);
                if (hm.getHomes(p).size() >= limit) { p.sendMessage(lm.getMessage("messages.max-homes").replace("{limit}", String.valueOf(limit))); GUIManager.playSound(p, "gui.sounds.error"); return true; }
                String name = a.length > 1 ? a[1] : "home" + (hm.getHomes(p).size() + 1);
                if (!name.matches(plugin.getConfig().getString("homes.name-regex"))) { p.sendMessage(lm.getMessage("messages.invalid-name")); return true; }
                hm.setHome(p, name, p.getLocation());
                p.sendMessage(lm.getMessage("messages.home-set").replace("{home}", name));
                GUIManager.playSound(p, "gui.sounds.click_set");
                break;
            case "delete":
                if (a.length < 2) { lm.getStringList("messages.usage").forEach(p::sendMessage); return true; }
                if (hm.getHomes(p).get(a[1]) == null) { p.sendMessage(lm.getMessage("messages.home-not-found").replace("{home}", a[1])); return true; }
                hm.deleteHome(p, a[1]);
                p.sendMessage(lm.getMessage("messages.home-deleted").replace("{home}", a[1]));
                GUIManager.playSound(p, "gui.sounds.click_delete");
                break;
            default:
                lm.getStringList("messages.usage").forEach(p::sendMessage);
        }
        return true;
    }
}
