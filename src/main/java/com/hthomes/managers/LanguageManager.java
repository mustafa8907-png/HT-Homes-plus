package com.hthomes.managers;

import com.hthomes.HTHomes;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LanguageManager {
    private final HTHomes plugin;
    private FileConfiguration langConfig;

    public LanguageManager(HTHomes plugin) {
        this.plugin = plugin;
        loadLanguage();
    }

    public void loadLanguage() {
        String langTag = plugin.getConfig().getString("language", "en");
        File langFile = new File(plugin.getDataFolder() + "/languages", langTag + ".yml");

        if (!langFile.exists()) {
            plugin.saveResource("languages/" + langTag + ".yml", false);
        }

        this.langConfig = YamlConfiguration.loadConfiguration(langFile);
        
        // Eksik mesajları iç kaynakla tamamla
        InputStream defStream = plugin.getResource("languages/" + langTag + ".yml");
        if (defStream != null) {
            this.langConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defStream, StandardCharsets.UTF_8)));
        }
    }

    public String getMessage(String path) {
        String msg = langConfig.getString(path, "Message not found: " + path);
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
