package me.wirlie.allbanks.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import me.wirlie.allbanks.command.Command.CommandExecuteResult;

/**
 * Ejecutor de comandos de AllBanks.
 * @author Wirlie
 */
public class AllBanksExecutor {
	
	private List<Command> registeredCommands = new ArrayList<Command>();
		
	/**
	 * Registar un nuevo comando.
	 * @param command Comando a registrar.
	 * @param arguments Argumentos para ejecutar este comando.
	 */
	public void registerCommand(Command command, String... arguments){
		List<String> argumentsRepresentation = new ArrayList<String>();
		
		for(String s : arguments){
			argumentsRepresentation.add(s);
		}
		
		command.setArguments(arguments);
		registeredCommands.add(command);
	}
	
	/**
	 * Comprobar si un comando es semejante a los argumentos especificados.
	 * @param args argumentos
	 * @return {@code true} si es semejante.
	 */
	public boolean checkCommandMatch(String[] args) {
		for(Command command : registeredCommands){
			if(command.matchArguments(args)){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Ejecutar comando.
	 * @param sender El que ejecuta el comando
	 * @param label Etiqueta del comando
	 * @param args Argumentos
	 * @return {@link CommandExecuteResult Resultado de la ejecución del comando}
	 */
	public CommandExecuteResult executeCommand(CommandSender sender, String label, String[] args) {
		for(Command command : registeredCommands){
			if(command.matchArguments(args)){
				return command.execute(sender, label, args);
			}
		}
		
		return CommandExecuteResult.DEFAULT;
	}
	
	/**
	 * Obtener todos los posibles comandos según los argumentos especificados.
	 * @param args Argumentos.
	 * @return Listado con todos los comandos posibles.
	 */
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
