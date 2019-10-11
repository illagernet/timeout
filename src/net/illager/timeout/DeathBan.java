package net.illager.timeout;

import java.util.Date;
import java.text.DateFormat;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

/**
 * A serializable deathban
 */
@SerializableAs("DeathBan")
public class DeathBan implements ConfigurationSerializable {
    private Date time;
    private Location location;
    private String message;
    private long discount;
    
    /**
     * Constructor
     * Timeout discount defaults to 0 milliseconds
     * @param time The time of the death
     * @param location The location of the death
     * @param message The death message broadcast at the time of death
     */
    public void DeathBan(Date time, Location location, String message) {
        this(time, location, message, 0L);
    }
    
    /**
     * Constructor
     * @param time The time of the death
     * @param location The location of the death
     * @param message The death message broadcast at the time of death
     * @param discount Number of milliseconds discounted from the deathban timeout
     */
    public void DeathBan(Date time, Location location, String message, long discount) {
        this.time = time;
        this.location = location;
        this.message = message;
        this.discount = discount;
    }
    
    /**
     * Serialize object for Bukkit configuration file
     */
    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap result = new LinkedHashMap();
        result.put("time", this.time.getTime());
        result.put("location", this.location);
        result.put("message", this.message);
        result.put("discount", this.discount);
        return result;
    }
    
    /**
     * Deserialize object from Bukkit configuration file
     * @returns A DeathBan instance
     */
    public static DeathBan deserialize(Map<String, Object> args) {
        Date time;
        Location location;
        String message;
        long discount = 0L;
        
        if(args.containsKey("time"))
            time = DateFormat.parse((String)args.get("time"));
        if(args.containsKey("location"))
            location = Location.deserialize(args.get("location"));
        if(args.containsKey("message"))
            message = (String) args.get("message");
        if(args.containsKey("discount"))
            discount = (long) args.get("discount");
        
        return new DeathBan(time, location, message, discount);
    }
}
