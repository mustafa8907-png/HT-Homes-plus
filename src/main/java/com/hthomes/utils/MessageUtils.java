package com.hthomes.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

public class MessageUtils {
    private static final MiniMessage mm = MiniMessage.miniMessage();

    public static Component parse(Player player, String text) {
        if (text == null) return Component.empty();
        return mm.deserialize(text.replace("&", "§"));
    }

    public static String parseToLegacy(Player player, String text) {
        return LegacyComponentSerializer.legacySection().serialize(parse(player, text));
    }

    // FIX: GUIManager'ın aradığı ama bulamadığı metod
    public static String color(String text) {
        if (text == null) return "";
        // Oyuncu bağımsız hızlı çeviri için legacy serializer kullanır
        return LegacyComponentSerializer.legacySection().serialize(mm.deserialize(text.replace("&", "§")));
    }
}
