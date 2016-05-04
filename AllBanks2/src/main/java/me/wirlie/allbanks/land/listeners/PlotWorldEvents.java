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
package me.wirlie.allbanks.land.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;

import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.land.AllBanksPlot;
import me.wirlie.allbanks.land.AllBanksWorld;
import me.wirlie.allbanks.land.WorldConfiguration;

/**
 * @author josue
 *
 */
public class PlotWorldEvents implements Listener {
	
	public boolean isAnimal(Entity e){
		if(e.getType().equals(EntityType.COW) ||
				e.getType().equals(EntityType.CHICKEN) ||
				e.getType().equals(EntityType.PIG) ||
				e.getType().equals(EntityType.SHEEP) ||
				e.getType().equals(EntityType.OCELOT) ||
				e.getType().equals(EntityType.WOLF) ||
				e.getType().equals(EntityType.HORSE))
		{
			return true;
		}
		
		return false;
	}
	
	public boolean isNeutral(Entity e){
		if(e.getType().equals(EntityType.SNOWMAN) ||
				e.getType().equals(EntityType.IRON_GOLEM) ||
				e.getType().equals(EntityType.VILLAGER) ||
				e.getType().equals(EntityType.MINECART) ||
				e.getType().equals(EntityType.MINECART_CHEST) ||
				e.getType().equals(EntityType.MINECART_COMMAND) ||
				e.getType().equals(EntityType.MINECART_FURNACE) ||
				e.getType().equals(EntityType.MINECART_HOPPER) ||
				e.getType().equals(EntityType.MINECART_MOB_SPAWNER) ||
				e.getType().equals(EntityType.MINECART_TNT) ||
				e.getType().equals(EntityType.ARMOR_STAND) ||
				e.getType().equals(EntityType.BOAT) ||
				e.getType().equals(EntityType.DROPPED_ITEM) ||
				e.getType().equals(EntityType.ENDER_CRYSTAL) ||
				e.getType().equals(EntityType.ENDER_SIGNAL))
		{
			return true;
		}
		
		return false;
	}
	
	public boolean isHostil(Entity e){
		if(!isAnimal(e) && !isNeutral(e)){
			return true;
		}
		
		return false;
	}
	
