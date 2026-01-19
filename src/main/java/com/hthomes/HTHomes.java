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
        
        // Adventure kütüphanesi başlatılıyor
        this.adventure = BukkitAudiences.create(this);
        
        // Yöneticiler başlatılıyor
        this.languageManager = new LanguageManager(this);
        this.homeManager = new HomeManager(this);
        
        // GUI Manager'a plugin instance veriliyor
        GUIManager.setPlugin(this);

        // Komutlar kaydediliyor
        HomeCommand cmd = new HomeCommand(this);
        getCommand("home").setExecutor(cmd);
        getCommand("homes").setExecutor(cmd);
        getCommand("sethome").setExecutor(cmd);
        getCommand("delhome").setExecutor(cmd);
        
        // Event Listener kaydediliyor
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
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
// HATA BURADAYDI: Buradaki } parantezi eksikti, şimdi ekl
    endi.
