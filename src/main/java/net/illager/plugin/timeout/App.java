package net.illager.plugin.timeout;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class App extends JavaPlugin {

    public static final long TIMEOUT = 72 * 3600 * 1000;

    @Override
    public void onEnable() {
        
        // Load death log
        File deathLogFile = new File(this.getDataFolder(), "deaths.yml");
        FileConfiguration deathLog = YamlConfiguration.loadConfiguration(deathLogFile);
        
        // Register events
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new DeathListener(this, deathLogFile, deathLog, this.getLogger()), this);
        pluginManager.registerEvents(new LoginListener(deathLog), this);
        
        // Regoster revive command
        this.getCommand("revive").setExecutor(new ReviveCommand(deathLogFile, deathLog, this.getLogger()));
    }
    
    @Override
    public void onDisable() {
    }

    // Return a kick message for a deathban
    public static String kickMessage(long timeout, String message) {
        long days = timeout / 86400000;
        long hours = timeout % 86400000 / 3600000;
        long minutes = timeout % 3600000 / 60000;
        long seconds = timeout % 60000 / 1000;

        if(days > 0) {
            return message + String.format("%d %s %d %s", days, (days > 1 ? "days": "day"), hours, (hours > 1 ? "hours": "hour"));
        }
        
        else if(hours > 0) {
            return message + String.format("%d %s %d %s", hours, (hours > 1 ? "hours": "hour"), minutes, (minutes > 1 ? "minutes": "minute"));
        }
        
        else if(minutes > 0) {
            return message + String.format("%d %s %d %s", minutes, (minutes > 1 ? "minutes": "minute"), seconds, (seconds > 1 ? "seconds": "second"));
        }
        
        else {
            return message + String.format("%d %s", seconds, (seconds > 1 ? "seconds": "second"));
        }
    }
}
