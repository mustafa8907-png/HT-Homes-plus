package com.hthomes;

import com.hthomes.commands.HomeCommand;
import com.hthomes.listeners.GUIListener;
import com.hthomes.managers.*;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Arrays;
import java.util.List;

public class HTHomes extends JavaPlugin {
    private static HTHomes instance;
    private BukkitAudiences adventure;
    private LanguageManager languageManager;
    private HomeManager homeManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        // Tüm dil dosyalarını klasöre çıkart
        loadAllLanguages();

        this.adventure = BukkitAudiences.create(this);
        this.languageManager = new LanguageManager(this);
        this.homeManager = new HomeManager(this);
        
        GUIManager.setPlugin(this);

        HomeCommand cmd = new HomeCommand(this);
        getCommand("home").setExecutor(cmd);
        getCommand("sethome").setExecutor(cmd);
        getCommand("delhome").setExecutor(cmd);
        
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
    }

    private void loadAllLanguages() {
        // Desteklenen 15 dil (TR + 14 Diğer)
        List<String> languages = Arrays.asList(
            "tr", "en", "de", "fr", "es", "it", "ru", 
            "pt", "pl", "zh", "ja", "ko", "nl", "ar", "hi"
        );

        for (String lang : languages) {
            String fileName = "languages/" + lang + ".yml";
            // Dosya yoksa resources içinden çıkart
            if (getResource(fileName) != null) {
                saveResource(fileName, false);
            }
        }
    }

    @Override
    public void onDisable() {
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

    public static HTHomes getInstance() { return instance; }
    public BukkitAudiences getAdventure() { return adventure; }
    public LanguageManager getLangManager() { return languageManager; }
    public HomeManager getHomeManager() { return homeManager; }
}
