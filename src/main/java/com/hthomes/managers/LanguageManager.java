package com.hthomes.managers;

import com.hthomes.HTHomes;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
        // config.yml'den hangi dilin seçildiğini oku (Varsayılan: en)
        String langTag = plugin.getConfig().getString("language", "en");
        File langFile = new File(plugin.getDataFolder(), "languages/" + langTag + ".yml");

        // Eğer seçilen dil dosyası klasörde yoksa, hata vermemesi için varsayılan olarak en.yml'ye dön
        if (!langFile.exists()) {
            plugin.getLogger().warning("Dil dosyasi bulunamadi: " + langTag + ".yml! Varsayilan (en.yml) yukleniyor.");
            langFile = new File(plugin.getDataFolder(), "languages/en.yml");
        }

        // Seçilen dil dosyasını yükle
        this.langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    public String getMessage(String path) {
        if (langConfig == null) return "Lang file not loaded!";
        String msg = langConfig.getString(path);
        
        // Eğer mesaj bulunamazsa yolu hata olarak döndür (debug için kolaylık sağlar)
        if (msg == null) return "Missing message: " + path;
        
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
