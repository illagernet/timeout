package net.illager.timeout;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class ReviveCommand implements CommandExecutor {
    Timeout plugin;

    public ReviveCommand(Timeout plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("revive")) {
    		if(sender.hasPermission("timeout.revive") || sender instanceof ConsoleCommandSender) {
    		    if(args.length == 0) {
    		        sender.sendMessage("You must specify at least one player to revive");
    		    } else {
                    for(String username : args) {
                        OfflinePlayer player = this.plugin.getServer().getOfflinePlayer(username);
                        String id = Timeout.getId(player);
                        
                        if(this.plugin.isDead(id)) {
                            this.plugin.removeDeath(id);
                            this.plugin.saveDeaths();
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
}