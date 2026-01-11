package com.hthomes;

import com.hthomes.commands.HomeCommand;
import com.hthomes.listeners.GUIListener;
import com.hthomes.managers.HomeManager;
import com.hthomes.managers.LanguageManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HTHomes extends JavaPlugin {
    private static HTHomes instance;
    private HomeManager homeManager;
    private LanguageManager languageManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        this.languageManager = new LanguageManager(this);
        this.homeManager = new HomeManager(this);
        
        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("sethome").setExecutor(new HomeCommand(this));
        getCommand("delhome").setExecutor(new HomeCommand(this));
        
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        
        getLogger().info("\n§a-----------------------------\n§6HT-Homes+ §eAktif!\n§fSürüm: 2.1.0\n§a-----------------------------");
    }

    @Override
    public void onDisable() {
        if (homeManager != null) homeManager.saveHomes();
        getLogger().info("§cHT-Homes+ Kapatıldı, Veriler Kaydedildi.");
    }

    public static HTHomes getInstance() { return instance; }
    public HomeManager getHomeManager() { return homeManager; }
    public LanguageManager getLanguageManager() { return languageManager; }
            }
