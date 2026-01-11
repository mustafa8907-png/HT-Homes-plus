package com.hthomes.managers;

import com.hthomes.HTHomes;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class LanguageManager {
    private final HTHomes plugin;
    private FileConfiguration langConfig;

    public LanguageManager(HTHomes plugin) {
        this.plugin = plugin;
        loadLanguage();
    }

    public void loadLanguage() {
        String langName = plugin.getConfig().getString("language", "en");
        File langFile = new File(plugin.getDataFolder(), "languages/" + langName + ".yml");
        if (!langFile.exists()) {
            langFile.getParentFile().mkdirs();
            plugin.saveResource("languages/en.yml", false);
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    public String getMessage(String path) {
        String prefix = langConfig.getString("prefix", "");
        String msg = langConfig.getString(path, path);
        return ChatColor.translateAlternateColorCodes('&', prefix + msg);
    }

    public String getRawString(String path) {
        return ChatColor.translateAlternateColorCodes('&', langConfig.getString(path, path));
    }

    public List<String> getStringList(String path) {
        return langConfig.getStringList(path).stream()
                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                .collect(Collectors.toList());
    }
}
