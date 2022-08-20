package de.raffi.autominer.compability;

import org.bukkit.Bukkit;

import de.raffi.pluginlib.utils.Logger;

public class CompabilityManager {
	
	public static VersionHandler HANDLER;
	
	@SuppressWarnings("deprecation")
	public static void setupVersionHandler() {
		Logger.debug("[Minions] Setting up VersionHander");
		try {
			HANDLER = (VersionHandler) Class.forName("de.raffi.autominer.compability.handler.Handler_" +getServerVersion()).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("[Minions] Unable to load Handler for your server! Your serverversion is not supported! " + getServerVersion());
		}
	}
	private static String getServerVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	}
	
	
	

}
