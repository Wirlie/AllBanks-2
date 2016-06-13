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
package me.wirlie.allbanks.listeners.allbanksland;

import org.bukkit.Location;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.allbanksland.AllBanksPlot;
import me.wirlie.allbanks.allbanksland.AllBanksWorld;

/**
 * @author Wirlie
 *
 */
@SuppressWarnings("javadoc")
public class PlotPlayerInteractListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteracEntity(PlayerInteractEntityEvent e){
		
		if(!(e.getRightClicked() instanceof ArmorStand) && !(e.getRightClicked() instanceof Minecart) && !(e.getRightClicked() instanceof ItemFrame)) return;
		
		Location loc = e.getRightClicked().getLocation();
		Player p = e.getPlayer();
		
		if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
			AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
			
			if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
				if(!abw.hasAdminPermissions(p)){
					e.setCancelled(true);
					Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
				}
			}else{
				AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
				
				if(plot.hasOwner()){
					if(!plot.havePermissions(p)){
						e.setCancelled(true);
						Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						return;
					}
				}else{
					if(!abw.hasAdminPermissions(p)){
						e.setCancelled(true);
						Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						return;
					}
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	//# ARMOR STAND
	public void playerInteractWithArmorStand(PlayerInteractAtEntityEvent e){
		
		if(!(e.getRightClicked() instanceof ArmorStand) && !(e.getRightClicked() instanceof Minecart) && !(e.getRightClicked() instanceof ItemFrame)) return;
		
		Location loc = e.getRightClicked().getLocation();
		Player p = e.getPlayer();
		
		if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
			AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
			
			if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
				if(!abw.hasAdminPermissions(p)){
					e.setCancelled(true);
					Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
				}
			}else{
				AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
				
				if(plot.hasOwner()){
					if(!plot.havePermissions(p)){
						e.setCancelled(true);
						Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						return;
					}
				}else{
					if(!abw.hasAdminPermissions(p)){
						e.setCancelled(true);
						Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						return;
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerUseBlock(PlayerInteractEvent e){
		Block b = e.getClickedBlock();
		if(b != null){
			Location loc = b.getLocation();
			
			if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				Player p = e.getPlayer();
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				
				if(b.getType().equals(Material.GOLD_PLATE) || b.getType().equals(Material.IRON_PLATE) || b.getType().equals(Material.WOOD_PLATE) || b.getType().equals(Material.STONE_PLATE)){
					if(e.getAction().equals(Action.PHYSICAL)){
						if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
							if(!abw.hasAdminPermissions(p)){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
							}
						}else{
							AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
							
							if(plot.hasOwner()){
								if(!plot.havePermissions(p) && !plot.getPlotConfiguration().usePressurePlate()){
									e.setCancelled(true);
									Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
									return;
								}
							}else{
								if(!abw.hasAdminPermissions(p)){
									e.setCancelled(true);
									Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
									return;
								}
							}
						}
					}
				}
				
				if(b.getType().equals(Material.TRIPWIRE_HOOK) || b.getType().equals(Material.STRING)){
					if(e.getAction().equals(Action.PHYSICAL)){
						if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
							if(!abw.hasAdminPermissions(p)){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
							}
						}else{
							AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
							
							if(plot.hasOwner()){
								if(!plot.havePermissions(p) && !plot.getPlotConfiguration().usePressurePlate()){
									e.setCancelled(true);
									Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
									return;
								}
							}else{
								if(!abw.hasAdminPermissions(p)){
									e.setCancelled(true);
									Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
									return;
								}
							}
						}
					}
				}
				
				if(b.getType().equals(Material.CHEST) || 
						b.getType().equals(Material.HOPPER) || 
						b.getType().equals(Material.ENDER_CHEST) || 
						b.getType().equals(Material.DISPENSER) || 
						b.getType().equals(Material.BEACON) || 
						b.getType().equals(Material.ARMOR_STAND) || 
						b.getType().equals(Material.FURNACE) || 
						b.getType().equals(Material.JUKEBOX) || 
						b.getType().equals(Material.DROPPER) ||
						b.getType().equals(Material.NOTE_BLOCK) ||
						b.getType().equals(Material.REDSTONE_COMPARATOR_OFF) ||
						b.getType().equals(Material.REDSTONE_COMPARATOR_ON) ||
						b.getType().equals(Material.DIODE_BLOCK_OFF) ||
						b.getType().equals(Material.DIODE_BLOCK_ON) ||
						b.getType().equals(Material.TRAPPED_CHEST))
				{
					if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
						if(!abw.hasAdminPermissions(p)){
							e.setCancelled(true);
							Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						}
					}else{
						AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
						
						if(plot.hasOwner()){
							if(!plot.havePermissions(p)){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								return;
							}
						}else{
							if(!abw.hasAdminPermissions(p)){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								return;
							}
						}
					}
				}
				
				if(b.getType().equals(Material.LEVER)){
					if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
						if(!abw.hasAdminPermissions(p)){
							e.setCancelled(true);
							Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						}
					}else{
						AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
						
						if(plot.hasOwner()){
							if(!plot.havePermissions(p) && !plot.getPlotConfiguration().useLever()){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								return;
							}
						}else{
							if(!abw.hasAdminPermissions(p)){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								return;
							}
						}
					}
				}
				
				if(b.getType().equals(Material.ACACIA_DOOR) || 
						b.getType().equals(Material.BIRCH_DOOR) ||
						b.getType().equals(Material.DARK_OAK_DOOR) ||
						b.getType().equals(Material.IRON_DOOR) ||
						b.getType().equals(Material.JUNGLE_DOOR) ||
						b.getType().equals(Material.SPRUCE_DOOR) ||
						b.getType().equals(Material.TRAP_DOOR) ||
						b.getType().equals(Material.WOOD_DOOR) ||
						b.getType().equals(Material.WOODEN_DOOR))
				{
					//Puerta
					if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
						if(!abw.hasAdminPermissions(p)){
							e.setCancelled(true);
							Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						}
					}else{
						AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
						
						if(plot.hasOwner()){
							if(!plot.havePermissions(p) && !plot.getPlotConfiguration().useDoor()){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								return;
							}
						}else{
							if(!abw.hasAdminPermissions(p)){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								return;
							}
						}
					}
				}
				
				if(b.getType().equals(Material.FENCE_GATE) ||
						b.getType().equals(Material.ACACIA_FENCE_GATE) ||
						b.getType().equals(Material.BIRCH_FENCE_GATE) ||
						b.getType().equals(Material.DARK_OAK_FENCE_GATE) ||
						b.getType().equals(Material.JUNGLE_FENCE_GATE) ||
						b.getType().equals(Material.SPRUCE_FENCE_GATE))
				{
					if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
						if(!abw.hasAdminPermissions(p)){
							e.setCancelled(true);
							Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						}
					}else{
						AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
						
						if(plot.hasOwner()){
							if(!plot.havePermissions(p) && !plot.getPlotConfiguration().useFenceGate()){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								return;
							}
						}else{
							if(!abw.hasAdminPermissions(p)){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								return;
							}
						}
					}
				}
				
				if(b.getType().equals(Material.ENCHANTMENT_TABLE)){
					if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
						if(!abw.hasAdminPermissions(p)){
							e.setCancelled(true);
							Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						}
					}else{
						AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
						
						if(plot.hasOwner()){
							if(!plot.havePermissions(p) && !plot.getPlotConfiguration().useEnchantmentTable()){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								return;
							}
						}else{
							if(!abw.hasAdminPermissions(p)){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								return;
							}
						}
					}
				}
				
				if(b.getType().equals(Material.WORKBENCH)){
					if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
						if(!abw.hasAdminPermissions(p)){
							e.setCancelled(true);
							Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						}
					}else{
						AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
						
						if(plot.hasOwner()){
							if(!plot.havePermissions(p) && !plot.getPlotConfiguration().useWorkbench()){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								return;
							}
						}else{
							if(!abw.hasAdminPermissions(p)){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								return;
							}
						}
					}
				}
				
				if(b.getType().equals(Material.ANVIL)){
					if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
						if(!abw.hasAdminPermissions(p)){
							e.setCancelled(true);
							Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						}
					}else{
						AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
						
						if(plot.hasOwner()){
							if(!plot.havePermissions(p) && !plot.getPlotConfiguration().useAnvil()){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								return;
							}
						}else{
							if(!abw.hasAdminPermissions(p)){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								return;
							}
						}
					}
				}
				
				if(b.getType().equals(Material.CAKE_BLOCK)){
					if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
						if(!abw.hasAdminPermissions(p)){
							e.setCancelled(true);
							Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						}
					}else{
						AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
						
						if(plot.hasOwner()){
							if(!plot.havePermissions(p)){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								return;
							}
						}else{
							if(!abw.hasAdminPermissions(p)){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								return;
							}
						}
					}
				}
				
				if(b.getType().equals(Material.STONE_BUTTON) || b.getType().equals(Material.WOOD_BUTTON)){
					if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
						if(!abw.hasAdminPermissions(p)){
							e.setCancelled(true);
							Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						}
					}else{
						AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
						
						if(plot.hasOwner()){
							if(!plot.havePermissions(p) && !plot.getPlotConfiguration().useButton()){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								return;
							}
						}else{
							if(!abw.hasAdminPermissions(p)){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								return;
							}
						}
					}
				}
				
				if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
					if(b.getType().equals(Material.RAILS) ||
							b.getType().equals(Material.ACTIVATOR_RAIL) ||
							b.getType().equals(Material.DETECTOR_RAIL) ||
							b.getType().equals(Material.POWERED_RAIL))
					{
						if(p.getInventory().getItemInMainHand().getType().equals(Material.MINECART) ||
								p.getInventory().getItemInMainHand().getType().equals(Material.COMMAND_MINECART) ||
								p.getInventory().getItemInMainHand().getType().equals(Material.EXPLOSIVE_MINECART) ||
								p.getInventory().getItemInMainHand().getType().equals(Material.HOPPER_MINECART) ||
								p.getInventory().getItemInMainHand().getType().equals(Material.POWERED_MINECART) ||
								p.getInventory().getItemInMainHand().getType().equals(Material.STORAGE_MINECART))
						{
							if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
								if(!abw.hasAdminPermissions(p)){
									e.setCancelled(true);
									Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								}
							}else{
								AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
								
								if(plot.hasOwner()){
									if(!plot.havePermissions(p)){
										e.setCancelled(true);
										Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
										return;
									}
								}else{
									if(!abw.hasAdminPermissions(p)){
										e.setCancelled(true);
										Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
										return;
									}
								}
							}
						}
					}else{
						if(p.getInventory().getItemInMainHand().getType().equals(Material.BOAT) ||
								p.getInventory().getItemInMainHand().getType().equals(Material.BOAT_ACACIA) ||
								p.getInventory().getItemInMainHand().getType().equals(Material.BOAT_BIRCH) ||
								p.getInventory().getItemInMainHand().getType().equals(Material.BOAT_DARK_OAK) ||
								p.getInventory().getItemInMainHand().getType().equals(Material.BOAT_JUNGLE) ||
								p.getInventory().getItemInMainHand().getType().equals(Material.BOAT_SPRUCE) ||
								p.getInventory().getItemInMainHand().getType().equals(Material.ITEM_FRAME) ||
								p.getInventory().getItemInMainHand().getType().equals(Material.PAINTING) ||
								p.getInventory().getItemInMainHand().getType().equals(Material.ARMOR_STAND))
						{
							if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
								if(!abw.hasAdminPermissions(p)){
									e.setCancelled(true);
									Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								}
							}else{
								AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
								
								if(plot.hasOwner()){
									if(!plot.havePermissions(p)){
										e.setCancelled(true);
										Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
										return;
									}
								}else{
									if(!abw.hasAdminPermissions(p)){
										e.setCancelled(true);
										Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
										return;
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