	@EventHandler
	public void arrowDamage(){
		
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
					if(e.getAction().equals(Action.PHYSICAL))
						if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
							e.setCancelled(true);
						}else{
							AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
							
							if(!plot.hasOwner()){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
							}else{
								if(!plot.getPlotConfiguration().usePressurePlate() && !plot.havePermissions(p)){
									e.setCancelled(true);
									Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								}
							}
						}
				}
				
				if(b.getType().equals(Material.LEVER)){
					if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
						e.setCancelled(true);
					}else{
						AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
						
						if(!plot.hasOwner()){
							e.setCancelled(true);
							Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						}else{
							if(!plot.getPlotConfiguration().useLever() && !plot.havePermissions(p)){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
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
						e.setCancelled(true);
					}else{
						AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
						
						if(!plot.hasOwner()){
							e.setCancelled(true);
							Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						}else{
							if(!plot.getPlotConfiguration().useDoor() && !plot.havePermissions(p)){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
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
						e.setCancelled(true);
					}else{
						AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
						
						if(!plot.hasOwner()){
							e.setCancelled(true);
							Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						}else{
							if(!plot.getPlotConfiguration().useFenceDoor() && !plot.havePermissions(p)){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
							}
						}
					}
				}
				
				if(b.getType().equals(Material.ENCHANTMENT_TABLE)){
					if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
						AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
						
						if(plot.hasOwner()){
							if(!plot.getPlotConfiguration().useEnchantmentTable() && !plot.havePermissions(p)){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
							}
						}
					}
				}
				
				if(b.getType().equals(Material.WORKBENCH)){
					if(abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
						AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
						
						if(plot.hasOwner()){
							if(!plot.getPlotConfiguration().useWorkbench() && !plot.havePermissions(p)){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
							}
						}
					}
				}
				
				if(b.getType().equals(Material.ANVIL)){
					if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
						e.setCancelled(true);
					}else{
						AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
						
						if(!plot.hasOwner()){
							e.setCancelled(true);
							Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						}else{
							if(!plot.getPlotConfiguration().useWorkbench() && !plot.havePermissions(p)){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
							}
						}
					}
				}
				
				if(b.getType().equals(Material.CAKE_BLOCK)){
					if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
						e.setCancelled(true);
					}else{
						AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
						
						if(!plot.hasOwner()){
							e.setCancelled(true);
							Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						}else{
							if(!plot.havePermissions(p)){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
							}
						}
					}
				}
				
				if(b.getType().equals(Material.STONE_BUTTON) || b.getType().equals(Material.WOOD_BUTTON)){
					if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
						e.setCancelled(true);
					}else{
						AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
						
						if(!plot.hasOwner()){
							e.setCancelled(true);
							Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						}else{
							if(!plot.getPlotConfiguration().useButton() && !plot.havePermissions(p)){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
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
								e.setCancelled(true);
							}else{
								AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
								
								if(!plot.hasOwner()){
									e.setCancelled(true);
									Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								}else{
									if(!plot.havePermissions(p)){
										e.setCancelled(true);
										Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
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
								p.getInventory().getItemInMainHand().getType().equals(Material.BOAT_SPRUCE))
						{
							if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
								e.setCancelled(true);
							}else{
								AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
								
								if(!plot.hasOwner()){
									e.setCancelled(true);
									Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								}else{
									if(!plot.havePermissions(p)){
										e.setCancelled(true);
										Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void preventDropItem(PlayerDropItemEvent e){
		Location loc = e.getItemDrop().getLocation();
		
		if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
			AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
			
			if(abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
				AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
				
				if(plot.hasOwner()){
					if(!plot.getPlotConfiguration().dropItem()){
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void preventCropGrief(PlayerInteractEvent e){
		if(e.getAction().equals(Action.PHYSICAL)){
			if (e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.SOIL){
				Location loc = e.getClickedBlock().getLocation();
				
				if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
					AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
					
					if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
						e.setCancelled(true);
						return;
					}
					
					AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
					
					if(!plot.hasOwner()){
						//Si esta permitido hacer esto en un plot sin dueño
						return;
					}
					
					if(!plot.havePermissions(e.getPlayer())){
						e.setCancelled(true);
						Translation.getAndSendMessage(e.getPlayer(), StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						return;
					}
				}
			}
		}
	}
	
	@EventHandler
	public void preventCroopGrief(EntityChangeBlockEvent e){
		if(e.getBlock().getType().equals(Material.SOIL)){
			Location loc = e.getBlock().getLocation();
			
			if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				
				if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
					e.setCancelled(true);
					return;
				}
				
				AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
				
				if(plot.hasOwner()){
					if(!e.getEntity().getType().equals(EntityType.PLAYER)) e.setCancelled(true);
					return;
				}
			}
		}
		
		if(e.getEntity().getType().equals(EntityType.WITHER)){
			Location loc = e.getBlock().getLocation();
			
			if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void preventUnleash(PlayerUnleashEntityEvent e){
		Location loc = e.getEntity().getLocation();
		Player p = e.getPlayer();
		
		if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
			AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
			
			if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
				//Si esta permitido usar esto en la calle.
				return;
			}
			
			AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
			
			if(!plot.hasOwner()){
				//Si esta permitido hacer esto en un plot sin dueño
				return;
			}
			
			if(!plot.havePermissions(p)){
				e.setCancelled(true);
				Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
				return;
			}
			
		}
	}
	
	@EventHandler
	public void preventFenceUnleash(HangingBreakByEntityEvent e){
		if(e.getRemover() instanceof Player){
			Player p = (Player) e.getRemover();
			Location loc = e.getEntity().getLocation(); //Localización de la valla con la cuerda sujetada
			
			if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				
				if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
					e.setCancelled(true);
					return;
				}
				
				AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
				
				if(!plot.hasOwner()){
					e.setCancelled(true);
					return;
				}
				
				if(!plot.havePermissions(p)){
					e.setCancelled(true);
					Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
					return;
				}
				
			}
		}
	}
	
	@EventHandler
	public void onPlayerLeash(PlayerLeashEntityEvent e){
		if(e.getLeashHolder() instanceof Player){
			Player p = (Player) e.getLeashHolder();
			Location loc = e.getEntity().getLocation();
			
			if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				
				if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
					//Si esta permitido usar esto en la calle.
					return;
				}
				
				AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
				
				if(!plot.hasOwner()){
					//Si esta permitido usar esto
					return;
				}
				
				if(!plot.havePermissions(p)){
					e.setCancelled(true);
					Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
					return;
				}
				
			}
		}
	}
	
	@EventHandler
	public void rideHorsePig(VehicleEnterEvent e){
		if(e.getVehicle().getType().equals(EntityType.BOAT) || e.getVehicle().getType().equals(EntityType.MINECART) || e.getVehicle().getType().equals(EntityType.HORSE) || e.getVehicle().getType().equals(EntityType.PIG)){
			Entity rider = e.getEntered();
			
			if(rider instanceof Player){
				Player p = (Player) rider;
				Location loc = e.getVehicle().getLocation();
				
				if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
					AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
					
					if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
						//Si esta permitido montar en la calle.
						return;
					}
					
					AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
					
					if(!plot.hasOwner()){
						//Permitido montar si el plot no tiene dueño.
						return;
					}
					
					if(!plot.havePermissions(p)){
						e.setCancelled(true);
						Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						return;
					}
					
				}
			}
		}
	}
	
	@EventHandler
	public void waterLavaFlow(BlockFromToEvent e){
		Location loc = e.getToBlock().getLocation();
		
		if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
			AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
			
			if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
				e.setCancelled(true);
				return;
			}
			
			AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
			
			if(!plot.hasOwner()){
				e.setCancelled(true);
				return;
			}
			
			//Comprobar configuración
			if(!plot.getPlotConfiguration().lavaFlow() && e.getBlock().getType().equals(Material.STATIONARY_LAVA)){
				e.setCancelled(true);
			}
			
			if(!plot.getPlotConfiguration().waterFlow() && e.getBlock().getType().equals(Material.STATIONARY_WATER)){
				e.setCancelled(true);
			}
			
		}
	}

	@EventHandler
	public void bucketFill(PlayerBucketFillEvent e){
		Player p = e.getPlayer();
		Location loc = e.getBlockClicked().getLocation();
		
		if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
			AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
			
			if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
				e.setCancelled(true);
				Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
				return;
			}
			
			AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
			
			if(!plot.hasOwner()){
				e.setCancelled(true);
				Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
				return;
			}
			
			if(!plot.havePermissions(p)){
				e.setCancelled(true);
				Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
			}
			
		}
	}
	
	@EventHandler
	public void bucketEmpty(PlayerBucketEmptyEvent e){
		Player p = e.getPlayer();
		Block clickedBlock = e.getBlockClicked();
		Block bukketBlock = 
				(e.getBlockFace().equals(BlockFace.NORTH)) ? clickedBlock.getRelative(BlockFace.NORTH) :
					((e.getBlockFace().equals(BlockFace.SOUTH)) ? clickedBlock.getRelative(BlockFace.SOUTH) : 
						((e.getBlockFace().equals(BlockFace.WEST)) ? clickedBlock.getRelative(BlockFace.WEST) : 
							((e.getBlockFace().equals(BlockFace.EAST)) ? clickedBlock.getRelative(BlockFace.EAST) : 
								((e.getBlockFace().equals(BlockFace.UP)) ? clickedBlock.getRelative(BlockFace.UP) : clickedBlock.getRelative(BlockFace.DOWN))
							)
						)
					);
	
		Location loc = bukketBlock.getLocation();
		
		if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
			AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
			
			if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
				e.setCancelled(true);
				Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
				return;
			}
			
			AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
			
			if(!plot.hasOwner()){
				e.setCancelled(true);
				Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
				return;
			}
			
			if(!plot.havePermissions(p)){
				e.setCancelled(true);
				Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
			}
			
		}
	}
	
	@EventHandler
	public void vehicleCollision(VehicleEntityCollisionEvent e){
		
		if(e.getEntity() instanceof Player){
			Player p = (Player) e.getEntity();
			Entity v = e.getVehicle();
			Location loc = v.getLocation();
			
			if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				
				if(abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
					AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
					
					if(plot.hasOwner()){
						if(!plot.havePermissions(p)){
							e.setCancelled(true);
							Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						}
					}
				}
				
			}
		}
	}
	
	@EventHandler
	public void vehicleDamage(VehicleDamageEvent e){
		Entity damager = e.getAttacker();
		
		if(damager instanceof Player){
			Player p = (Player) damager;
			Entity ent = e.getVehicle();
			Location loc = ent.getLocation();
			
			if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				
				if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
					//Todas las entidades fuera de un plot pueden ser atacables.
					return;
				}
				
				AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
				
				if(!plot.hasOwner()){
					//Todas las entidades que estén en un plot que no tenga dueño pueden ser atacables
					return;
				}
				
				if(!plot.havePermissions(p) && !isHostil(ent)){
					//No tiene derecho de hacer eso para atacar entidades no hostiles
					e.setCancelled(true);
					Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
					return;
				}
			}
		}else if(damager instanceof Arrow){
			Arrow arrow = (Arrow) damager;
			
			if(arrow.getShooter() instanceof Player){
				Player p = (Player) arrow.getShooter();
				Entity ent = e.getVehicle();
				Location loc = ent.getLocation();
				
				if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
					AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
					
					if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
						//Todas las entidades fuera de un plot pueden ser atacables.
						return;
					}
					
					AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
					
					if(!plot.hasOwner()){
						//Todas las entidades que estén en un plot que no tenga dueño pueden ser atacables
						return;
					}
					
					if(!plot.havePermissions(p) && !isHostil(ent)){
						//No tiene derecho de hacer eso para atacar entidades no hostiles
						e.setCancelled(true);
						Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						return;
					}
				}
			}
		}
	}

	@EventHandler
	public void entityDamage(EntityDamageEvent event){
		if(event instanceof EntityDamageByEntityEvent){
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)event;
			Entity damager = e.getDamager();
			
			if(damager instanceof Player){
				Player p = (Player) damager;
				Entity ent = e.getEntity();
				Location loc = ent.getLocation();
				
				if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
					AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
					
					if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
						//Todas las entidades fuera de un plot pueden ser atacables.
						return;
					}
					
					AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
					
					if(!plot.hasOwner()){
						//Todas las entidades que estén en un plot que no tenga dueño pueden ser atacables
						return;
					}
					
					if(!plot.havePermissions(p) && !isHostil(ent)){
						//No tiene derecho de hacer eso para atacar entidades no hostiles
						e.setCancelled(true);
						Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						return;
					}
				}
			}else if(damager instanceof Arrow){
				Arrow arrow = (Arrow) damager;
				
				if(arrow.getShooter() instanceof Player){
					Player p = (Player) arrow.getShooter();
					Entity ent = e.getEntity();
					Location loc = ent.getLocation();
					
					if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
						AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
						
						if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
							//Todas las entidades fuera de un plot pueden ser atacables.
							return;
						}
						
						AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
						
						if(!plot.hasOwner()){
							//Todas las entidades que estén en un plot que no tenga dueño pueden ser atacables
							return;
						}
						
						if(!plot.havePermissions(p) && !isHostil(ent)){
							//No tiene derecho de hacer eso para atacar entidades no hostiles
							e.setCancelled(true);
							Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
							return;
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockBurn(BlockBurnEvent e){
		Location loc = e.getBlock().getLocation();
		
		if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
			AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
			
			if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
				e.setCancelled(true);
				return;
			}
			
			AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
			
			if(!plot.hasOwner()){
				e.setCancelled(true);
				return;
			}
			
			if(!plot.getPlotConfiguration().fireSpread()){
				e.setCancelled(true);
				return;
			}
			
		}
	}
	
	@EventHandler
	public void onFireSpread(BlockIgniteEvent e){
		Location loc = e.getBlock().getLocation();

		if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
			AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
			
			if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
				e.setCancelled(true);
				return;
			}
			
			AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
			
			if(!plot.hasOwner()){
				e.setCancelled(true);
				return;
			}
			
			if(e.getCause().equals(IgniteCause.FLINT_AND_STEEL)){
				if(e.getIgnitingEntity() instanceof Player){
					Player p = (Player) e.getIgnitingEntity();
					if(!plot.getOwnerName().equalsIgnoreCase(p.getName())
							&& !plot.getPlotConfiguration().getFriends().contains(p.getName().toLowerCase()))
					{
						e.setCancelled(true);
					}
				}else{
					e.setCancelled(true);
					return;
				}
			}else if(e.getCause().equals(IgniteCause.SPREAD)){
				if(!plot.getPlotConfiguration().fireSpread()){
					e.setCancelled(true);
					return;
				}
			}else{
				//Otra causa, por ejemplo, un blaz
				e.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onEntitySpawn(CreatureSpawnEvent e){
		Entity ent = e.getEntity();
		Location loc = ent.getLocation();
		
		boolean animal = isAnimal(ent);
		boolean hostil = isHostil(ent);
		
		if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
			AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
			WorldConfiguration wcfg = abw.getWorldConfiguration();
			
			if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
				if(!wcfg.animalSpawn() && animal || !wcfg.mobSpawn() && hostil){
					e.setCancelled(true);
					return;
				}
			}else{
				AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
				
				if(!plot.hasOwner()){
					if(!wcfg.animalSpawn() && animal || !wcfg.mobSpawn() && hostil){
						e.setCancelled(true);
						return;
					}
				}else{
					if(!wcfg.mobSpawn() && hostil || !plot.getPlotConfiguration().mobs() && hostil || !wcfg.animalSpawn() && animal){
						e.setCancelled(true);
						return;
					}
				}
			}
			
			//Wither
			if(ent.getType().equals(EntityType.WITHER)){
				if(!wcfg.allowWither()){
					e.setCancelled(true);
					return;
				}
			}
			
		}
	}
	
	@EventHandler
	public void preventEntityExplosion(EntityExplodeEvent e){
		if(e.getEntityType().equals(EntityType.CREEPER) || e.getEntityType().equals(EntityType.WITHER) || e.getEntityType().equals(EntityType.WITHER_SKULL)){
			Location loc = e.getLocation();
			
			if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				WorldConfiguration wcfg = abw.getWorldConfiguration();
				if(e.getEntityType().equals(EntityType.CREEPER))
					if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
						e.setCancelled(true);
					}else if(!wcfg.creeperExplosion()){
						e.setCancelled(true);
					}else{
						//limitar bloques
						List<Block> remove = new ArrayList<Block>();
						for(Block b : e.blockList()){
							Location bloc = b.getLocation();
							
							if(!abw.locationIsPlot(bloc.getBlockX(), bloc.getBlockZ())){
								remove.add(b);
								continue;
							}
							
							AllBanksPlot bplot = abw.getPlot(bloc.getBlockX(), bloc.getBlockZ());
							
							if(!bplot.hasOwner()){
								remove.add(b);
								continue;
							}
						}
						
						e.blockList().removeAll(remove);
					}
				
				if(e.getEntityType().equals(EntityType.WITHER) || e.getEntityType().equals(EntityType.WITHER_SKULL))
					if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
						e.setCancelled(true);
					}else if(!wcfg.witherExplosion()){
						e.setCancelled(true);
					}else{
						//limitar bloques
						List<Block> remove = new ArrayList<Block>();
						for(Block b : e.blockList()){
							Location bloc = b.getLocation();
							
							if(!abw.locationIsPlot(bloc.getBlockX(), bloc.getBlockZ())){
								remove.add(b);
								continue;
							}
							
							AllBanksPlot bplot = abw.getPlot(bloc.getBlockX(), bloc.getBlockZ());
							
							if(!bplot.hasOwner()){
								remove.add(b);
								continue;
							}
						}
						
						e.blockList().removeAll(remove);
					}
			}
		}
		
		if(e.getEntityType().equals(EntityType.PRIMED_TNT) || e.getEntityType().equals(EntityType.MINECART_TNT)){
			Location loc = e.getLocation();
			
			if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				WorldConfiguration wcfg = abw.getWorldConfiguration();
				if(!wcfg.allowTNTExplosion()){
					e.setCancelled(true);
				}else{
					if (e.getEntity() instanceof TNTPrimed) {
						Entity causer = ((TNTPrimed) e.getEntity()).getSource();
						if(causer instanceof Player){
							if(abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
								AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
								Player p = (Player) causer;
								
								if(!plot.hasOwner()){
									e.setCancelled(true);
									return;
								}
								
								if(!plot.havePermissions(p)){
									e.setCancelled(true);
									return;
								}
								
								if(!plot.getPlotConfiguration().explosions()){
									e.setCancelled(true);
									return;
								}
								
								//Comprobar todos los bloques
								List<Block> remove = new ArrayList<Block>();
								for(Block b : e.blockList()){
									Location bloc = b.getLocation();
									
									if(!abw.locationIsPlot(bloc.getBlockX(), bloc.getBlockZ())){
										remove.add(b);
										continue;
									}
									
									AllBanksPlot bplot = abw.getPlot(bloc.getBlockX(), bloc.getBlockZ());
									
									if(!bplot.hasOwner()){
										remove.add(b);
										continue;
									}
									
									if(!bplot.havePermissions(p)){
										remove.add(b);
										continue;
									}
								}
								
								e.blockList().removeAll(remove);
							}else{
								e.setCancelled(true);
							}
						}else{
							//Otra entidad hizo la explosión
							e.setCancelled(true);
						}
					}else{
						//cancelar, por ejemplo, un minecart con TNT
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void preventEntityExplosionDamage(EntityDamageByEntityEvent e){
		if(e.getDamager().getType().equals(EntityType.CREEPER)){
			Location loc = e.getEntity().getLocation();
			
			if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				WorldConfiguration wcfg = abw.getWorldConfiguration();
				if(!wcfg.creeperExplosion()){
					e.setCancelled(true);
				}
			}
		}
		
		if(e.getDamager().getType().equals(EntityType.PRIMED_TNT) || e.getDamager().getType().equals(EntityType.MINECART_TNT)){
			Location loc = e.getEntity().getLocation();
			
			if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				WorldConfiguration wcfg = abw.getWorldConfiguration();
				if(!wcfg.allowTNTExplosion()){
					e.setCancelled(true);
					return;
				}
				
				if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
					e.setCancelled(true);
					return;
				}
				
				AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
				
				if(!plot.getPlotConfiguration().explosions()){
					e.setCancelled(true);
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void preventEntityExplosionDamage(PlayerPortalEvent e){
		if(e.getCause().equals(TeleportCause.NETHER_PORTAL)){
			Location loc = e.getFrom();
			
			if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				WorldConfiguration wcfg = abw.getWorldConfiguration();
				if(!wcfg.allowNetherPortal()){
					e.setCancelled(true);
				}
			}
		}
	}
}
