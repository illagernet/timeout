package net.illager.plugin.timeout;

import org.bukkit.command.CommandExecutor;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import java.io.IOException;
import java.util.logging.Level;

public class ReviveCommand implements CommandExecutor {
    File deathLogFile;
    FileConfiguration deathLog;
    Logger serverLog;

    public ReviveCommand(File file, FileConfiguration config, Logger log) {
        this.deathLogFile = file;
        this.deathLog = config;
        this.serverLog = log;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        // Player with permissions or console
        if(sender.hasPermission("illager.timeout.revive") || !(sender instanceof Player)) {
            
            // No argument supplied
            if(args[0] == null || args[0].length() == 0) {
                sender.sendMessage("You must specify a payer to revive.");
                return false;
            }

            // Intentional use of deprecated method
            String playerId = Bukkit.getOfflinePlayer(args[0]).getUniqueId().toString();
            this.deathLog.set(playerId, 0);
            try {
                this.deathLog.save(this.deathLogFile);
            } catch (IOException exception) {
                this.serverLog.log(Level.SEVERE, "Could not zero-out death in " + deathLogFile, exception);
            }

            sender.sendMessage(args[0] + " has been revived.");
            return true;
        }

        return false;
    }
}