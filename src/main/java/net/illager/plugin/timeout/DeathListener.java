package net.illager.plugin.timeout;

import org.bukkit.event.Listener;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import java.lang.Runnable;
import java.util.Date;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DeathListener implements Listener {
    App timeoutPlugin;
    File deathLogFile;
    FileConfiguration deathLog;
    Logger serverLog;

    public DeathListener(App plugin, File file, FileConfiguration config, Logger log) {
        this.timeoutPlugin = plugin;
        this.deathLogFile = file;
        this.deathLog = config;
        this.serverLog = log;
    }
    
    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        Player player = event.getEntity();
        String playerId = player.getUniqueId().toString();
        Location playerPos = player.getLocation();
        
        // Kicking player 1 second after death
        Bukkit.getServer().getScheduler().runTaskLater(this.timeoutPlugin, new Runnable() {
            @Override
            public void run() {
                player.kickPlayer(
                    App.kickMessage(
                        App.TIMEOUT,
                        String.format(
                            "You died at (%d, %d, %d). Respawning in ",
                            playerPos.getBlockX(),
                            playerPos.getBlockY(),
                            playerPos.getBlockZ()
                        )
                    )
                );
            }
        }, 20L);

        // Log death
        Date now = new Date();
        this.deathLog.set(playerId, now.getTime());
        try {
            this.deathLog.save(this.deathLogFile);
        } catch (IOException exception) {
            this.serverLog.log(Level.SEVERE, "Could not log death in " + deathLogFile, exception);
        }
    }
}
