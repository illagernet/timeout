package net.illager.plugin.timeout;

import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;

public class Plugin extends JavaPlugin {

    public static final long SEVENTY_TWO_HOURS = 72 * 3600 * 1000;

    // Death log
    File deathFile;
    FileConfiguration deathCache;

    @Override
    public void onEnable() {
        
        // Load "deaths.yml"
        this.deathFile = new File(this.getDataFolder(), "deaths.yml");
        this.deathCache = YamlConfiguration.loadConfiguration(deathFile);
        
        // Setup Commands
        this.getCommand("revive").setExecutor(new ReviveCommand(this));
        
        // Register Events
        this.getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        this.getServer().getPluginManager().registerEvents(new LoginListener(this), this);
    }
    
    @Override
    public void onDisable() {
    }
    
    public void logDeath(String id, long time, Location location, String message) {
        this.deathCache.set(id + ".time", time);
        this.deathCache.set(id + ".pos", location.toVector());
        this.deathCache.set(id + ".world", location.getWorld().getName());
        this.deathCache.set(id + ".message", message);
        this.deathCache.set(id + ".discount", 0L);
        this.saveDeaths();
    }
    
    public void removeDeath(String id) {
        this.deathCache.set(id, null);
        this.saveDeaths();
    }
    
    public void saveDeaths() {
        try {
            this.deathCache.save(this.deathFile);
        } catch(java.io.IOException exception) {
            this.getLogger().log(Level.SEVERE, exception.toString());
        }
    }
    
    public boolean isPlayerDead(String id) {
        return this.deathCache.contains("id");
    }
    
    public long getRespawnTime(String id) {
        return this.deathCache.getLong(id + ".time") +
            Plugin.SEVENTY_TWO_HOURS -
            this.deathCache.getLong(id + ".discount");
    }
    
    public Location getDeathLocation(String id) {
        return new Location(
            this.getServer().getWorld(this.deathCache.getString(id + ".world")),
            new Double(this.deathCache.getVector(id + ".pos").getBlockX()),
            new Double(this.deathCache.getVector(id + ".pos").getBlockY()),
            new Double(this.deathCache.getVector(id + ".pos").getBlockZ())
        );
    }
    
    public String getDeathMessage(String id) {
        return this.deathCache.getString(id + ".message");
    }
    
    public static String kickMessage(long timeout, Location location, String message) {
        
        List<String> lines = new ArrayList<String>();
        
        // Death message
        lines.add(message);
        
        // Location
        lines.add(
            ChatColor.YELLOW +
            String.format(
                "at (%d, %d, %d) in %s",
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ(),
                location.getWorld().getName()
            ) +
            ChatColor.RESET
        );
        
        // Respawning Countdown
        lines.add("Respawning in " + Plugin.formatTime(timeout));
        
        // Discord invite
        lines.add(
            "Join our Discord at " +
            ChatColor.BLUE +
            "https://illager.net/discord" +
            ChatColor.RESET
        );
        
        return String.join("\n", lines);
    }
    
    public static String formatTime(long timeout) {
        long days = timeout / 86400000;
        long hours = timeout % 86400000 / 3600000;
        long minutes = timeout % 3600000 / 60000;
        long seconds = timeout % 60000 / 1000;

        if(days > 0) {
            return String.format("%d %s %d %s", days, (days > 1 ? "days": "day"), hours, (hours > 1 ? "hours": "hour"));
        }
        
        else if(hours > 0) {
            return String.format("%d %s %d %s", hours, (hours > 1 ? "hours": "hour"), minutes, (minutes > 1 ? "minutes": "minute"));
        }
        
        else if(minutes > 0) {
            return String.format("%d %s %d %s", minutes, (minutes > 1 ? "minutes": "minute"), seconds, (seconds > 1 ? "seconds": "second"));
        }
        
        else {
            return String.format("%d %s", seconds, (seconds > 1 ? "seconds": "second"));
        }
    }
}
