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

    public static String color(String text) {
        if (text == null) return "";
        return LegacyComponentSerializer.legacySection().serialize(mm.deserialize(text.replace("&", "§")));
    }

    public static String parseToLegacy(Player player, String text) {
        return color(text);
    }
            }
