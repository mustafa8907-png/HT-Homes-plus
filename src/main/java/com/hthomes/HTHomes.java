package com.hthomes;

import com.hthomes.commands.HomeCommand;
import com.hthomes.hooks.HookManager;
import com.hthomes.listeners.GUIListener;
import com.hthomes.managers.*;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;

public class HTHomes extends JavaPlugin {
    private static HTHomes instance;
    private BukkitAudiences adventure;
    private LanguageManager languageManager;
    private HomeManager homeManager;
    private HookManager hookManager;
    private LimitManager limitManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        // 12 Dil Dosyasını Yükle
        String[] langs = {"ar", "br", "de", "en", "es", "fr", "hi", "it", "ja", "ko", "ru", "tr"};
        for (String lang : langs) saveResource("languages/" + lang + ".yml", false);

        this.adventure = BukkitAudiences.create(this);
        this.languageManager = new LanguageManager(this);
        this.homeManager = new HomeManager(this);
        this.hookManager = new HookManager(this);
        this.limitManager = new LimitManager(this);
        
        GUIManager.setPlugin(this);
        getCommand("home").setExecutor(new HomeCommand(this));
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);

        getLogger().info("🏠 HT-Homes+ has been enabled successfully!");
    }

    public static HTHomes getInstance() { return instance; }
    public BukkitAudiences getAdventure() { return adventure; }
    public LanguageManager getLangManager() { return languageManager; }
    public HomeManager getHomeManager() { return homeManager; }
    public HookManager getHookManager() { return hookManager; }
    public LimitManager getLimitManager() { return limitManager; }
}
