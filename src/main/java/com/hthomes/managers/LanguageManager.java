package com.hthomes.managers;

import com.hthomes.HTHomes;
import com.hthomes.utils.MessageUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {
    private final HTHomes plugin;
    private final Map<String, FileConfiguration> langCache = new HashMap<>();

    public LanguageManager(HTHomes plugin) {
        this.plugin = plugin;
        loadLanguages();
    }

    public void loadLanguages() {
        langCache.clear();
        File folder = new File(plugin.getDataFolder(), "languages");
        if (!folder.exists()) folder.mkdirs();
        
        File trFile = new File(folder, "tr.yml");
        if (!trFile.exists()) plugin.saveResource("languages/tr.yml", false);

        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.getName().endsWith(".yml")) {
                    langCache.put(f.getName().replace(".yml", "").toLowerCase(), YamlConfiguration.loadConfiguration(f));
                }
            }
        }
    }

    public String getRaw(Player p, String path) {
        String locale = (p != null && p.getLocale() != null) ? p.getLocale().split("_")[0].toLowerCase() : "tr";
        FileConfiguration config = langCache.getOrDefault(locale, langCache.get("tr"));
        
        if (config == null || !config.contains(path)) return path; // Key bulunamazsa path döner
        return config.getString(path);
    }

    public void sendMessage(Player p, String path, Map<String, String> placeholders) {
        String prefix = getRaw(p, "messages.prefix");
        String msg = getRaw(p, path);
        String full = prefix + msg;
        if (placeholders != null) {
            for (Map.Entry<String, String> e : placeholders.entrySet()) full = full.replace(e.getKey(), e.getValue());
        }
        plugin.getAdventure().player(p).sendMessage(MessageUtils.parse(p, full));
    }
}
