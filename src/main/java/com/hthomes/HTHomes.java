package com.hthomes;

import com.hthomes.commands.HomeCommand;
import com.hthomes.listeners.GUIListener;
import com.hthomes.managers.*;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;

public class HTHomes extends JavaPlugin {
    private static HTHomes instance;
    private BukkitAudiences adventure;
    private LanguageManager languageManager;
    private HomeManager homeManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        // Adventure API başlatma
        this.adventure = BukkitAudiences.create(this);
        
        // Manager sınıflarını yükleme
        this.languageManager = new LanguageManager(this);
        this.homeManager = new HomeManager(this);
        
        // Statik erişim için plugin instance gönderme
        GUIManager.setPlugin(this);

        // Komutları kaydetme
        HomeCommand cmd = new HomeCommand(this);
        getCommand("home").setExecutor(cmd);
        getCommand("homes").setExecutor(cmd);
        getCommand("sethome").setExecutor(cmd);
        getCommand("delhome").setExecutor(cmd);
        
        // Event listener kaydetme
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
    }

    @Override
    public void onDisable() {
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

    // Getter Metotları
    public static HTHomes getInstance() { return instance; }
    public BukkitAudiences getAdventure() { return adventure; }
    public LanguageManager getLangManager() { return languageManager; }
    public HomeManager getHomeManager() { return homeManager; }
}

