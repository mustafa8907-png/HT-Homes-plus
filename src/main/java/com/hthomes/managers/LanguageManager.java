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
        File folder = new File(plugin.getDataFolder(), "languages");
        if (!folder.exists()) folder.mkdirs();
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.getName().endsWith(".yml")) {
                    langCache.put(f.getName().replace(".yml", "").toLowerCase(), YamlConfiguration.loadConfiguration(f));
                }
            }
        }
    }

    public FileConfiguration getConfigForPlayer(Player p) {
        // Oyuncunun dilini al, yoksa varsayılanı kullan
        String locale = "tr"; // Varsayılan TR yaptık, istersen p.getLocale() kullanabilirsin
        if (p != null && p.getLocale() != null) {
            locale = p.getLocale().split("_")[0].toLowerCase();
        }
        
        if (langCache.containsKey(locale)) return langCache.get(locale);
        
        // Varsayılan dil configden
        String defaultLang = plugin.getConfig().getString("default-lang", "tr");
        return langCache.getOrDefault(defaultLang, langCache.values().stream().findFirst().orElse(null));
    }

    // İŞTE HATAYI ÇÖZEN KISIM: getRaw ARTIK PLAYER ALIYOR
    public String getRaw(Player p, String path) {
        FileConfiguration config = getConfigForPlayer(p);
        return config != null ? config.getString(path, path) : path;
    }

    public void sendMessage(Player p, String path, Map<String, String> placeholders) {
        String prefix = getRaw(p, "messages.prefix");
        String msg = getRaw(p, path);
        
        String fullMsg = prefix + msg;
        
        if (placeholders != null) {
            for (Map.Entry<String, String> e : placeholders.entrySet()) {
                fullMsg = fullMsg.replace(e.getKey(), e.getValue());
            }
        }
        plugin.getAdventure().player(p).sendMessage(MessageUtils.parse(p, fullMsg));
    }
}
