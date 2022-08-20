package de.raffi.autominer.commands;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.raffi.autominer.exception.PathfinderException;
import de.raffi.autominer.main.MiniMiners;
import de.raffi.autominer.miner.Miner;
import de.raffi.autominer.miner.MinerSword;
import de.raffi.autominer.pathfinder.Path;
import de.raffi.autominer.pathfinder.PathFinder;
import de.raffi.autominer.utils.WalkingDispatcher;
import de.raffi.pluginlib.builder.ArmorBuilder;
import de.raffi.pluginlib.builder.SkullBuilder;

public class CommandTest implements CommandExecutor{

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player p = (Player) sender;
		Miner m = new MinerSword(p.getLocation(), "§b§lDev",20,20,true);
		//m.getArmorStand().setHelmet(new SkullBuilder("jeremiaslp").build());
		m.getArmorStand().setHelmet(new SkullBuilder(p.getName()).build());
		m.getArmorStand().setChestplate(new ArmorBuilder(Material.LEATHER_CHESTPLATE).setColor(Color.YELLOW).build());
		m.getArmorStand().setLeggings(new ArmorBuilder(Material.LEATHER_LEGGINGS).setColor(Color.YELLOW).build());
		m.getArmorStand().setBoots(new ArmorBuilder(Material.LEATHER_BOOTS).setColor(Color.BLACK).build());
		m.setJobDelay(30);
		m.setStatusText("Ready!");
		Bukkit.getScheduler().scheduleAsyncRepeatingTask(MiniMiners.getInstance(), ()->{
			//m.rotateHeadTo(p.getLocation());
		}, 10, 10);
		m.register();
			p.sendMessage("§eErfolg.");
			
		Bukkit.getScheduler().scheduleSyncDelayedTask(MiniMiners.getInstance(), ()->{
			try {
				Path path = PathFinder.findPath(m.getLocation(), p.getLocation());
				/*path.getNodes().forEach(node->{
					node.getBlock().setType(Material.WOOL);
				});
				path.getStart().getBlock().setType(Material.DIAMOND);
				path.getStop().getBlock().setType(Material.IRON_BLOCK);*/
				WalkingDispatcher d = new WalkingDispatcher(path, m, 5,0,()->m.setStatusText("Destination Reached!"));
				d.start();
			} catch (PathfinderException e) {
				m.setStatusText("Unable to find Path!");
			}
	
		},60);
			
		return false;
	}
	
	


}
