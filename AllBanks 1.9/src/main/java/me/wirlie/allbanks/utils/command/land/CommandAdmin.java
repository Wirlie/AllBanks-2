package me.wirlie.allbanks.utils.command.land;

import org.bukkit.command.CommandSender;

import me.wirlie.allbanks.AllBanksLandManager;
import me.wirlie.allbanks.land.AllBanksLandWorld;
import me.wirlie.allbanks.utils.command.Command;

public class CommandAdmin extends Command {

	@Override
	public boolean execute(CommandSender sender, String[] args){
		
		boolean displayHelp = false;
		
		if(args.length <= 1){
			//	/abland plot
			displayHelp = true;
		}else if(args[1].equalsIgnoreCase("?") || args[1].equalsIgnoreCase("help")){
			displayHelp = true;
		}
		
		if(displayHelp){
			
			return true;
		}
		
		if(args[1].equalsIgnoreCase("generate")){
			if(args.length > 2){
				if(args[2].equalsIgnoreCase("world")){
					if(args.length > 3){
						String worldName = args[3];
						AllBanksLandWorld world = AllBanksLandManager.getWorld(worldName);
					}
				}
			}
		}
		
		return true;
	}
}
