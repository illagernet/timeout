package net.illager.plugin.timeout;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;

import org.bukkit.entity.Player;
import java.util.Date;

public class LoginListener implements Listener {
    
    Plugin plugin;
    
    public LoginListener(Plugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        String id = player.getUniqueId().toString();
        Date now = new Date();
        
        // If player is dead
        if(plugin.isPlayerDead(id)) {
            
            long remaining = now.getTime() - this.plugin.getRespawnTime(id);
            
            // Player can respawn!
            if(remaining <= 0) {
                this.plugin.removeDeath(id);
                player.spigot().respawn();
            }
            
            // Player must wait
            else {
                event.disallow(
                    PlayerLoginEvent.Result.KICK_BANNED,
                    Plugin.kickMessage(
                        remaining,
                        this.plugin.getDeathLocation(id),
                        this.plugin.getDeathMessage(id)
                    )
                );
            }
        }
    }
}
