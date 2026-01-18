package com.hthomes.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

public class MessageUtils {
    private static final MiniMessage mm = MiniMessage.miniMessage();

    /**
     * Parses MiniMessage strings into Adventure Components.
     * Supports legacy (&) codes by converting them first.
     */
    public static Component parse(Player player, String text) {
        if (text == null) return Component.empty();
        // Replace legacy color codes first, then parse MiniMessage
        return mm.deserialize(text.replace("&", "§"));
    }

    /**
     * Converts Component to Legacy String for GUI Titles and Item Names.
     * This fixes the issue where gradients appear as raw text in inventories.
     */
    public static String parseToLegacy(Player player, String text) {
        Component component = parse(player, text);
        return LegacyComponentSerializer.legacySection().serialize(component);
    }
    }
