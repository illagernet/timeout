package net.illager.plugin.timeout;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.block.Action;
import java.util.Date;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;

public class ResurrectionRune implements Listener {
    Plugin plugin;
    
    public ResurrectionRune(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFeedRune(final PlayerInteractEvent event) {
        
        if(
            // Right clicked
            event.getAction() == Action.RIGHT_CLICK_BLOCK &&
            
            // Conduit
            event.getClickedBlock().getType() == Material.CONDUIT &&
            
            // Fire
            event.getClickedBlock().getRelative(-2, 0, -2).getType() == Material.FIRE &&
            event.getClickedBlock().getRelative(2, 0, -2).getType() == Material.FIRE &&
            event.getClickedBlock().getRelative(-2, 0, 2).getType() == Material.FIRE &&
            event.getClickedBlock().getRelative(2, 0, 2).getType() == Material.FIRE &&
            
            // Chest
            event.getClickedBlock().getRelative(0, -1, 0).getType() == Material.CHEST &&
            
            // Netherrack
            event.getClickedBlock().getRelative(-2, -1, -2).getType() == Material.NETHERRACK &&
            event.getClickedBlock().getRelative(2, -1, -2).getType() == Material.NETHERRACK &&
            event.getClickedBlock().getRelative(-2, -1, 2).getType() == Material.NETHERRACK &&
            event.getClickedBlock().getRelative(2, -1, 2).getType() == Material.NETHERRACK &&
            
            // Obsidian
            event.getClickedBlock().getRelative(0, -2, 0).getType() == Material.OBSIDIAN &&
            event.getClickedBlock().getRelative(1, -2, 0).getType() == Material.OBSIDIAN &&
            event.getClickedBlock().getRelative(-1, -2, 0).getType() == Material.OBSIDIAN &&
            event.getClickedBlock().getRelative(0, -2, 1).getType() == Material.OBSIDIAN &&
            event.getClickedBlock().getRelative(0, -2, -1).getType() == Material.OBSIDIAN &&
            event.getClickedBlock().getRelative(-2, -2, -2).getType() == Material.OBSIDIAN &&
            event.getClickedBlock().getRelative(2, -2, -2).getType() == Material.OBSIDIAN &&
            event.getClickedBlock().getRelative(-2, -2, 2).getType() == Material.OBSIDIAN &&
            event.getClickedBlock().getRelative(2, -2, 2).getType() == Material.OBSIDIAN &&

            // Holding a diamonds
            event.hasItem() &&
            event.getItem().getType() == Material.DIAMOND
        ) {

            // Check named items in chest
            Chest bank = (Chest) event.getClickedBlock().getRelative(0, -1, 0).getState();
            for(ItemStack item : bank.getBlockInventory().getContents()) {

                // Named piece of paper
                if(
                    item != null &&
                    item.getType() == Material.PAPER &&
                    item.hasItemMeta() &&
                    item.getItemMeta().hasDisplayName()
                ) {
                    
                    String username = item.getItemMeta().getDisplayName();
                    String id = Bukkit.getOfflinePlayer(username).getUniqueId().toString();
                    Date now = new Date();

                    if(this.plugin.isPlayerDead(id)) {
                        long remaining = this.plugin.getRespawnTime(id) - now.getTime();
                        
                        // Player can already respawn
                        if(remaining <= 0) {
                            this.plugin.removeDeath(id);
                            continue;
                        }
                        
                        else {
                            this.plugin.discountDeath(id, Plugin.ONE_HOUR);
                            event.getItem().setAmount(event.getItem().getAmount() - 1);
                            return;
                        }
                    }
                }
            }

            // No players to discount
            event.getClickedBlock().getRelative(-2, 0, -2).setType(Material.AIR);
            event.getClickedBlock().getRelative(2, 0, -2).setType(Material.AIR);
            event.getClickedBlock().getRelative(-2, 0, 2).setType(Material.AIR);
            event.getClickedBlock().getRelative(2, 0, 2).setType(Material.AIR);
        }
    }
}
