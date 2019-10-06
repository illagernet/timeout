package net.illager.timeout;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

class PlayerDeath implements Listener {
    
    private Timeout plugin;

    public PlayerDeath(Timeout plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        this.plugin.getDeathLog().addEntry();
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new KickPlayer(this.plugin, player), 20L);
    }
}
