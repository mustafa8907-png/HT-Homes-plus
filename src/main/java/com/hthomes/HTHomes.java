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
        this.adventure = BukkitAudiences.create(this);
        this.homeManager = new HomeManager(this);
        this.languageManager = new LanguageManager(this);
        GUIManager.setPlugin(this);

        HomeCommand cmd = new HomeCommand(this);
        getCommand("home").setExecutor(cmd);
        getCommand("homes").setExecutor(cmd);
        getCommand("sethome").setExecutor(cmd);
        getCommand("delhome").setExecutor(cmd);
        
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
    }

    @Override
    public void onDisable() { if(this.adventure != null) this.adventure.close(); }
    public static HTHomes getInstance() { return instance; }
    public BukkitAudiences getAdventure() { return adventure; }
    public LanguageManager getLangManager() { return languageManager; }
    public HomeManager getHomeManager() { return homeManager
        ; }
    }
