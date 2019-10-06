package net.illager.timeout;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A yaml log file to store deathbans
 * @author Benjamin Herman
 * @version 1.0
 */
public class DeathBanLog {

    static {
        // Register DeathEntry as serializable object
        ConfigurationSerialization.registerClass(DeathBan.class, "DeathBan");
    }

    private JavaPlugin plugin;
    private File file;
    private FileConfiguration cache;

    /**
     * Constructor
     * @param plugin The parent {@link org.bukkit.plugin.java.JavaPlugin} instance
     * @param path The path to yaml log file within the plugin's data folder
     * @since 1.0
     */
    public DeathBanLog(JavaPlugin plugin, String path) {
        this.plugin = plugin;
        this.file = new File(this.plugin.getDataFolder(), path);
        this.load();
    }

    /**
     * Gets the unique identifier of a {@link org.bukkit.OfflinePlayer} 
     * @param player A {@link org.bukkit.OfflinePlayer}
     * @return A unique identifier string
     */
    public static String playerId(OfflinePlayer player) {
        return player.getUniqueId().toString();
    }

    /**
     * Load the log into memory
     * @since 1.0
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
     * @since 1.0
     */
    public void save() {
        try {
            this.cache.save(this.file);
        } catch(IOException exception) {
            this.plugin.getLogger().log(Level.SEVERE, exception.getMessage());
        }
    }

    /**
     * Add an entry generated from a player death event
     * @param event A Bukkit player death event
     * @since 1.0
     */
    public void add(PlayerDeathEvent event) {
        this.add(event.getEntity(), new DeathBan(event));
    }

    /**
     * Add an entry for the given player
     * @param id The entry identifier
     * @param entry A instance of {@link net.illager.timeout.deathlog.DeathEntry}
     * @since 1.0
     */
    public void add(OfflinePlayer player, DeathBan deathban) {
        this.cache.set(DeathBanLog.playerId(player), deathban);
        this.save();
    }
    
    /**
     * Remove an entry under the given id
     * @param id The entry identifier
     * @since 1.0
     */
    public void remove(OfflinePlayer player) {
        this.cache.set(DeathBanLog.playerId(player), null);
        this.save();
    }

    /**
     * Checks if the log has an entry under the given id
     * @param id The entry identifier
     * @return Whether or not the entry exists
     * @since 1.0
     */
    public boolean has(OfflinePlayer player) {
        return this.cache.contains(DeathBanLog.playerId(player));
    }

    /**
     * Gets the entry under the given id
     * @param id The entry identifier
     * @return An deserialized DeathEntry corresponding to the given id, or null if none is found  
     * @since 1.0
     */
    public DeathBan get(OfflinePlayer player) {
        return (DeathBan) this.cache.get(DeathBanLog.playerId(player));
    }

    /**
     * A serializable deathban description
     */
    private class DeathBan implements ConfigurationSerializable {
        
        private long time;
        private Location location;
        private String deathMessage;
        private long discount;

        /**
         * Constructor
         * @param event A player death event
         * @since 1.0
         */
        public DeathBan(PlayerDeathEvent event) {
            this.time = new Date().getTime();
            this.location = event.getEntity().getLocation();
            this.deathMessage = event.getDeathMessage();
            this.discount = 0L;
        }
        
        /**
         * Serializes a deathban
         * @return A serialized Map
         * @since 1.0
         */
        @Override
        public Map<String, Object> serialize() {
            Map<String, Object> map = new HashMap<>();
            map.put("time", this.time);
            map.put("world", this.location.getWorld().getName());
            map.put("pos", this.location.toVector());
            map.put("message", this.deathMessage);
            map.put("discount", this.discount);
            return map;
        }

        /**
         * Creates an instance from a serialized Map
         * @return A new {@link net.illager.timeout.DeathBanLog.DeathBan} instance
         * @since 1.0
         */
        public static DeathBan deserialize(Map<String, Object> map) {
            long time = (long) map.get("time");
            String worldName = (String) map.get("world");
            Vector pos = (Vector) map.get("pos");
            Location location = new Location(Bukkit.getWorld(worldName), pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
            String message = (String) map.get("message");
            long discount = (long) map.get("discount");
            return new DeathBan(time, location, message, discount);
        }

        /**
         * Get the name of world where the player died
         * @return The name of the world
         * @since 1.0
         */
        public String getWorld() {
            return this.location.getWorld().getName();
        }

        /**
         * Get the vector coordinates where the player died
         * @return The vector coordinates
         * @since 1.0
         */
        public Vector getPosition() {
            return this.location.toVector();
        }

        /**
         * Get the time of death in millisecond
         * @return The time of death in millisecond
         * @since 1.0
         */
        public long getTime() {
            return this.time;
        }

        /**
         * Get the death message broadcast at the time of death
         * @return The death message
         * @since 1.0
         */
        public String getDeathMessage() {
            return this.deathMessage;
        }

        /**
         * Get the deathban time in milliseconds discounted
         * @return The deathban time in milliseconds discounted
         * @since 1.0
         */
        public long getDiscount() {
            return this.discount;
        }

        /**
         * Get the time ellapsed, including discount, in milliseconds
         * @return The time ellapsed in milliseconds
         * @since 1.0
         */
        public long getEllapsed() {
            long now = new Date().getTime();
            return now - this.time + this.discount;
        }
    }
}