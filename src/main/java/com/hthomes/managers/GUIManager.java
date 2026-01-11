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
import java.util.Map;

public class GUIManager {
    public static void open(Player p) {
        HTHomes pl = HTHomes.getInstance();
        LanguageManager lm = pl.getLanguageManager();
        HomeManager hm = pl.getHomeManager();

        int rows = pl.getConfig().getInt("gui.rows", 6);
        Inventory inv = Bukkit.createInventory(null, rows * 9, lm.getRawString("gui.title"));

        Map<String, org.bukkit.Location> homes = hm.getHomes(p);
        int limit = hm.getPlayerLimit(p);
        Material fMat = Material.matchMaterial(pl.getConfig().getString("gui.materials.filled", "GREEN_BED"));
        Material eMat = Material.matchMaterial(pl.getConfig().getString("gui.materials.empty", "RED_BED"));

        int slot = 0;
        for (Map.Entry<String, org.bukkit.Location> entry : homes.entrySet()) {
            if (slot >= rows * 9) break;
            inv.setItem(slot++, createFilled(fMat, lm, entry.getKey(), entry.getValue()));
        }
        while (slot < limit && slot < rows * 9) {
            inv.setItem(slot++, createEmpty(eMat, lm));
        }
        p.openInventory(inv);
        playSound(p, "gui.sounds.open");
    }

    private static ItemStack createFilled(Material m, LanguageManager l, String n, org.bukkit.Location loc) {
        ItemStack i = new ItemStack(m != null ? m : Material.GREEN_BED);
        ItemMeta mt = i.getItemMeta();
        mt.setDisplayName(l.getRawString("gui.items.filled.name").replace("{home}", n));
        List<String> lr = new ArrayList<>();
        l.getStringList("gui.items.filled.lore").forEach(s -> lr.add(s.replace("{world}", loc.getWorld().getName()).replace("{x}", String.valueOf(loc.getBlockX())).replace("{y}", String.valueOf(loc.getBlockY())).replace("{z}", String.valueOf(loc.getBlockZ()))));
        mt.setLore(lr); i.setItemMeta(mt); return i;
    }

    private static ItemStack createEmpty(Material m, LanguageManager l) {
        ItemStack i = new ItemStack(m != null ? m : Material.RED_BED);
        ItemMeta mt = i.getItemMeta();
        mt.setDisplayName(l.getRawString("gui.items.empty.name"));
        mt.setLore(l.getStringList("gui.items.empty.lore"));
        i.setItemMeta(mt); return i;
    }

    public static void playSound(Player p, String path) {
        try { p.playSound(p.getLocation(), org.bukkit.Sound.valueOf(HTHomes.getInstance().getConfig().getString(path)), 1f, 1f); } catch (Exception ignored) {}
    }
          }
