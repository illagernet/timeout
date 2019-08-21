package net.illager.plugin.timeout;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.event.Event;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class App extends JavaPlugin {

    @Override
    public void onEnable() {
        File deathLogFile = new File(this.getDataFolder(), "deaths.yml");
        FileConfiguration deathLog = YamlConfiguration.loadConfiguration(deathLogFile);
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new DeathListener(deathLogFile, deathLog, this.getLogger()), this);
        pluginManager.registerEvents(new LoginListener(deathLog), this);
    }
    
    @Override
    public void onDisable() {
    }
    
    public static final long TIMEOUT = 72 * 3600 * 1000;
    
    public static String kickMessage(long timeout) {
        String remaining = "";
        long days = timeout / 24 * 3600 * 1000;
        long hours = timeout % days / 3600 * 1000;
        long minutes = timeout % days % hours / 60 * 1000;
        long seconds = timeout % days % hours % minutes / 1000;
        
        if(days > 0) {
            remaining = days + (days > 1 ? " days": " day") + hours + (hours > 1 ? " hours": "hour");
        }
        
        else if(hours > 0) {
            remaining = hours + (hours > 1 ? " hours": "hour") + minutes + (minutes > 1 ? " minutes": " minute");
        }
        
        else if(minutes > 0) {
            remaining = minutes + (minutes > 1 ? " minutes": " minute") + seconds + (seconds > 1 ? " seconds": "second");
        }
        
        else {
            remaining = seconds + (seconds > 1 ? " seconds": "second");
        }
        
        return remaining;
    }
}
