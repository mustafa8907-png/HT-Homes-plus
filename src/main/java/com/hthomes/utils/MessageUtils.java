package com.hthomes.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageUtils {

    // Hem MiniMessage hem de Legacy (&) kodlarını destekleyen ana metot
    public static Component parse(Player player, String text) {
        if (text == null) return Component.empty();
        
        // Önce & kodlarını temizle ve MiniMessage'ın anlayacağı hale getir
        String processedText = text.replace("&", "§");
        Component legacyComponent = LegacyComponentSerializer.legacySection().deserialize(processedText);
        String miniMessageText = MiniMessage.miniMessage().serialize(legacyComponent);
        
        // Şimdi MiniMessage olarak işle (Gradyanlar burada çalışır)
        return MiniMessage.miniMessage().deserialize(text.replace("§", "&"));
    }

    // GUI Başlıkları ve basit renkler için (Legacy + MiniMessage uyumlu)
    public static String color(String text) {
        if (text == null) return "";
        // Eğer içinde MiniMessage etiketi varsa (<), MiniMessage olarak dönüştür
        if (text.contains("<")) {
            return ChatColor.translateAlternateColorCodes('&', 
                LegacyComponentSerializer.legacyAmpersand().serialize(MiniMessage.miniMessage().deserialize(text)));
        }
        // Yoksa sadece klasik renkleri çevir
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
