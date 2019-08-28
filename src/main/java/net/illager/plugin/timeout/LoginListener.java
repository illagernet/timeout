package net.illager.plugin.timeout;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.entity.Player;
import java.util.Date;
import java.util.logging.Level;

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
            
            long remaining = this.plugin.getRespawnTime(id) - now.getTime();

            // Player can respawn!
            if(remaining <= 0) {
                this.plugin.removeDeath(id);
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
