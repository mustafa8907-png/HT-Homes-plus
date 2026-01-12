package com.hthomes.managers;

import com.hthomes.HTHomes;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class LanguageManager {
    private final HTHomes plugin;
    private FileConfiguration langConfig;

    public LanguageManager(HTHomes plugin) {
        this.plugin = plugin;
        reloadLanguages();
    }

    public void reloadLanguages() {
        // config.yml'den dili çek (varsayılan: en)
        String langTag = plugin.getConfig().getString("language", "en");
        File langFile = new File(plugin.getDataFolder(), "languages/" + langTag + ".yml");

        // Dosya yoksa en.yml'ye geri dön
        if (!langFile.exists()) {
            langFile = new File(plugin.getDataFolder(), "languages/en.yml");
        }

        // Eğer en.yml bile yoksa (ilk kurulum hatası önleyici)
        if (langFile.exists()) {
            this.langConfig = YamlConfiguration.loadConfiguration(langFile);
        }
    }

    public String getMessage(String path) {
        if (langConfig == null) return "§cLanguage file not loaded!";
        
        String msg = langConfig.getString(path);
        if (msg == null) return "§cMissing path: " + path;
        
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
