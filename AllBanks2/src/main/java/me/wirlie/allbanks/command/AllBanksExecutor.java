package me.wirlie.allbanks.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import me.wirlie.allbanks.command.Command.CommandExecuteResult;

public class AllBanksExecutor {
	
	private List<Command> registeredCommands = new ArrayList<Command>();
		
	public void registerCommand(Command command, String... arguments){
		List<String> argumentsRepresentation = new ArrayList<String>();
		
		for(String s : arguments){
			argumentsRepresentation.add(s);
		}
		
		command.setArguments(arguments);
		registeredCommands.add(command);
	}
	
	public boolean checkCommandMatch(String[] args) {
		for(Command command : registeredCommands){
			if(command.matchArguments(args)){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @param args
	 */
	public CommandExecuteResult executeCommand(CommandSender sender, String label, String[] args) {
		for(Command command : registeredCommands){
			if(command.matchArguments(args)){
				return command.execute(sender, label, args);
			}
		}
		
		return CommandExecuteResult.DEFAULT;
	}
	
	public List<Command> possibleMatches(String[] args){
		List<Command> returnPossibleMatches = new ArrayList<Command>();
		
		for(Command command : registeredCommands){
			if(command.possibleMatch(args)){
				returnPossibleMatches.add(command);
			}
		}
		
		return returnPossibleMatches;
		
	}
}
