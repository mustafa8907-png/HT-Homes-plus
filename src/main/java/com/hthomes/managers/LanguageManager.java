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
    private final String currentLang;

    public LanguageManager(HTHomes plugin) {
        this.plugin = plugin;
        this.currentLang = plugin.getConfig().getString("language", "tr");
        loadLanguages();
    }

    private void loadLanguages() {
        File folder = new File(plugin.getDataFolder(), "languages");
        if (!folder.exists()) folder.mkdirs();
        
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.getName().endsWith(".yml")) {
                    langCache.put(f.getName().replace(".yml", ""), YamlConfiguration.loadConfiguration(f));
                }
            }
        }
    }

    // Fixed: Added missing method
    public FileConfiguration getLangConfig() {
        return langCache.getOrDefault(currentLang, langCache.get("en"));
    }

    public String getRaw(String path) {
        FileConfiguration config = getLangConfig();
        if (config == null) return "Error: Lang file not found";
        return config.getString(path, "Missing: " + path);
    }

    public void sendMessage(Player p, String path, Map<String, String> placeholders) {
        String msg = getRaw("messages.prefix") + getRaw(path);
        if (placeholders != null) {
            for (Map.Entry<String, String> e : placeholders.entrySet()) {
                msg = msg.replace(e.getKey(), e.getValue());
            }
        }
        // Use Adventure Audience to send MiniMessage component
        plugin.getAdventure().player(p).sendMessage(MessageUtils.parse(p, msg));
    }
}
