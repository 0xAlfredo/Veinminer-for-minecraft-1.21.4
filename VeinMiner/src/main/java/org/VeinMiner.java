package com.example.veinminer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class VeinMiner extends JavaPlugin implements Listener {

    private final int MAX_BLOCKS = 64;
    private final BlockFace[] FACES = {
            BlockFace.UP, BlockFace.DOWN,
            BlockFace.NORTH, BlockFace.SOUTH,
            BlockFace.EAST, BlockFace.WEST
    };

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block startBlock = event.getBlock();
        ItemStack tool = player.getInventory().getItemInMainHand();

        if (!player.isSneaking()) return;
        if (!Tag.ITEMS_PICKAXES.isTagged(tool.getType())) return;
        if (!isOre(startBlock.getType())) return;

        mineVein(startBlock, player, tool);
    }

    private void mineVein(Block startBlock, Player player, ItemStack tool) {
        Material targetMaterial = startBlock.getType();
        Queue<Block> queue = new LinkedList<>();
        Set<Block> visited = new HashSet<>();

        queue.add(startBlock);
        visited.add(startBlock);

        int blocksBroken = 0;

        while (!queue.isEmpty() && blocksBroken < MAX_BLOCKS) {
            Block current = queue.poll();

            if (!current.equals(startBlock)) {
                current.breakNaturally(tool);
                blocksBroken++;

                if (tool.getItemMeta() instanceof Damageable) {
                    Damageable meta = (Damageable) tool.getItemMeta();
                    meta.setDamage(meta.getDamage() + 1);
                    tool.setItemMeta((ItemMeta) meta);

                    if (meta.getDamage() >= tool.getType().getMaxDurability()) {
                        player.getInventory().setItemInMainHand(null);
                        break;
                    }
                }
            }

            for (BlockFace face : FACES) {
                Block relative = current.getRelative(face);
                if (!visited.contains(relative) && isSameOre(targetMaterial, relative.getType())) {
                    visited.add(relative);
                    queue.add(relative);
                }
            }
        }
    }

    private boolean isOre(Material mat) {
        return Tag.COAL_ORES.isTagged(mat)
                || Tag.IRON_ORES.isTagged(mat)
                || Tag.COPPER_ORES.isTagged(mat)
                || Tag.GOLD_ORES.isTagged(mat)
                || Tag.LAPIS_ORES.isTagged(mat)
                || Tag.REDSTONE_ORES.isTagged(mat)
                || Tag.DIAMOND_ORES.isTagged(mat)
                || Tag.EMERALD_ORES.isTagged(mat)
                || mat == Material.NETHER_QUARTZ_ORE
                || mat == Material.NETHER_GOLD_ORE
                || mat == Material.ANCIENT_DEBRIS;
    }

    private boolean isSameOre(Material target, Material check) {
        if (target == check) return true;
        if (Tag.COAL_ORES.isTagged(target) && Tag.COAL_ORES.isTagged(check)) return true;
        if (Tag.IRON_ORES.isTagged(target) && Tag.IRON_ORES.isTagged(check)) return true;
        if (Tag.COPPER_ORES.isTagged(target) && Tag.COPPER_ORES.isTagged(check)) return true;
        if (Tag.GOLD_ORES.isTagged(target) && Tag.GOLD_ORES.isTagged(check)) return true;
        if (Tag.LAPIS_ORES.isTagged(target) && Tag.LAPIS_ORES.isTagged(check)) return true;
        if (Tag.REDSTONE_ORES.isTagged(target) && Tag.REDSTONE_ORES.isTagged(check)) return true;
        if (Tag.DIAMOND_ORES.isTagged(target) && Tag.DIAMOND_ORES.isTagged(check)) return true;
        if (Tag.EMERALD_ORES.isTagged(target) && Tag.EMERALD_ORES.isTagged(check)) return true;
        return false;
    }
}