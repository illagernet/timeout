package net.illager.timeout;

import org.bukkit.plugin.java.JavaPlugin;

import net.illager.timeout.deathban.Log;

/**
 * Bukkit Plugin to deathban players with a timeout
 * @author Benjamin Herman
 * @version 1.0
 */
public class Timeout extends JavaPlugin {
    
    private Log log;

    @Override
    public void onEnable() {
        this.log = new Log(this, "deathbans.yml");
        this.saveDefaultConfig();
        this.getCommand("revive").setExecutor(new ReviveCommand(this));
        this.getServer().getPluginManager().registerEvents(new PlayerDeath(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerLogin(this), this);
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
