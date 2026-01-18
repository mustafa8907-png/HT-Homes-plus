package com.hthomes;

import com.hthomes.commands.HomeCommand;
import com.hthomes.managers.*;
import com.hthomes.utils.MessageUtils;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public class HTHomes extends JavaPlugin {
    private static HTHomes instance;
    private BukkitAudiences adventure;
    private LanguageManager languageManager;
    private HomeManager homeManager;
    private HookManager hookManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        createLanguageFiles();

        // Initialize Adventure for MiniMessage support
        this.adventure = BukkitAudiences.create(this);

        this.hookManager = new HookManager();
        this.languageManager = new LanguageManager(this);
        this.homeManager = new HomeManager(this);
        
        GUIManager.setPlugin(this);

        // Register Commands
        if (getCommand("home") != null) getCommand("home").setExecutor(new HomeCommand(this));
        if (getCommand("sethome") != null) getCommand("sethome").setExecutor(new HomeCommand(this));
        if (getCommand("delhome") != null) getCommand("delhome").setExecutor(new HomeCommand(this));

        getLogger().info("HT-Homes+ Enabled (Java 17 / MiniMessage Fixed)");
    }

    @Override
    public void onDisable() {
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

    private void createLanguageFiles() {
        String[] langs = {"tr", "en", "de", "fr", "es", "it", "br", "ru", "ko", "ja", "hi", "ar", "zh"};
        File folder = new File(getDataFolder(), "languages");
        if (!folder.exists()) folder.mkdirs();
        for (String l : langs) {
            if (!new File(folder, l + ".yml").exists()) {
                saveResource("languages/" + l + ".yml", false);
            }
        }
    }

    public static HTHomes getInstance() { return instance; }
    public BukkitAudiences getAdventure() { return adventure; }
    public LanguageManager getLangManager() { return languageManager; }
    public HomeManager getHomeManager() { return homeManager; }
    public HookManager getHookManager() { return hookManager; }
}
