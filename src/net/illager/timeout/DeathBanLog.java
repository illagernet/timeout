package net.illager.timeout;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;

/**
 * A yaml log file to store deathbans
 */
public class DeathBanLog {

    private JavaPlugin plugin;
    private File file;
    private FileConfiguration cache;

    /**
     * Constructor
     * @param plugin The parent {@link org.bukkit.plugin.java.JavaPlugin} instance
     * @param path The path to yaml log file within the plugin's data folder
     */
    public DeathBanLog(JavaPlugin plugin, String path) {
        this.plugin = plugin;
        this.file = new File(this.plugin.getDataFolder(), path);
        this.load();
    }

    /**
     * Gets the unique identifier of an {@link org.bukkit.OfflinePlayer}
     * @param player A {@link org.bukkit.OfflinePlayer}
     * @return A unique identifier string
     */
    // public static String playerId(OfflinePlayer player) {
    //     return player.getUniqueId().toString();
    // }

    /**
     * Load the log file into memory
     */
    public void load() {
        try {
            this.cache = YamlConfiguration.loadConfiguration(this.file);
        } catch(IllegalArgumentException exception) {
            this.plugin.getLogger().log(Level.SEVERE, exception.getMessage());
        }
    }

    /**
     * Save the in-memory log to file
     */
    public void save() {
        try {
            this.cache.save(this.file);
        } catch(IOException exception) {
            this.plugin.getLogger().log(Level.SEVERE, exception.getMessage());
        }
    }
    
    /**
     * Add a log entry
     * @param uuid The UUID of the player
     * @param entry The DeathBan entry
     */
    public void add(UUID uuid, DeathBan entry) {
        this.cache.set(uuid.toString(), entry);
    }

    /**
     * Remove a log entry
     * @param uuid The UUID of the player
     */
    public void remove(UUID uuid) {
        this.cache.set(uuid.toString(), null);
    }

    /**
     * Checks if a log entry exists
     * @param uuid The UUID of the player
     * @return Whether or not the entry exists
     */
    public boolean has(UUID uuid) {
        return this.cache.contains(uuid.toString());
    }
    
    /**
     * Get a log entry
     * @param id The UUID of the player
     * @return A deserialized DeathBan instance
     */
    public DeathBan get(UUID uuid) {
        return (DeathBan) this.cache.get(uuid.toString());
    }
}
