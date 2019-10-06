package net.illager.timeout;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

class PlayerLogin implements Listener {
    
    Timeout plugin;

    public PlayerLogin(Timeout plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        

        // If entry found in deathlog
        if(this.plugin.isDead(id)) {
            // If timeout complete
            if(timeout <= 0) {
                // Remove entry from deathlog
                this.plugin.removeDeath(id);
            } else {
                String kickMessage = Timeout.kickMessage(timeout, location, this.plugin.getDeathMessage(id)); 
                // Disallow login with detailed message
                event.disallow(
                    PlayerLoginEvent.Result.KICK_BANNED,
                    Timeout.kickMessage(
                        this.plugin.getTimeout(id),
                        this.plugin.getDeathLocation(id),
                        this.plugin.getDeathMessage(id)
                    )
                );
            }
        }
    }
}
