package com.hthomes.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageUtils {
    public static Component parse(Player player, String text) {
        if (text == null) return Component.empty();
        // & kodlarını MiniMessage formatına çevirerek çakışmayı önle
        String colorized = ChatColor.translateAlternateColorCodes('&', text);
        return MiniMessage.miniMessage().deserialize(text.replace("§", "&"));
    }

    public static String color(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', 
            LegacyComponentSerializer.legacyAmpersand().serialize(MiniMessage.miniMessage().deserialize(text)));
    }
}
