package net.illager.plugin.timeout;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

public class ReviveCommand implements CommandExecutor {
    
    Plugin plugin;
    
    public ReviveCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        // Player with permissions or from console
        if(sender.hasPermission("timeout.revive") || !(sender instanceof Player)) {
            
            // No argument supplied
            if(args.length == 0) {
                sender.sendMessage("You must specify at least one player to revive.");
                return false;
            }

            // Revive all players
            for(String username : args) {
                
                String id = Bukkit.getOfflinePlayer(username).getUniqueId().toString();
                
                // Remove death
                this.plugin.removeDeath(id);
                sender.sendMessage(username + " has been revived.");
            }

            return true;
        }

        return false;
    }
}
