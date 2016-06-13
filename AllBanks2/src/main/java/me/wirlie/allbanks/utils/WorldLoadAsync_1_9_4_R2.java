/*
 * AllBanks - AllBanks is a plugin for CraftBukkit and Spigot.
 * Copyright (C) 2016 Josue Israel Acevedo Peña
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package me.wirlie.allbanks.utils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_9_R2.CraftServer;
import org.bukkit.craftbukkit.v1_9_R2.chunkio.ChunkIOExecutor;
import org.bukkit.craftbukkit.v1_9_R2.util.LongHash;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.scheduler.BukkitRunnable;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.allbanksland.generator.WorldGenerationCfg;
import net.minecraft.server.v1_9_R2.BlockPosition;
import net.minecraft.server.v1_9_R2.Blocks;
import net.minecraft.server.v1_9_R2.Chunk;
import net.minecraft.server.v1_9_R2.ChunkProviderServer;
import net.minecraft.server.v1_9_R2.ChunkRegionLoader;
import net.minecraft.server.v1_9_R2.EntityTracker;
import net.minecraft.server.v1_9_R2.EnumDifficulty;
import net.minecraft.server.v1_9_R2.IDataManager;
import net.minecraft.server.v1_9_R2.MinecraftServer;
import net.minecraft.server.v1_9_R2.PlayerChunk;
import net.minecraft.server.v1_9_R2.ServerNBTManager;
import net.minecraft.server.v1_9_R2.WorldData;
import net.minecraft.server.v1_9_R2.WorldManager;
import net.minecraft.server.v1_9_R2.WorldServer;
import net.minecraft.server.v1_9_R2.WorldSettings;

/**
 * @author Wirlie
 *
 */
public class WorldLoadAsync_1_9_4_R2 {

	private static ChunkGenerator generator;
	private static Chunk waitForChunk;
	private static int waitForChunkSeconds = 0;
	private static boolean generationBusy = false;
	/** Último mundo generado */
	public static String lastWorldGenerated = "";
	private static boolean waitPostProcess = false;
	
	/**
	 * Saber si el generador está ocupado.
	 * @return {@code true} si el generador está ocupado.
	 */
	public static boolean isBusy(){
		return generationBusy;
	}

	/**
	 * Crear un mundo de manera asíncrona.
	 * @param creator Datos de creación
	 * @param wcfg Configuración
	 * @return Mundo generado
	 */
	public static World createAsyncWorld(final WorldCreator creator, WorldGenerationCfg wcfg){
		return createAsyncWorld(creator, null, wcfg);
	}
	
	/**
	 * Crear un mundo de manera asíncrona.
	 * @param creator Datos de creación
	 * @param sender Ejecutor del comando
	 * @param wcfg Configuración
	 * @return Mundo generado
	 */
	public static World createAsyncWorld(WorldCreator creator, final CommandSender sender, final WorldGenerationCfg wcfg){
		
		//Nombre del mundo en minúsculas.
		final String name = creator.name().toLowerCase();
		
		//Comprobar si ya hay otro trabajo en proceso.
		while(generationBusy){
			throw new IllegalStateException("Another job in progress...");
		}
		
		//Establecer el estado en Ocupado
		generationBusy = true;
		lastWorldGenerated = name;
		
		//Generador
		generator = creator.generator();
		//Carpeta del mundo
		File folder = new File(getWorldContainer(), name);
		//Mundo desde la API de Bukkit
		World world = getCraftServer().getWorld(name);
		//Si está cargado, no procesamos la generaicón
		if (world != null) {
			if(sender != null) Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_GENERATE_WORLD_ERROR_ALREADY_LOADED, true);
			System.out.println("world " + world.getName() + " already loaded");
			generationBusy = false;
			return world;
		}
		//No sé exactamente para que sirve esto... se usa para obtener el WorldData del mundo deseado
		IDataManager sdm = (IDataManager) new ServerNBTManager(getWorldContainer(), name, true, ((CraftServer) Bukkit.getServer()).getServer().getDataConverterManager());
		//Obtener los datos del mundo
		WorldData worlddata = sdm.getWorldData();
		//Obtener el tipo de mundo de minecraft
		net.minecraft.server.v1_9_R2.WorldType type = net.minecraft.server.v1_9_R2.WorldType.getType(creator.type().getName());
		//Si los datos del mundo no pudieron ser obtenidos los cargamos
		if (worlddata == null) {
			//Generar nueva configuración para cargar nuevos datos
			WorldSettings worldSettings = new WorldSettings(
					creator.seed(), //Semilla del generador
					WorldSettings.EnumGamemode.SURVIVAL, //Modo de juego, survival por defecto
					creator.generateStructures(),  //Generar estructuras
					false, //No sé para que sirve este false
					type); //Tipo del mundo
			
			//Configuración del generador
			worldSettings.setGeneratorSettings(creator.generatorSettings());
			//Establecer la nueva información
			worlddata = new WorldData(worldSettings, name);
		}
		
