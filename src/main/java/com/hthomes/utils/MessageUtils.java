package com.hthomes.utils;

import com.hthomes.managers.HookManager;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageUtils {
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static HookManager hooks;

    public static void setHooks(HookManager h) { hooks = h; }

    public static Component parse(Player p, String text) {
        if (text == null) return Component.empty();
        if (hooks != null && hooks.hasPAPI() && p != null) {
            text = PlaceholderAPI.setPlaceholders(p, text);
        }
        // Hem Legacy (&a) hem MiniMessage (<red>) desteği
        String legacy = ChatColor.translateAlternateColorCodes('&', text);
        return LegacyComponentSerializer.legacySection().deserialize(legacy);
    }

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
  }
