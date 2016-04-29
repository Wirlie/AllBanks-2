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

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_9_R1.CraftServer;
import org.bukkit.craftbukkit.v1_9_R1.chunkio.ChunkIOExecutor;
import org.bukkit.craftbukkit.v1_9_R1.util.LongHash;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.scheduler.BukkitRunnable;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.Chunk;
import net.minecraft.server.v1_9_R1.ChunkProviderServer;
import net.minecraft.server.v1_9_R1.ChunkRegionLoader;
import net.minecraft.server.v1_9_R1.IDataManager;
import net.minecraft.server.v1_9_R1.MinecraftServer;
import net.minecraft.server.v1_9_R1.ServerNBTManager;
import net.minecraft.server.v1_9_R1.WorldData;
import net.minecraft.server.v1_9_R1.WorldServer;
import net.minecraft.server.v1_9_R1.WorldSettings;

/**
 * @author Wirlie
 *
 */
public class WorldLoadAsync {

	private static ChunkGenerator generator;
	private static Chunk waitForChunk;
	private static int waitForChunkSeconds = 0;
	private static boolean generationBusy = false;
	public static String lastWorldGenerated = "";
	
	public static boolean isBusy(){
		return generationBusy;
	}

	public static World createAsyncWorld(final WorldCreator creator){
		return createAsyncWorld(creator, null);
	}
	
	public static World createAsyncWorld(WorldCreator creator, final CommandSender sender){

		final String creatorName = creator.name().toLowerCase();
		
		while(generationBusy){
			throw new IllegalStateException("Another job in progress...");
		}
		
		generationBusy = true;
		lastWorldGenerated = creatorName;
		
		//Nombre del mundo, el objeto creator contiene el nombre del mundo deseado.
		final String name = creator.name();
		//Obtener el generador del objeto creator.
		generator = creator.generator();
		//Obtener la carpeta del mundo
		File folder = new File(getWorldContainer(), name);
		//Obtener el mundo de bukkit deseado (para saber si está cargado)
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
		net.minecraft.server.v1_9_R1.WorldType type = net.minecraft.server.v1_9_R1.WorldType.getType(creator.type().getName());
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
		
		//Dimensión del mundo
		int dimension = getServer().worlds.size() + 1;
		final WorldServer internal = (WorldServer) new WorldServer(getServer(), (IDataManager) sdm, worlddata, dimension, getServer().methodProfiler, creator.environment(), generator).b();
		
		final ChunkProviderServer cps = internal.getChunkProviderServer();
		
		if ((folder.exists()) && (!folder.isDirectory())) {
			throw new IllegalArgumentException("File exists with the name '" + name + "' and isn't a folder");
		}
		
		if (generator == null) {
			throw new IllegalArgumentException("creator not have a valid generator");
		}
		
		new BukkitRunnable(){

			public void run() {
				ChunkRegionLoader loader = null;
			    try{
			    Field f = ChunkProviderServer.class.getDeclaredField("chunkLoader");
			    f.setAccessible(true);
			    if ((f.get(cps) instanceof ChunkRegionLoader)) {
			      loader = (ChunkRegionLoader)f.get(cps);
			    }
			    }catch(Exception e){
			    	e.printStackTrace();
			    }
			    
			    System.out.print("[AllBanks] ASYNC: Preparing start region for level " + (getServer().worlds.size()) + " (Seed: " + internal.getSeed() + ")");

			    if(internal.keepSpawnInMemory){
			    	
			    	short short1 = 196;
			    	long i = System.currentTimeMillis();
			    	boolean progressShow = false;
			    	
			    	for (int j = -short1; j <= short1; j += 16) {
						for (int k = -short1; k <= short1; k += 16) {
							
							long l = System.currentTimeMillis();

							if (l < i) {
								i = l;
							}

							if (l > i + 1000L) {
								int i1 = (short1 * 2 + 1)
										* (short1 * 2 + 1);
								int j1 = (j + short1)
										* (short1 * 2 + 1)
										+ k + 1;
								progressShow = true;
								System.out.println("[AllBanks] Preparing spawn area for " + name + ", " + j1 * 100 / i1 + "%");
								if(sender != null) Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_GENERATE_WORLD_GENERATING_PROGRESS, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + j1 * 100 / i1 ), true);
								
								i = l;
							}
							
							BlockPosition chunkcoordinates = internal.getSpawn();
							
							final ChunkRegionLoader loader_final = loader;
							final int j2 = chunkcoordinates.getX() + j >> 4;
							final int k2 = chunkcoordinates.getZ() + k >> 4;
							

					    	long waitTimeInitial = System.currentTimeMillis();
					    	
							if(loader.chunkExists(cps.world, j2, k2)){
								new BukkitRunnable(){
							    	public void run(){
							    		waitForChunk = ChunkIOExecutor.syncChunkLoad(cps.world, loader_final, internal.getChunkProviderServer(), j2, k2);
							    	}
							    }.runTask(AllBanks.getInstance());
							    
							    while(waitForChunk == null){
							    	
							    	if(waitForChunkSeconds > 10){
							    		throw new IllegalStateException("Generation stopped! TimeOut (10 seconds)");
							    	}
							    	
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

							    final Chunk chunk_final = waitForChunk;
							    internal.getChunkProviderServer().chunks.put(LongHash.toLong(j2, k2), chunk_final);
							    
							}else{
								cps.unloadQueue.remove(j2, k2);
							    Chunk chunk = (Chunk)cps.chunks.get(LongHash.toLong(j2, k2));
							    boolean newChunk = false;
							    
							    if (chunk == null) {
							    	chunk = cps.loadChunk(j2, k2);
							    	if (chunk == null) {
							    		chunk = cps.getOrCreateChunkFast(j2, k2);
							    	}
							    	
							    	newChunk = true;
							    }
							    
							    final boolean newChunk_final = newChunk;
							    final Chunk chunk_final = chunk;
							    
							    new BukkitRunnable(){public void run(){
							    	Server server = internal.getChunkProviderServer().world.getServer();
									    if (server != null) {
									      server.getPluginManager().callEvent(new ChunkLoadEvent(chunk_final.bukkitChunk, newChunk_final));
									    }
							    }}.runTask(AllBanks.getInstance());
							    
							    internal.getChunkProviderServer().chunks.put(LongHash.toLong(j2, k2), chunk_final);
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

				getServer().worlds.add(internal);
				
				new BukkitRunnable(){

					public void run() {
						World w = Bukkit.getWorld(name);
						
						if(w != null){
							System.out.println("[AllBanks] Done, world loaded.");
						}else{
							System.out.println("[AllBanks] World not loaded??? (null)");
						}
						
						generationBusy = false;
					}
					
				}.runTask(AllBanks.getInstance());
				
				if(sender != null) Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_GENERATE_WORLD_GENERATING_FINISH, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + name), true);
			}
			
		}.runTaskAsynchronously(AllBanks.getInstance());
		
		return internal.getWorld();
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
