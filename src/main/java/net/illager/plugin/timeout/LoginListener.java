package net.illager.plugin.timeout;

import org.bukkit.event.Listener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.entity.Player;
import java.util.Date;

public class LoginListener implements Listener {
    FileConfiguration deathLog;

    public LoginListener(FileConfiguration config) {
        this.deathLog = config;
    }
    
    @EventHandler
    public void onPlayerLogin(final PlayerLoginEvent event) {
        Player player = event.getPlayer();
        String playerId = player.getUniqueId().toString();
        Date now = new Date();
        long msSinceDeath = now.getTime() - this.deathLog.getLong(playerId);
        if(msSinceDeath < App.TIMEOUT) {
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, App.kickMessage(App.TIMEOUT - msSinceDeath, "Respawning in "));
        }
    }
}
