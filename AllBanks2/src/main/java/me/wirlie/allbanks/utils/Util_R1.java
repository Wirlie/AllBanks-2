package me.wirlie.allbanks.utils;

import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import net.minecraft.server.v1_9_R1.Chunk;
import net.minecraft.server.v1_9_R1.ChunkProviderServer;
import net.minecraft.server.v1_9_R1.EnumChatFormat;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import net.minecraft.server.v1_9_R1.WorldServer;

public class Util_R1 {
	
	public static String getItemCodeOrGetCustomName(net.minecraft.server.v1_9_R1.ItemStack stack) {
        NBTTagCompound tag = stack.getTag();

        if ((tag != null) && (tag.hasKeyOfType("display", 10))) {
            NBTTagCompound nbttagcompound = tag.getCompound("display");

            if (nbttagcompound.hasKeyOfType("Name", 8)) {
                return EnumChatFormat.ITALIC + nbttagcompound.getString("Name");
            }
        }

        return stack.a() + ".name";
    }
	
	public static ChatColor convertEnumChatFormatToChatColor(EnumChatFormat e) {
		
		switch(e){
		case AQUA:
			return ChatColor.AQUA;
		case BLACK:
			return ChatColor.BLACK;
		case BLUE:
			return ChatColor.BLUE;
		case BOLD:
			return ChatColor.BOLD;
		case DARK_AQUA:
			return ChatColor.DARK_AQUA;
		case DARK_BLUE:
			return ChatColor.DARK_BLUE;
		case DARK_GRAY:
			return ChatColor.DARK_GRAY;
		case DARK_GREEN:
			return ChatColor.DARK_GREEN;
		case DARK_PURPLE:
			return ChatColor.DARK_PURPLE;
		case DARK_RED:
			return ChatColor.DARK_RED;
		case GOLD:
			return ChatColor.GOLD;
		case GRAY:
			return ChatColor.GRAY;
		case GREEN:
			return ChatColor.GREEN;
		case ITALIC:
			return ChatColor.ITALIC;
		case LIGHT_PURPLE:
			return ChatColor.LIGHT_PURPLE;
		case RED:
			return ChatColor.RED;
		case RESET:
			return ChatColor.RESET;
		case STRIKETHROUGH:
			return ChatColor.STRIKETHROUGH;
		case UNDERLINE:
			return ChatColor.UNDERLINE;
		case WHITE:
			return ChatColor.WHITE;
		case YELLOW:
			return ChatColor.YELLOW;
		default:
			return ChatColor.WHITE;
		}
	}
	
	public static class ChunkLocStruct{
		int x;
		int z;
		
		public ChunkLocStruct(int x, int z){
			this.x = x;
			this.z = z;
		}
	}
	
	private static Chunk originalChunkStatic;
	private static HashSet<String> lockClearPlot = new HashSet<String>();

