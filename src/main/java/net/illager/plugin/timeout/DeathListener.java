package net.illager.plugin.timeout;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import java.util.Date;
import org.bukkit.Bukkit;
import java.lang.Runnable;

public class DeathListener implements Listener {
    
    Plugin plugin;
    
    public DeathListener(Plugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        
        Player player = event.getEntity();
        String id = player.getUniqueId().toString();
        String message = event.getDeathMessage();
        Location location = player.getLocation();
        Date now = new Date();
        
        // Log Death
        this.plugin.logDeath(id, now.getTime(), location, message);

        // Schedule kick
        Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable() {

            @Override
            public void run() {
                player.kickPlayer(
                    Plugin.kickMessage(
                        Plugin.SEVENTY_TWO_HOURS,
                        location,
                        message
                    )
                );
            }
        }, 20L);
    }
}
