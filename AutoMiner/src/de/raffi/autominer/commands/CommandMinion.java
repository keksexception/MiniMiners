package de.raffi.autominer.commands;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.raffi.autominer.miner.Miner;
import de.raffi.autominer.miner.MinerCave;
import de.raffi.autominer.miner.MinerSword;
import de.raffi.pluginlib.builder.ArmorBuilder;
import de.raffi.pluginlib.builder.SkullBuilder;

public class CommandMinion implements CommandExecutor{

	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player p = (Player) sender;
		if(!p.hasPermission("miniminers.create")) {
			p.sendMessage("§cPermission §4miniminers.create §crequired to execute this command.");
			return false;
		}
		if(args.length == 0 || args.length == 1) {
			p.sendMessage("§6Syntax: /miniminer create <miner/butcher> §eSpawn Miner or Butcher minion");
			return false;
		}
		if(args.length == 2) {
			if(args[0].equalsIgnoreCase("create")) {
				if(args[1].equalsIgnoreCase("miner")) {
					Miner m = new MinerCave(p.getLocation(), "§b§lMiner",20,20,true);
					m.getArmorStand().setHelmet(new SkullBuilder(p.getName()).build());
					m.getArmorStand().setChestplate(new ArmorBuilder(Material.LEATHER_CHESTPLATE).setColor(Color.YELLOW).build());
					m.getArmorStand().setLeggings(new ArmorBuilder(Material.LEATHER_LEGGINGS).setColor(Color.YELLOW).build());
					m.getArmorStand().setBoots(new ArmorBuilder(Material.LEATHER_BOOTS).setColor(Color.BLACK).build());
					m.setJobDelay(30);
					m.setStatusText("Ready!");
					m.register();
				} else if(args[1].equalsIgnoreCase("butcher")) {
					Miner m = new MinerSword(p.getLocation(), "§b§lButcher",20,20,true);
					m.getArmorStand().setHelmet(new SkullBuilder(p.getName()).build());
					m.getArmorStand().setChestplate(new ArmorBuilder(Material.LEATHER_CHESTPLATE).setColor(Color.YELLOW).build());
					m.getArmorStand().setLeggings(new ArmorBuilder(Material.LEATHER_LEGGINGS).setColor(Color.YELLOW).build());
					m.getArmorStand().setBoots(new ArmorBuilder(Material.LEATHER_BOOTS).setColor(Color.BLACK).build());
					m.setJobDelay(30);
					m.setStatusText("Ready!");
					m.register();
				}
			} else
				p.sendMessage("§cInvalid command.");
		}
		return false;
	}

	
}
