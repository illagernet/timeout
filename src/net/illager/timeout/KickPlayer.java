package net.illager.timeout;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.text.StrSubstitutor;
import org.bukkit.entity.Player;

import net.illager.timeout.deathlog.DeathEntry;

public class KickPlayer implements Runnable {
    Timeout plugin;
    Player player;
    DeathEntry entry;
    
    /**
     * Constructor
     * @param plugin The Timeout plugin instance
     * @param event The player death event relevant to this runnable player kick
     */
    public KickPlayer(Timeout plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.entry = this.plugin.getDeathLog().getEntry(player.getUniqueId().toString());
    }
    
    /**
     * Run hook called by a scheduler
     */
    public void run() {
        String kickMessage = this.plugin.getConfig().getString("kick-message");
        Map<String, String> subs = new HashMap<>();
        subs.put("deathMessage", this.entry.getDeathMessage());
        subs.put("x", Integer.toString(this.entry.getPosition().getBlockX()));
        subs.put("y", Integer.toString(this.entry.getPosition().getBlockY()));
        subs.put("z", Integer.toString(this.entry.getPosition().getBlockZ()));
        subs.put("world", this.plugin.getConfig().getString("worlds." + this.entry.getWorld(), this.plugin.getConfig().getString("unknown-world")));
        subs.put("timeout", KickPlayer.formatTimeout(this.plugin.getConfig().getLong("timeout") - this.entry.getEllapsed()));
        StrSubstitutor sub = new StrSubstitutor(subs);
        this.player.kickPlayer(sub.replace(kickMessage));
    }
    
    /**
     * Generate a formatted timeout display string
     * @param timeout Time in milliseconds
     * @return A formatted timeout display string
     */
    public static String formatTimeout(long timeout) {
        long days = timeout / 86400000;
        long hours = timeout % 86400000 / 3600000;
        long minutes = timeout % 3600000 / 60000;
        long seconds = timeout % 60000 / 1000;
        if(days > 0) {
            return String.format("%d %s %d %s", days, (days > 1 ? "days": "day"), hours, (hours > 1 ? "hours": "hour"));
        } else if(hours > 0) {
            return String.format("%d %s %d %s", hours, (hours > 1 ? "hours": "hour"), minutes, (minutes > 1 ? "minutes": "minute"));
        } else if(minutes > 0) {
            return String.format("%d %s %d %s", minutes, (minutes > 1 ? "minutes": "minute"), seconds, (seconds > 1 ? "seconds": "second"));
        } else {
            return String.format("%d %s", seconds, (seconds > 1 ? "seconds": "second"));
        }
    }
}
