package net.illager.timeout;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import java.lang.Runnable;
import java.util.Date;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.util.Vector;
import org.bukkit.World;
import java.util.logging.Level;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.Command;

public class Timeout extends JavaPlugin implements Listener, CommandExecutor {
    public static final long SEVENTY_TWO_HOURS = 259200000;
    File file;
    FileConfiguration cache;

    @Override
    public void onEnable() {
        this.file = new File(this.getDataFolder(), "deaths.yml");
        this.loadDeaths();
        this.getCommand("revive").setExecutor(this);
        this.getServer().getPluginManager().registerEvents(this, this);
    }
    
    @Override
    public void onDisable() {
        this.saveDeaths();
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location location = player.getLocation();
        String message = event.getDeathMessage();
        
        this.addDeath(
            Timeout.getId(player),
            location,
            message
        );
        this.saveDeaths();
        this.getServer().getScheduler().runTaskLater(
            this,
            new KickPlayer(
                player,
                Timeout.kickMessage(
                    Timeout.SEVENTY_TWO_HOURS,
                    location,
                    message
                )
            ),
            20L
        );
    }
    
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        String id = Timeout.getId(player);
        
        if(this.isDead(id)) {
            if(this.getTimeout(id) <= 0) {
                this.removeDeath(id);
                this.saveDeaths();
            } else {
                event.disallow(
                    PlayerLoginEvent.Result.KICK_BANNED,
                    Timeout.kickMessage(
                        this.getTimeout(id),
                        this.getDeathLocation(id),
                        this.getDeathMessage(id)
                    )
                );
            }
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("revive")) {
    		if(sender.hasPermission("timeout.revive") || sender instanceof ConsoleCommandSender) {
    		    if(args.length == 0) {
    		        sender.sendMessage("You must specify at least one player to revive");
    		    } else {
                    for(String username : args) {
                        OfflinePlayer player = this.getServer().getOfflinePlayer(username);
                        String id = Timeout.getId(player);
                        
                        if(this.isDead(id)) {
                            this.removeDeath(id);
                            this.saveDeaths();
                            sender.sendMessage(username + " has been revived");
                        } else {
                            sender.sendMessage(username + " is not deathbanned!");
                        }
                    }
    		    }
    		    return true;
    		} else {
        		return false;
    		}
    	} else {
    	    return false;
    	}
    }

    
    private class KickPlayer implements Runnable {
        Player player;
        String message;
        
        public KickPlayer(Player player, String message) {
            this.player = player;
            this.message = message;
        }
        
        public void run() {
            this.player.kickPlayer(this.message);
        }
    }
    
    public void addDeath(String id, Location location, String message) {
        this.cache.set(
            id + ".time",
            new Date().getTime()
        );
        this.cache.set(
            id + ".pos",
            location.toVector()
        );
        this.cache.set(
            id + ".world",
            location.getWorld().getName()
        );
        this.cache.set(
            id + ".message",
            message
        );
        this.cache.set(
            id + ".discount",
            0L
        );
    }
    
    public void removeDeath(String id) {
        this.cache.set(id, null);
    }
    
    public void discountDeath(String id, long time) {
        this.cache.set(
            id + ".discount",
            this.getDiscount(id) + time
        );
        this.saveDeaths();
    }
    
    public void loadDeaths() {
        this.cache = YamlConfiguration.loadConfiguration(this.file);
    }
    
    public void saveDeaths() {
        try {
            this.cache.save(this.file);
        } catch(IOException exception) {
            this.getLogger().log(
                Level.SEVERE,
                exception.toString()
            );
        }
    }
    
    public boolean isDead(String id) {
        return this.cache.contains(id);
    }
    
    public long getTimeout(String id) {
        return this.getDeathTime(id)
            + Timeout.SEVENTY_TWO_HOURS
            - this.getDiscount(id)
            - new Date().getTime();
    }
    
    public long getDeathTime(String id) {
        return this.cache.getLong(id + ".time", 0);
    }
    
    public long getDiscount(String id) {
        return this.cache.getLong(id + ".discount", 0);
    }
    
    public Location getDeathLocation(String id) {
        return new Location(
            this.getServer().getWorld(
                this.cache.getString(
                    id + ".world",
                    "world"
                )
            ),
            (double) this.cache.getVector(id + ".pos", new Vector()).getBlockX(),
            (double) this.cache.getVector(id + ".pos", new Vector()).getBlockY(),
            (double) this.cache.getVector(id + ".pos", new Vector()).getBlockZ()
        );
    }
    
    public String getDeathMessage(String id) {
        return this.cache.getString(id + ".message", "Player was killed");
    }

    public static String getDimension(World world) {
        switch(world.getEnvironment()) {
            case NORMAL:
                return "the Overworld";
            case NETHER:
                return "the Nether";
            case THE_END:
                return "the End";
            default:
                return "an unknown dimension";
        }
    }
    
    public static String getId(Player player) {
        return player.getUniqueId().toString();
    }
    
    public static String getId(OfflinePlayer player) {
        return player.getUniqueId().toString();
    }
    
    public static String kickMessage(long timeout, Location location, String message) {
        return ChatColor.YELLOW
            + message
            + ChatColor.RESET
            + "\n"
            + String.format(
                "at (%d, %d, %d) in %s",
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ(),
                Timeout.getDimension(location.getWorld())
            )
            + "\nRespawning in "
            + Timeout.formatTimeout(timeout)
            + "\n\nJoin our Discord at "
            + ChatColor.BLUE
            + "illager.net/discord"
            + ChatColor.RESET;
    }
    
    public static String formatTimeout(long timeout) {
        long days = timeout / 86400000;
        long hours = timeout % 86400000 / 3600000;
        long minutes = timeout % 3600000 / 60000;
        long seconds = timeout % 60000 / 1000;

        if(days > 0) {
            return String.format(
                "%d %s %d %s",
                days,
                (days > 1 ? "days": "day"),
                hours,
                (hours > 1 ? "hours": "hour")
            );
        } else if(hours > 0) {
            return String.format(
                "%d %s %d %s",
                hours,
                (hours > 1 ? "hours": "hour"),
                minutes,
                (minutes > 1 ? "minutes": "minute")
            );
        } else if(minutes > 0) {
            return String.format(
                "%d %s %d %s",
                minutes,
                (minutes > 1 ? "minutes": "minute"),
                seconds,
                (seconds > 1 ? "seconds": "second")
            );
        } else {
            return String.format(
                "%d %s",
                seconds,
                (seconds > 1 ? "seconds": "second")
            );
        }
    }
}
