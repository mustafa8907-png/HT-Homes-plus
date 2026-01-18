package com.hthomes;

import com.hthomes.listeners.GUIListener;
import com.hthomes.managers.*;
import com.hthomes.utils.MessageUtils;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public class HTHomes extends JavaPlugin {
    private static HTHomes instance;
    private HomeManager homeManager;
    private HookManager hookManager;
    private LanguageManager languageManager;
    private BukkitAudiences adventure;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        this.adventure = BukkitAudiences.create(this);
        this.hookManager = new HookManager();
        MessageUtils.setHooks(hookManager);
        
        createLanguageFiles();
        this.languageManager = new LanguageManager(this);
        this.homeManager = new HomeManager(this);
        
        GUIManager.setPlugin(this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);

        getLogger().info("========================================");
        getLogger().info("HT-Homes+ v5.1.0 ENABLED");
        getLogger().info("Author: mustata8907");
        getLogger().info("========================================");
    }

    @Override
    public void onDisable() {
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

    private void createLanguageFiles() {
        String[] langs = {"tr", "en", "de", "fr", "es", "it", "br", "ru", "ko", "ja", "hi", "ar"};
        File folder = new File(getDataFolder(), "languages");
        if (!folder.exists()) folder.mkdirs();
        for (String l : langs) {
            if (!new File(folder, l + ".yml").exists()) saveResource("languages/" + l + ".yml", false);
        }
    }

    public static HTHomes getInstance() { return instance; }
    public HomeManager getHomeManager() { return homeManager; }
    public HookManager getHookManager() { return hookManager; }
    public LanguageManager getLangManager() { return languageManager; }
    public BukkitAudiences getAdventure() { return adventure; }
}
