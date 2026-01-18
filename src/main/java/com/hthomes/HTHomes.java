package com.hthomes;

import com.hthomes.commands.HomeCommand;
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

        // 1. Config dosyasını yükle
        saveDefaultConfig();

        // 2. Adventure (MiniMessage) Başlatma
        this.adventure = BukkitAudiences.create(this);

        // 3. HookManager Başlatma (PAPI, WG, GP)
        this.hookManager = new HookManager();
        MessageUtils.setHooks(hookManager);

        // 4. Dil Dosyalarını Oluştur ve Yükle
        createLanguageFiles();
        this.languageManager = new LanguageManager(this);

        // 5. Veri ve GUI Yöneticilerini Başlat
        this.homeManager = new HomeManager(this);
        GUIManager.setPlugin(this);

        // 6. Komutları Kaydet
        HomeCommand homeCommand = new HomeCommand(this);
        getCommand("home").setExecutor(homeCommand);
        getCommand("sethome").setExecutor(homeCommand);
        getCommand("delhome").setExecutor(homeCommand);

        // 7. Eventleri Kaydet
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);

        // Konsol Açılış Mesajı (İngilizce)
        getLogger().info("========================================");
        getLogger().info("HT-Homes+ PREMIUM v5.1.0 ENABLED");
        getLogger().info("Author: mustata8907");
        getLogger().info("Support: MiniMessage, PAPI, WG, GP");
        getLogger().info("========================================");
    }

    @Override
    public void onDisable() {
        // Adventure kapatma (Memory leak önlemek için)
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        getLogger().info("HT-Homes+ Premium has been disabled.");
    }

    /**
     * languages klasörüne varsayılan dil dosyalarını çıkartır.
     */
    private void createLanguageFiles() {
        String[] langs = {"tr", "en", "de", "fr", "es", "it", "br", "ru", "ko", "ja", "hi", "ar"};
        File folder = new File(getDataFolder(), "languages");
        if (!folder.exists()) folder.mkdirs();
        
        for (String l : langs) {
            File langFile = new File(folder, l + ".yml");
            if (!langFile.exists()) {
                saveResource("languages/" + l + ".yml", false);
            }
        }
    }

    // Getters
    public static HTHomes getInstance() { return instance; }
    public HomeManager getHomeManager() { return homeManager; }
    public HookManager getHookManager() { return hookManager; }
    public LanguageManager getLangManager() { return languageManager; }
    public BukkitAudiences getAdventure() { return adventure; }

    /**
     * Eski kodlarla uyumluluk için yardımcı metod
     */
    public org.bukkit.configuration.file.FileConfiguration getLang() {
        return languageManager.getLangConfig();
    }
}
