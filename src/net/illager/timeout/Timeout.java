package net.illager.timeout;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Spigot Plugin to deathban players until a respawn timeout has been satisfied
 * @author Illager Net
 * @version 1.0.0
 */
public class Timeout extends JavaPlugin {
    
    public DeathBanLog log;

    @Override
    public void onEnable() {
        this.log = new DeathBanLog(this, "deathbans.yml");
        this.saveDefaultConfig();
        this.getCommand("revive").setExecutor(new ReviveCommand(this));
        this.getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerLoginListener(this), this);
    }
    
    @Override
    public void onDisable() {
        // Do nothing
    }

    /**
     * Get the DeathLog log associated with this plugin instance
     * @return A {@link net.illager.timeout.deathlog.DeathLog} instance
     * @since 1.0
     */
    public DeathLog getDeathLog() {
        return this.log;
    }
}
