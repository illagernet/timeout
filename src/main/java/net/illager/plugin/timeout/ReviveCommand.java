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
        if(sender.hasPermission("timeout.revive") || !(sender instanceof Player)) {
            
            // No argument supplied
            if(args.length == 0) {
                sender.sendMessage("You must specify at least one player to revive.");
                return false;
            }

            // Revive all players
            for(String username : args) {
                this.revivePlayer(username, sender);
            }

            return true;
        }

        return false;
    }

    public void revivePlayer(String username, CommandSender reviver) {
        // Intentional use of deprecated method
        String playerId = Bukkit.getOfflinePlayer(args[0]).getUniqueId().toString();
        this.deathLog.set(playerId, 0);
        try {
            this.deathLog.save(this.deathLogFile);
            reviver.sendMessage(username + " has been revived.");
        } catch (IOException exception) {
            this.serverLog.log(Level.SEVERE, "Could not zero-out death in " + deathLogFile, exception);
            reviver.sendMessage(username + " could not be revived.");
        }
    }
}