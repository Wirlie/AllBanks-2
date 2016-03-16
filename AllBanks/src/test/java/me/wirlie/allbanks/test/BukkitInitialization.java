package me.wirlie.allbanks.test;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_9_R1.CraftServer;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemFactory;
import org.bukkit.craftbukkit.v1_9_R1.util.Versioning;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.logging.Logger;

import net.minecraft.server.v1_9_R1.DispenserRegistry;

public class BukkitInitialization {
	
	private static boolean initialized;
	
	public static void initializeItemMeta() throws IllegalAccessException {
		if (!initialized) {
			initialized = true;

			DispenserRegistry.c();

			Server mockedServer = mock(Server.class);

			when(mockedServer.getLogger()).thenReturn(Logger.getLogger("Minecraft"));
			when(mockedServer.getName()).thenReturn("Mock Server");
			when(mockedServer.getVersion()).thenReturn(CraftServer.class.getPackage().getImplementationVersion());
			when(mockedServer.getBukkitVersion()).thenReturn(Versioning.getBukkitVersion());

			when(mockedServer.getItemFactory()).thenReturn(CraftItemFactory.instance());
			when(mockedServer.isPrimaryThread()).thenReturn(true);

			Bukkit.setServer(mockedServer);
		}
	}
}
