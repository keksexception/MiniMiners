package de.raffi.autominer.inventory;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import de.raffi.autominer.compability.CompabilityManager;
import de.raffi.autominer.configuration.Settings;
import de.raffi.autominer.main.MiniMiners;
import de.raffi.autominer.miner.Miner;
import de.raffi.pluginlib.builder.ItemBuilder;
import de.raffi.pluginlib.test.InputHandler;
import de.raffi.pluginlib.test.MessageHandler;

public class InventoryManager {
	
	public static final String INVENTORY_MINER = "§6Option Select";
	public static final String INVENTORY_CLOTHING = "§cClothing";
	
	private static HashMap<Player, BetterInventory> inventory = new HashMap<>();


	/**
	 * returns the inventory, in that you can select helmet,chestplate,leggins,boots
	 * @param m the minion
	 * @return
	 */
	public static Inventory createMinionClothingInventory(Miner m) {
		Inventory inv = Bukkit.createInventory(null, 9,INVENTORY_CLOTHING);
		inv.setItem(0, new ItemBuilder(m.getArmorStand().getHelmet()).setName("§7Helmet").setLore("§7Change his helmet").build());
		inv.setItem(1, new ItemBuilder(m.getArmorStand().getChestplate()).setName("§7Chestplate").setLore("§7Change his Chestplate").build());
		inv.setItem(2, new ItemBuilder(m.getArmorStand().getLeggings()).setName("§7Leggings").setLore("§7Change his Leggings").build());
		inv.setItem(3, new ItemBuilder(m.getArmorStand().getBoots()).setName("§7Boots").setLore("§7Change his Boots").build());
		
		return inv;
	}
	/**
	 * returns the inventory, in that you can select the options
	 * @param m the miner
	 * @return
	 */
	public static BetterInventory createMinionOptionSelectInventory(Miner m) {
		BetterInventory inv = new BetterInventory(Bukkit.createInventory(null, 9,INVENTORY_MINER+" §cpublic test")) {
			@Override
			public void update() {
				if(m.isWorking()) 
					setItem(1, new ClickItem(new ItemBuilder(Material.COAL).setName("§eStop Working").setLore("§7Stops the Minions current job"), p->{
						m.stopDoJob();
						m.resetAnimation();
						update();
					}));
				else
					setItem(1, new ClickItem(new ItemBuilder(Material.STONE_AXE).setName("§eStart Working").setLore("§7Starts the Minions current job"), p->{
						m.startDoJob(m.getJobDelay());
						update();
					}));
				setItem(4, new ClickItem( new ItemBuilder(Material.PAINTING).setName("§3Stats").setLore("§7Health: §a"+m.getHealth()+"/20","§7Food: §a"+m.getFoodLevel()+"/20","§7Stamina: §cComming soon","§7Click to refill"), p->{
					m.setHealth(20);
				}));
				
				String allowTeleport = m.doAllowTeleportation()?"§atrue":"§cfalse";
				setItem(5, new ClickItem(new ItemBuilder(Material.ENDER_PEARL).setName("§bTeleportation").setLore("§7Allowed: "+allowTeleport),p->{
					m.setAllowsTeleportation(!m.doAllowTeleportation());
					update();
				}));
			}
			@Override
			public void playSound(Player p) {
				p.playSound(p.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
			}
		};
		inv.setItem(0,new ClickItem(new ItemBuilder(Material.CHEST).setName("§eOpen Inventory").setLore("§7Opens the Minions inventory"), p->{
			p.openInventory(m.getInventory());
		}));
		
		inv.setItem(2, new ClickItem(new ItemBuilder(Material.PAPER).setName("§eRename").setLore("§7Change the name"), p->{
			InputHandler.getInputFrom(p, "§eEnter new Name for this Minion", new MessageHandler() {
				
				@Override
				public void onMessageDenied(String msg) {}
				
				@Override
				public void onHandlerRemoved() {
					p.sendMessage("§6You renamed your Minion");
					
				}
				
				@Override
				public boolean handleMessage(String msg) {
					m.getArmorStand().setCustomName(ChatColor.translateAlternateColorCodes('&', msg));
					return true;
				}
			});
		}));
		inv.setItem(3, new ClickItem(new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).setName("§eChange clothing").setLore("§7Change the look"), p->{
			p.openInventory(InventoryManager.createMinionClothingInventory(m));
		}));

		
		inv.setItem(6, new ClickItem( new ItemBuilder(Material.BARRIER).setName("§cRemove").setLore("§cRemove the Minion"), p->{
			m.remove(true);
			CompabilityManager.HANDLER.spawnDestroyParticle(m.getLocation());
			p.playSound(p.getLocation(), Sound.EXPLODE, 1.0F, 1.0F);
			p.closeInventory();
			inv.close(p);
		}));
		
		inv.setItem(7, new ClickItem( new ItemBuilder(Material.ANVIL).setName("§7Future Features").setLore("§7- Broken Tools","§7- Rest","§7- Economy integration (Vault)","§7- Medical insurance","§7- Happyness System", "§7- and more ..."), p->{
			
		}));
		inv.update();

		return inv;
	}
	/**
	 * starts inventory update loop (20 ticks) repeat
	 */
	public static void init() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MiniMiners.getInstance(), ()->{
			for(BetterInventory openInventorys : inventory.values()) {
				openInventorys.update();
			}
		},Settings.INVENTORY_UPDATE_TICKS, Settings.INVENTORY_UPDATE_TICKS);
	}
	public static void openBetterInventory(Player p, BetterInventory i) {
		if(inventory.get(p)!=null) {
			i.close(p);
			inventory.remove(p);
		}
		p.openInventory(i.getContainer());
		inventory.put(p, i);
	}
	public static BetterInventory getBetterInventory(Player of) {
		return inventory.get(of);
	}
}
