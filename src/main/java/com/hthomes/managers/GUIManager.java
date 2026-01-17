package com.hthomes.managers;

import com.hthomes.HTHomes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIManager {
    private final HTHomes plugin;
    private final Material[] ICON_OPTIONS = {
        Material.CRAFTING_TABLE, Material.OAK_LOG, Material.GOLDEN_APPLE, Material.ENCHANTED_GOLDEN_APPLE,
        Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE,
        Material.WHITE_BED, Material.ORANGE_BED, Material.YELLOW_BED, Material.LIME_BED, Material.CYAN_BED, Material.BLUE_BED, Material.PURPLE_BED, Material.RED_BED
    };

    public GUIManager(HTHomes plugin) { this.plugin = plugin; }

    public void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "HT-Homes: My Homes");
        if (plugin.getHomeManager().getHomesConfig().contains(player.getUniqueId().toString())) {
            for (String homeName : plugin.getHomeManager().getHomesConfig().getConfigurationSection(player.getUniqueId().toString()).getKeys(false)) {
                Material icon = plugin.getHomeManager().getHomeIcon(player, homeName);
                inv.addItem(createGuiItem(icon, "§a" + homeName, "§7Click to manage."));
            }
        }
        player.openInventory(inv);
    }

    public void openManagementMenu(Player player, String homeName) {
        Inventory inv = Bukkit.createInventory(null, 27, "Manage: " + homeName);
        inv.setItem(11, createGuiItem(Material.ENDER_PEARL, "§aTeleport", "§7Go to your home."));
        inv.setItem(13, createGuiItem(Material.PAINTING, "§eChange Icon", "§7Customize your home icon."));
        inv.setItem(15, createGuiItem(Material.SKELETON_SKULL, "§cDelete", "§7Delete this home forever."));
        inv.setItem(26, createGuiItem(Material.BARRIER, "§cBack", "§7Return to menu."));
        player.openInventory(inv);
    }

    public void openIconSelectionMenu(Player player, String homeName) {
        Inventory inv = Bukkit.createInventory(null, 36, "Select Icon: " + homeName);
        for (Material mat : ICON_OPTIONS) {
            inv.addItem(createGuiItem(mat, "§eSelect Icon", "§7Set as your home icon."));
        }
        inv.setItem(35, createGuiItem(Material.BARRIER, "§cCancel", "§7Go back."));
        player.openInventory(inv);
    }

    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        List<String> list = new ArrayList<>();
        for (String s : lore) list.add(s);
        meta.setLore(list);
        item.setItemMeta(meta);
        return item;
    }
    }
