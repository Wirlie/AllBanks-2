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
package me.wirlie.allbanks.listeners.shops;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.PermissionsConstants;
import me.wirlie.allbanks.Shops;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.utils.InteractiveUtil;
import me.wirlie.allbanks.utils.ItemNameUtil;
import me.wirlie.allbanks.utils.ShopUtil;
import me.wirlie.allbanks.utils.Util;
import me.wirlie.allbanks.utils.InteractiveUtil.SoundType;

/**
 * Detectar cuando un jugador intenta hacer una tienda
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
@SuppressWarnings("javadoc")
public class ShopSignChangeListener implements Listener {
	@EventHandler(ignoreCancelled = true)
	public void onSignChange(final SignChangeEvent e){
		String[] lines = e.getLines();
		
		if(lines[Shops.LINE_HEADER].equalsIgnoreCase("AllBanks Shop") || lines[0].equalsIgnoreCase("AllBanksShop") || lines[0].equalsIgnoreCase("AB Shop") || lines[0].equalsIgnoreCase("ABShop")) {
			//Bien antes que nada, el usuario tiene permisos?
			final Player p = e.getPlayer();
			
			if(!Util.hasPermission(p, PermissionsConstants.SHOP_ADMIN_PERMISSION)) {
				Translation.getAndSendMessage(p, StringsID.NO_PERMISSIONS_FOR_THIS, true);
				InteractiveUtil.sendSound(p, SoundType.DENY);
				e.getBlock().breakNaturally();
				return;
			}
			
			if(!AllBanks.getInstance().getConfig().getBoolean("modules.shop.enable")){
				Translation.getAndSendMessage(p, StringsID.MODULE_DISABLED, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>AllBanksShop"), true);
				e.setCancelled(true);
				return;
			}
			
			boolean isAdminShop = false;
			
			if(lines[Shops.LINE_OWNER].equalsIgnoreCase(Shops.ADMIN_TAG)) {
				if(!Util.hasPermission(p, PermissionsConstants.SHOP_ADMIN_PERMISSION)) {
					Translation.getAndSendMessage(p, StringsID.SHOP_NO_PERMISSIONS_FOR_ADMIN_SHOP, true);
					InteractiveUtil.sendSound(p, SoundType.DENY);
					e.getBlock().breakNaturally();
					return;
				}else {
					isAdminShop = true;
				}
			}
			
			if(!isAdminShop) lines[Shops.LINE_OWNER] = e.getPlayer().getName();
			
			//Validar la línea de precio:
			if(!ShopUtil.validatePriceLine(lines[Shops.LINE_PRICE])) {
				Translation.getAndSendMessage(p, StringsID.SHOP_PRICE_LINE_NOT_VALID, true);
				InteractiveUtil.sendSound(p, SoundType.DENY);
				e.getBlock().breakNaturally();
				return;
			}
			
			//Validar la cantidad de objetos
			int amount = ShopUtil.getItemAmount(e.getLine(Shops.LINE_PRICE));
			
			if(amount <= 0) {
				Translation.getAndSendMessage(p, StringsID.ONLY_VALID_NUMBER_MORE_THAN_0, true);
				e.getBlock().breakNaturally();
				return;
			}
			
			if(amount > 64) {
				//es demasiado
				Translation.getAndSendMessage(p, StringsID.SHOP_ERROR_ITEM_MAX_64, true);
				e.getBlock().breakNaturally();
				return;
			}
			
			if(lines[Shops.LINE_ITEM].replace(" ", "").equalsIgnoreCase("")){
				lines[Shops.LINE_ITEM] = "???";
			}
			
			//Validar el nombre de item
			Pattern defaultItemSyntax = Pattern.compile("^([A-Za-z]{1,})(|:([0-9]{1,}))$");
			Pattern specialItemSyntax = Pattern.compile("^([A-Za-z]{1,})#([0-9]{1,})$");
			
			Matcher match = defaultItemSyntax.matcher(lines[Shops.LINE_ITEM]);
			
			if(match.matches()){
				String shortName = match.group(1);
				Material material = ItemNameUtil.getItemByShortName(shortName);
				
				if(material == null){
					lines[Shops.LINE_ITEM] = "???";
				}else{
					if(ShopUtil.itemNeedResolveCustomDurability(material)){
						//Okey, si es un objeto especial no se puede especificar una durabilidad.
						lines[Shops.LINE_ITEM] = "???";
					}
				}
			}else{
				match = specialItemSyntax.matcher(lines[Shops.LINE_ITEM]);
				
				if(match.matches()){
					String shortName = match.group(1);
					if(ItemNameUtil.getItemByShortName(shortName) == null){
						lines[Shops.LINE_ITEM] = "???";
					}else{
						//Comprobar si el ID especial se encuentra registrado
						String specialID = match.group(2);
						if(ShopUtil.checkForSpecialID(specialID) == false){
							lines[Shops.LINE_ITEM] = "???";
						}
					}
				}
			}
			
			final String[] finalLines = lines;
			
			//Procesar
			new BukkitRunnable() {

				public void run() {
					Shops.makeNewShop(finalLines, e.getBlock(), p);
				}
				
			}.runTaskLater(AllBanks.getInstance(), 20 * 1);
		}
	}
}
