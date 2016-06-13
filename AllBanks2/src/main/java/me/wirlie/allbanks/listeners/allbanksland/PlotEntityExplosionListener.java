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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import me.wirlie.allbanks.allbanksland.AllBanksPlot;
import me.wirlie.allbanks.allbanksland.AllBanksWorld;
import me.wirlie.allbanks.allbanksland.WorldConfiguration;

/**
 * @author Wirlie
 *
 */
@SuppressWarnings("javadoc")
public class PlotEntityExplosionListener implements Listener {
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
							
							if(!bplot.hasOwner() || !bplot.getPlotConfiguration().explosions()){
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
