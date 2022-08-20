package de.raffi.autominer.main;


import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.raffi.autominer.commands.CommandMinion;
import de.raffi.autominer.commands.CommandTest;
import de.raffi.autominer.compability.CompabilityManager;
import de.raffi.autominer.configuration.Settings;
import de.raffi.autominer.inventory.InventoryManager;
import de.raffi.autominer.listener.InteractListener;
import de.raffi.autominer.utils.MinerManager;
import de.raffi.pluginlib.configuration.Configurator;

public class MiniMiners extends JavaPlugin {
	
	private static MiniMiners instance;
	
	@Override
	public void onEnable() {
		instance = this;
		
		getCommand("miniminer").setExecutor(new CommandMinion());
		
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new InteractListener(), instance);
		
		CompabilityManager.setupVersionHandler();
		
		Configurator.load(Settings.class, "plugins/Minions");
		
		InventoryManager.init();
	}
	
	public static MiniMiners getInstance() {
		return instance;
	}
	public void onDisable() {MinerManager.miners.forEach(miner->miner.remove(false));};
}