		//Establecer la dimensión del mundo
		int dimension = getServer().worlds.size() + 1;
		//Obtener el mundo de minecraft
		final WorldServer minecraftWorld = (WorldServer) new WorldServer(getServer(), (IDataManager) sdm, worlddata, dimension, getServer().methodProfiler, creator.environment(), generator).b();
		//Proveedor de chunks del mundo de minecraft
		final ChunkProviderServer chunkProvider = minecraftWorld.getChunkProviderServer();
		//Obtener la carpeta del mundo y checar si es válido
		if ((folder.exists()) && (!folder.isDirectory())) {
			throw new IllegalArgumentException("File exists with the name '" + name + "' and isn't a folder");
		}
		//Comprobar que el generador no es nulo
		if (generator == null) {
			throw new IllegalArgumentException("creator not have a valid generator");
		}
		
		//Comenzar con el proceso de generación
		new BukkitRunnable(){
			
			@SuppressWarnings("unchecked")
			//Thread asincrono, no consumirá recursos del servidor principal.
			public void run() {
				//Obtener el loader
				ChunkRegionLoader chunkLoader = null;
			    try{
				    Field f = ChunkProviderServer.class.getDeclaredField("chunkLoader");
				    f.setAccessible(true);
				    
				    if ((f.get(chunkProvider) instanceof ChunkRegionLoader)) {
				      chunkLoader = (ChunkRegionLoader)f.get(chunkProvider);
				    }
			    }catch(Exception e){
			    	e.printStackTrace();
			    }
			    
			    //Preparar la variable worlds
			    try {
					Field w = CraftServer.class
							.getDeclaredField("worlds");
					w.setAccessible(true);
					if (!((Map<String, World>) w.get(getCraftServer())).containsKey(name.toLowerCase())) {
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			    
			    //Establecer cosas escenciales del mundo como el scoreboard, entidades, etc.
			    minecraftWorld.scoreboard = getCraftServer().getScoreboardManager().getMainScoreboard().getHandle();
			    minecraftWorld.tracker = new EntityTracker(minecraftWorld);
				minecraftWorld.addIWorldAccess(new WorldManager(getServer(), minecraftWorld));
				minecraftWorld.worldData.setDifficulty(EnumDifficulty.EASY);
				minecraftWorld.setSpawnFlags(true, true);
				//Añadir a los mundos de minecraft del servidor
				getServer().worlds.add(minecraftWorld);
				
				//Añadir los populators del generador.
				minecraftWorld.getWorld().getPopulators().addAll(generator.getDefaultPopulators(minecraftWorld.getWorld()));
				
				//Evento de carga
				new BukkitRunnable() {
					public void run() {
						Bukkit.getPluginManager().callEvent(new WorldInitEvent(minecraftWorld.getWorld()));
					}
				}.runTask(AllBanks.getInstance());
			    
			    System.out.print("[AllBanks] ASYNC: Preparing start region for level " + (getServer().worlds.size()) + " (Seed: " + minecraftWorld.getSeed() + ")");
			    
			    //Si es necesario almacenar el spawn en la memoria:
			    if(minecraftWorld.keepSpawnInMemory){
			    	
			    	//No tengo idea de para que se usan estos calculos, pero, generan el % de carga de un mundo
			    	short short1 = 196;
			    	long i = System.currentTimeMillis();
			    	boolean progressShow = false;
			    	
			    	for (int j = -short1; j <= short1; j += 16) {
						for (int k = -short1; k <= short1; k += 16) {
							
							long l = System.currentTimeMillis();

							if (l < i) { i = l; }

							if (l > i + 1000L) {
								int i1 = (short1 * 2 + 1) * (short1 * 2 + 1);
								int j1 = (j + short1) * (short1 * 2 + 1) + k + 1;
								progressShow = true;
								System.out.println("[AllBanks] Preparing spawn area for " + name + ", " + j1 * 100 / i1 + "%");
								if(sender != null) Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_GENERATE_WORLD_GENERATING_PROGRESS, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + j1 * 100 / i1 ), true);
								
								i = l;
							}
							
							BlockPosition chunkcoordinates = minecraftWorld.getSpawn();
							
							final ChunkRegionLoader loader_final = chunkLoader;
							final int j2 = chunkcoordinates.getX() + j >> 4;
							final int k2 = chunkcoordinates.getZ() + k >> 4;
							
					    	long waitTimeInitial = System.currentTimeMillis();
					    	
					    	//Comenzar con la carga de chunks
							if(chunkLoader.chunkExists(chunkProvider.world, j2, k2)){
								//Existe, entonces solicitamos que se cargue el chunk de manera sincronizada
								new BukkitRunnable(){
							    	public void run(){
							    		waitForChunk = ChunkIOExecutor.syncChunkLoad(chunkProvider.world, loader_final, minecraftWorld.getChunkProviderServer(), j2, k2);
							    	}
							    }.runTask(AllBanks.getInstance());
							    
							    //Esperar hasta que la petición anterior se complete (cuando no es nulo)
							    while(waitForChunk == null){
							    	//Si la petición ha tomado ya más de 30 segundos, entonces algo no anda bien.
							    	if(waitForChunkSeconds > 30){
							    		throw new IllegalStateException("Generation stopped! TimeOut (30 seconds)");
							    	}
							    	//Usado para detectar cuantos segundos han pasado desde que se inició el while
							    	long waitTime = System.currentTimeMillis();
							    	
							    	if (waitTime > waitTimeInitial + 1000L) {
							    		waitTimeInitial = waitTime;
							    		waitForChunkSeconds++;
							    	}
							    	
							    	try {
										Thread.sleep(10);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
							    }
							    
							    waitForChunkSeconds = 0;
							    
							    //Bien, el chunk ya fue cargado/generado entonces lo colocamos en el mapa de chunks
							    Chunk chunk_final = waitForChunk;
							    minecraftWorld.getChunkProviderServer().chunks.put(LongHash.toLong(j2, k2), chunk_final);
							}else{
								//Si no existe el chunk, entonces tratamos de generarlo
					    		final Chunk chunk = chunkProvider.chunkGenerator.getOrCreateChunk(j2, k2);;
					    		//Petición sincrona
					    		new BukkitRunnable(){
					    			public void run(){
					    				//Establecer para esperar a que este run() se termine de procesar
					    				waitPostProcess = true;
					    				
					    				//Obtener el playerChunk y establecer (no se para que se use)
							    		PlayerChunk playerChunk = minecraftWorld.getPlayerChunkMap().getChunk(j2, k2);
							    		if(playerChunk != null){
							    			playerChunk.chunk = chunk;
							    		}
							    		
							    		//PostProcess (craftWorld)
							    		//Colocar el chunk generado en el listado de chunks
							    		chunkProvider.chunks.put(LongHash.toLong(j2, k2), chunk);
							    		//Añadir entidades
							    		chunk.addEntities();
							    		//cargar chunks locales
							    		for (int x = -2; x < 3; x++) {
							                for (int z = -2; z < 3; z++) {
							                    if (x == 0 && z == 0) {
							                        continue;
							                    }
							                    //No sé para que se use esto, probablemente nunca se ejecute (probablemente neighbor == null siempre)
							                    Chunk neighbor = chunkProvider.getLoadedChunkAt(chunk.locX + x, chunk.locZ + z);
							                    if (neighbor != null) {
							                        neighbor.setNeighborLoaded(-x, -z);
							                        chunk.setNeighborLoaded(x, z);
							                    }
							                }
							            }
							    		
							    		//Cargar chunk local
							    		chunk.loadNearby(chunkProvider, chunkProvider.chunkGenerator);
							    		
							    		//refresh chunk (CraftWorld)
							    		int px = j2 << 4;
							            int pz = k2 << 4;
							    		int height = minecraftWorld.getHeight() / 16;
							    		
							            for (int idx = 0; idx < 64; idx++) {
							                minecraftWorld.notify(new BlockPosition(px + (idx / height), ((idx % height) * 16), pz), Blocks.AIR.getBlockData(), Blocks.STONE.getBlockData(), 3);
							            }
							            
							            minecraftWorld.notify(new BlockPosition(px + 15, (height * 16) - 1, pz + 15), Blocks.AIR.getBlockData(), Blocks.STONE.getBlockData(), 3);
							            
							            //Terminar el proceso
							            waitPostProcess = false;
					    			}
					    		}.runTask(AllBanks.getInstance());
					    		
					    		while(waitPostProcess){
					    			//Esperar a que el task anterior termine.
					    		}
					    		
					    		//Llamar al evento ChunkLoadEvent
							    new BukkitRunnable(){
							    	public void run(){
							    		Server server = minecraftWorld.getChunkProviderServer().world.getServer();
									    if (server != null) {
									      server.getPluginManager().callEvent(new ChunkLoadEvent(chunk.bukkitChunk, true));
									    }
							    	}
							    }.runTask(AllBanks.getInstance());
							}
						}
			    	}
			    	
			    	if(progressShow){
			    		System.out.println("[AllBanks] Preparing spawn area for " + name + ", 100%");
			    		System.out.println("[AllBanks] Done.");
			    	}
			    }else{
			    	System.out.println("[AllBanks] keepSpawnInMemory is false??? Skip chunk generation.");
			    }
				
				new BukkitRunnable(){

					public void run() {
						World w = Bukkit.getWorld(name);
						//establecer spawn, para corregir errores.
						if(w != null){
							System.out.println("[AllBanks] Set spawn to " + 0 + ", " + (wcfg.world_height + 1) + ", " + 0);
							w.setSpawnLocation(0, wcfg.world_height + 1, 0);
							System.out.println("[AllBanks] Done.");
						}else{
							System.out.println("[AllBanks] World not loaded??? (null)");
						}
						//Quitar el estado ocupado
						generationBusy = false;
					}
					
				}.runTask(AllBanks.getInstance());
				
				if(sender != null) Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_GENERATE_WORLD_GENERATING_FINISH, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + name), true);
			}
			
		}.runTaskAsynchronously(AllBanks.getInstance());
		
		return minecraftWorld.getWorld();
	}
	
	private static File getWorldContainer() {
		if (getServer().universe != null) {
			return getServer().universe;
		}
		try {
			Field container = CraftServer.class.getDeclaredField("container");
			container.setAccessible(true);
			Field settings = CraftServer.class.getDeclaredField("configuration");
			settings.setAccessible(true);
			File co = (File) container.get(getCraftServer());
			if (co == null) {
				container.set(
						getCraftServer(),
						new File(((YamlConfiguration) settings
								.get(getCraftServer())).getString(
								"settings.world-container", ".")));
			}

			return (File) container.get(getCraftServer());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static MinecraftServer getServer() {
		return getCraftServer().getServer();
	}
	
	private static CraftServer getCraftServer() {
		return ((CraftServer) Bukkit.getServer());
	}
	
}
