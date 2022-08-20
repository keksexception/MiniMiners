package de.raffi.autominer.listener;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import de.raffi.autominer.compability.CompabilityManager;
import de.raffi.autominer.inventory.BetterInventory;
import de.raffi.autominer.inventory.ClickItem;
import de.raffi.autominer.inventory.InventoryManager;
import de.raffi.autominer.miner.Miner;
import de.raffi.autominer.utils.MinerManager;
import de.raffi.pluginlib.builder.ArmorBuilder;

public class InteractListener implements Listener {
	
	private HashMap<Player, Miner> clickedMiner = new HashMap<>();
	
	@EventHandler
	public void onInteract(PlayerInteractAtEntityEvent e) {
		if(e.getRightClicked().getType()!=EntityType.ARMOR_STAND) return;
			/*
			 * open the options inventory on rightclick
			 */
			for(Miner m : MinerManager.miners) {
				if(m.getArmorStand().getEntityId()==e.getRightClicked().getEntityId()) {
					clickedMiner.put(e.getPlayer(), m);
					// e.getPlayer().openInventory(m.getOptionsInventory());
					InventoryManager.openBetterInventory(e.getPlayer(), InventoryManager.createMinionOptionSelectInventory(m));
					break;
				}
			}
		
	}
	
	
	@EventHandler
	public void onManipulate(PlayerArmorStandManipulateEvent e) {
		/*
		 * cancel armorstand interaction, manipulation
		 */
		if(!e.getRightClicked().isVisible()) {
			e.setCancelled(true);
			return;
		}
		for(Miner m : MinerManager.miners) {
			if(m.getArmorStand().getEntityId()==e.getRightClicked().getEntityId()) {
				e.setCancelled(true);
				break;
			}
		}
	}
	
	
	@EventHandler
	public void handleInventoryclick(InventoryClickEvent e)  {
		if(e.getClickedInventory()!=null&&e.getInventory()!=null&&e.getCurrentItem().getType()!=Material.AIR) {
			Player p = (Player) e.getWhoClicked();
			if(e.getClickedInventory().getTitle().equals(InventoryManager.INVENTORY_CLOTHING)) {
				/*
				 * clothing options
				 */
				Miner m = clickedMiner.get(p);
				e.setCancelled(true);
				switch (e.getCurrentItem().getType()) {
				case LEATHER_HELMET:
					m.getArmorStand().setHelmet(new ArmorBuilder(Material.LEATHER_HELMET).setColor(m.getColorManager().getNextColor()).build());
					break;
				case LEATHER_CHESTPLATE:
					m.getArmorStand().setChestplate(new ArmorBuilder(Material.LEATHER_CHESTPLATE).setColor(m.getColorManager().getNextColor()).build());
					break;
				case LEATHER_LEGGINGS:
					m.getArmorStand().setLeggings(new ArmorBuilder(Material.LEATHER_LEGGINGS).setColor(m.getColorManager().getNextColor()).build());
					break;
				case LEATHER_BOOTS:
					m.getArmorStand().setBoots(new ArmorBuilder(Material.LEATHER_BOOTS).setColor(m.getColorManager().getNextColor()).build());
					break;
				default:
					break;
				}
				p.playSound(p.getLocation(), Sound.ENDERDRAGON_WINGS, 1.0F, 2.0F);
				p.openInventory(InventoryManager.createMinionClothingInventory(m));
			}
			
			BetterInventory inv = InventoryManager.getBetterInventory(p);
			if(inv != null) {
				if(inv.getTitle().equals(e.getClickedInventory().getTitle()))
				for(ClickItem clickItem : inv.getItems()) {
					if(clickItem.getItem().equals(e.getCurrentItem())) {
						e.setCancelled(true);
						clickItem.click(p);
						inv.playSound(p);
						break;
					}
				}
					 
			}
			
		}
	}
	
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity().getType() != EntityType.ARMOR_STAND)
			return;
		for (Miner m : MinerManager.miners) {
			if (m.getArmorStand().getEntityId() == e.getEntity().getEntityId()
					|| m.getStatusStand().getEntityId() == e.getEntity().getEntityId()) {
				e.setCancelled(true);
				if (e.getCause() != DamageCause.LAVA && e.getCause() != DamageCause.FIRE_TICK
						&& e.getCause() != DamageCause.SUFFOCATION)
					m.damage(e.getFinalDamage());
				break;
			}
		}

	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if (e.getBlock().getType() != Material.FLOWER_POT)
			return;
		if (!CompabilityManager.HANDLER.hasNBTTag(e.getPlayer().getItemInHand(), "minerdata"))
			return;
		
		Miner m = Miner.fromItem(e.getPlayer().getItemInHand(), e.getBlock().getLocation().add(0.5, 0, 0.5));
		m.register();
		// Bukkit.getScheduler().scheduleSyncDelayedTask(MiniMiners.getInstance(),()->m.setLocation(e.getBlock().getLocation().add(0.5,
		// 0.5, 0.5)),2);
		m.setStatusText("Ready!");
		e.getPlayer().setItemInHand(null);
		e.setCancelled(true);

	}
}