	public static void clearPlot(final CommandSender sender, final World bukkitWorld, final Location plotFirstBound, final Location plotSecondBound) {
		
		if(sender != null){
			if(lockClearPlot.contains(sender.getName())){
				Translation.getAndSendMessage(sender, StringsID.PLOT_REGENERATION_ANOTHER_JOB_IN_PROGRESS, true);
				return;
			}else{
				Translation.getAndSendMessage(sender, StringsID.PLOT_REGENERATION_IN_PROGRESS, true);
				lockClearPlot.add(sender.getName());
			}
		}
		
		final WorldServer ws = ((CraftWorld) bukkitWorld).getHandle();
		final ChunkProviderServer provider = ws.getChunkProviderServer();
		final HashMap<String, Chunk> tempChunks = new HashMap<String, Chunk>();
		
		//Background
		new BukkitRunnable(){
			@SuppressWarnings("deprecation")
			public void run(){
				long initialTime = new Date().getTime();
				int firstCursorX = 0;
				int firstCursorZ = 0;
				int secondCursorX = 0;
				int secondCursorZ = 0;
				
				if(plotFirstBound.getBlockX() < plotSecondBound.getBlockX()){
					firstCursorX = plotFirstBound.getBlockX();
					secondCursorX = plotSecondBound.getBlockX();
				}else{
					firstCursorX = plotSecondBound.getBlockX();
					secondCursorX = plotFirstBound.getBlockX();
				}
				
				if(plotFirstBound.getBlockZ() < plotSecondBound.getBlockZ()){
					firstCursorZ = plotFirstBound.getBlockZ();
					secondCursorZ = plotSecondBound.getBlockZ();
				}else{
					firstCursorZ = plotSecondBound.getBlockZ();
					secondCursorZ = plotFirstBound.getBlockZ();
				}
				
				long totalBlocks = (secondCursorX - firstCursorX) * (secondCursorZ - firstCursorZ) * (bukkitWorld.getMaxHeight());
				long processedBlocks = 0;
				long lastReport = new Date().getTime();
				
				for(int cursorX = firstCursorX; cursorX <= secondCursorX; cursorX++){
					for(int cursorZ = firstCursorZ; cursorZ <= secondCursorZ; cursorZ++){
						y_cursor:
						for(int cursorY = 0; cursorY < bukkitWorld.getMaxHeight(); cursorY++){
							
							long currentTime = new Date().getTime();
							if((currentTime - lastReport) > 1000){
								int percent = (int) ((processedBlocks * 100) / totalBlocks);
								Translation.getAndSendMessage(sender, StringsID.PLOT_REGENERATION_REGENERATING, true, String.valueOf(percent), String.valueOf(processedBlocks), String.valueOf(totalBlocks));
								lastReport = currentTime;
							}
							
							Location loc = new Location(bukkitWorld, cursorX, cursorY, cursorZ);
							final int chunk_x = loc.getChunk().getX();
							final int chunk_z = loc.getChunk().getZ();
							originalChunkStatic = null;
							
							if(tempChunks.containsKey(chunk_x + ":" + chunk_z)){
								//Ok, obtener chunk almacenado
								originalChunkStatic = tempChunks.get(chunk_x + ":" + chunk_z);
							}else{
								BukkitTask task = new BukkitRunnable(){
									public void run(){
										originalChunkStatic = provider.chunkGenerator.getOrCreateChunk(chunk_x, chunk_z);
										tempChunks.put(chunk_x + ":" + chunk_z, originalChunkStatic);
									}
								}.runTask(AllBanks.getInstance());
								
								long lastWhileTime = new Date().getTime();
								
								while(originalChunkStatic == null){
									//esperar
									long currentWhileTime = new Date().getTime();
									if((currentWhileTime - lastWhileTime) > 10000){
										//Más de 10 segundos
										AllBanksLogger.severe("PlotRegeneration timeout (10 seconds).");
										task.cancel();
										continue y_cursor;
									}
								}
							}
							
							try{
								Block originalBlock = originalChunkStatic.bukkitChunk.getBlock(cursorX - (chunk_x << 4), cursorY, cursorZ - (chunk_z << 4));
								Block currentBlock = loc.getBlock();
								currentBlock.setType(originalBlock.getType());
								currentBlock.setData(originalBlock.getData());
								currentBlock.setBiome(originalBlock.getBiome());
								processedBlocks++;
							}catch(ConcurrentModificationException e){
								//skip
								AllBanksLogger.severe("ConcurrentModificationException! - Plot Clear");
							}
						}
					}
				}
				
				if(sender != null){
					long finishTime = new Date().getTime();
					long seconds = finishTime - initialTime;
					if(seconds > 1000){
						seconds = seconds / 1000;
					}else{
						seconds = 0;
					}
					
					Translation.getAndSendMessage(sender, StringsID.PLOT_REGENERATION_COMPLETED, true, String.valueOf(seconds));
					lockClearPlot.remove(sender.getName());
				}
			}
		}.runTaskAsynchronously(AllBanks.getInstance());
	}
}
