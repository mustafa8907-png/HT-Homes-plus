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
        // Config dosyasından dili çeker, yoksa 'tr' varsayılan olur
        this.currentLang = plugin.getConfig().getString("language", "tr");
        loadLanguages();
    }

    /**
     * Diller klasöründeki tüm .yml dosyalarını önbelleğe alır.
     */
    public void loadLanguages() {
        File folder = new File(plugin.getDataFolder(), "languages");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.getName().endsWith(".yml")) {
                    String langName = f.getName().replace(".yml", "");
                    langCache.put(langName, YamlConfiguration.loadConfiguration(f));
                }
            }
        }
    }

    /**
     * Aktif dil dosyasını döndürür. GUIManager ve diğer sınıflar için kritiktir.
     */
    public FileConfiguration getLangConfig() {
        FileConfiguration config = langCache.get(currentLang);
        if (config == null) {
            // Eğer seçili dil yoksa İngilizceye, o da yoksa önbellekteki ilk dile düşer
            config = langCache.getOrDefault("en", langCache.values().stream().findFirst().orElse(null));
        }
        return config;
    }

    /**
     * Ham metni (raw string) dil dosyasından çeker.
     */
    public String getRaw(String path) {
        FileConfiguration config = getLangConfig();
        if (config == null) return "§c[Error] Lang files missing!";
        return config.getString(path, "§c[Missing Path] " + path);
    }

    /**
     * Oyuncuya gradient destekli mesaj gönderir.
     * @param p Mesajın gideceği oyuncu
     * @param path Dil dosyasındaki yol (messages.set-home gibi)
     * @param placeholders Değiştirilecek değişkenler ({home} -> "evim" gibi)
     */
    public void sendMessage(Player p, String path, Map<String, String> placeholders) {
        String prefix = getRaw("messages.prefix");
        String message = getRaw(path);
        
        String fullMessage = prefix + message;

        // Değişkenleri yerleştir (PlaceholderAPI desteği yoksa manuel değişim)
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                fullMessage = fullMessage.replace(entry.getKey(), entry.getValue());
            }
        }

        // Adventure API kullanarak mesajı gönder (Gradientleri korur)
        plugin.getAdventure().player(p).sendMessage(MessageUtils.parse(p, fullMessage));
    }
            }
