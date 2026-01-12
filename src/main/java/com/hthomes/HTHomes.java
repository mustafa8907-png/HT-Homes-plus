package com.hthomes;

import com.hthomes.commands.HomeCommand;
import com.hthomes.listeners.GUIListener;
import com.hthomes.managers.HomeManager;
import com.hthomes.managers.LanguageManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public class HTHomes extends JavaPlugin {
    private static HTHomes instance;
    private HomeManager homeManager;
    private LanguageManager languageManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // 1. Önce config.yml'yi oluştur
        saveDefaultConfig();
        
        // 2. Dil dosyalarını klasöre dök (Eksik olanları çıkartır)
        exportDefaultLanguages();
        
        // 3. Manager'ları başlat
        this.languageManager = new LanguageManager(this);
        this.homeManager = new HomeManager(this);
        
        // 4. Komutlar ve Listenerlar
        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("sethome").setExecutor(new HomeCommand(this));
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
    }

    private void exportDefaultLanguages() {
        String[] languages = {"en", "tr", "ru", "ar", "es", "hi", "de", "pt", "ja", "br", "fr"};
        File langDir = new File(getDataFolder(), "languages");
        
        if (!langDir.exists()) langDir.mkdirs();
        
        for (String lang : languages) {
            String path = "languages/" + lang + ".yml";
            File file = new File(getDataFolder(), path);
            if (!file.exists()) {
                saveResource(path, false);
            }
        }
    }

    public static HTHomes getInstance() { return instance; }
    public HomeManager getHomeManager() { return homeManager; }
    public LanguageManager getLanguageManager() { return languageManager; }
    }
