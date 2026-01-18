package com.hthomes.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class MessageUtils {
    private static final MiniMessage mm = MiniMessage.miniMessage();

    // Metni olduğu gibi MiniMessage formatında işler (Gradientleri korur)
    public static Component parse(Player player, String text) {
        if (text == null) return Component.empty();
        // & simgesi varsa MiniMessage'a uygun hale getirir
        return mm.deserialize(text.replace("&", "§"));
    }
}
