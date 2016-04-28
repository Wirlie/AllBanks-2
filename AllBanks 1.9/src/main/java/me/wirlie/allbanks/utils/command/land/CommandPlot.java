package me.wirlie.allbanks.utils.command.land;

import org.bukkit.command.CommandSender;

import me.wirlie.allbanks.utils.command.Command;

public class CommandPlot extends Command {

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
		
		if(args[1].equalsIgnoreCase("claim")){
			
		}
		
		return true;
	}
}
