package de.raffi.autominer.miner;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.json.simple.JSONObject;

import de.raffi.autominer.animations.AnimationJump;
import de.raffi.autominer.animations.AnimationMine;
import de.raffi.autominer.exception.PathfinderException;
import de.raffi.autominer.main.MiniMiners;
import de.raffi.autominer.pathfinder.Path;
import de.raffi.autominer.pathfinder.PathFinder;
import de.raffi.autominer.utils.MinerManager;
import de.raffi.autominer.utils.WalkingDispatcher;
import de.raffi.pluginlib.builder.ItemBuilder;

public class MinerCave extends Miner{


	

	public MinerCave(Location loc, String displayName, double health, double foodLevel, boolean allowsTeleportation,
			JSONObject additional) {
		super(loc, displayName, health, foodLevel, allowsTeleportation, additional);
		// TODO Auto-generated constructor stub
	}
	public MinerCave(Location loc, String displayName, double health, double foodLevel, boolean allowsTeleportation) {
		super(loc, displayName, health, foodLevel, allowsTeleportation);
		// TODO Auto-generated constructor stub
	}

	private int cy, index;
	
	private List<Block> blocks;
	private boolean walking = false;
	
	@SuppressWarnings("deprecation")
	@Override
	public void doJob() {
		if(walking) return;
		if(index >= blocks.size()) {
			startAnimation(new AnimationJump());
			stopDoJob();
			setStatusText("Job Finished!");
			return;
		}

		Block block = blocks.get(index);
		index++;
		if(block.getType()==Material.AIR) {
			Miner minionNear = null;
			for(Miner m : MinerManager.miners) {
				if(m!=this&&m.getLocation().getWorld().getName().equals(this.getLocation().getWorld().getName())) {
					if(m.getLocation().distance(this.getLocation())<=2) {
						minionNear = m;
						break;
					}
				}
			}
			setStatusText(minionNear==null?"???":"§aConnected with " + minionNear.getArmorStand().getCustomName());
			index++;
			doJob();
			return;
		}
		if(getInventory().firstEmpty()!=-1) { //-1 means full
			if(block.getType()==Material.LAVA||block.getType()==Material.STATIONARY_LAVA) {
					block.setType(Material.AIR);
					getInventory().addItem(new ItemStack(Material.LAVA_BUCKET));
					block.getWorld().playSound(block.getLocation(), Sound.LAVA, 1.0F, 1.0F);
			
			} else if(block.getType()==Material.WATER||block.getType()==Material.STATIONARY_WATER) {
					block.setType(Material.AIR);
					getInventory().addItem(new ItemStack(Material.WATER_BUCKET));
					block.getWorld().playSound(block.getLocation(), Sound.WATER, 1.0F, 1.0F);
		
			} else {
				getInventory().addItem(new ItemBuilder(block.getType(),block.getData()).build());
				block.setType(Material.AIR);
				block.getWorld().playSound(block.getLocation(), Sound.DIG_STONE, 1.0F, 1.0F);
				if(index>0) {
					/*setLocation(blocks.get(index-1).getLocation().add(0.5,0,0.5));
					if(!hasWalkingAnimation())
						walkAnimation.start(getArmorStand());*/
					try {
						Path path = PathFinder.findPath(getLocation(), blocks.get(index-1).getLocation());
						WalkingDispatcher w = new WalkingDispatcher(path, this, 3, 0, ()->{
							walking = false;
							getArmorStand().setRightLegPose(EulerAngle.ZERO);
							getArmorStand().setLeftLegPose(EulerAngle.ZERO);
							setStatusText("Breaking ...");
							if(!hasAnimation())startAnimation(new AnimationMine(true));
						});
						w.start();
							walking = true;
							setStatusText("Walking ...");
							stopAnimation();
							getArmorStand().setRightArmPose(EulerAngle.ZERO);
					} catch (PathfinderException e) {
						setLocation(blocks.get(index-1).getLocation().add(0.5,0,0.5));
						setStatusText("§4Error whilst getting path");
					}
				
				}
			}
		
			Bukkit.getScheduler().scheduleSyncDelayedTask(MiniMiners.getInstance(), ()->rotateHeadTo(blocks.get(index).getLocation().add(0.5, 0, 0.5)),1);
		
		//	if(!walking && !hasAnimation()) startAnimation(new AnimationMine(true));
		} else {
			stopDoJob();
			getLocation().getWorld().playSound(getLocation(), Sound.ZOMBIE_WOODBREAK, 1.0F, 1.0F);
			setStatusText("Inventory full!");
		}
		
		cy-=1;
	}
	@Override
	public void stopDoJob() {
		walking = false;
		super.stopDoJob();
	}
	@Override
	public ItemStack getHandItem() {
		return new ItemStack(Material.DIAMOND_PICKAXE);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void init() {
		setStatusText("Working ...");
		this.blocks = new ArrayList<>();
		cy = getLocation().getBlockY();
		Chunk chunk = getLocation().getChunk();
		for (int y = cy; y > 0; y--) {
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					Block block = chunk.getBlock(x, y, z);
					if (block.getType() != Material.AIR && block.getType() != Material.BEDROCK) {
						if((block.getType()==Material.STATIONARY_WATER||block.getType()==Material.STATIONARY_LAVA)&&block.getData()!=0)continue;
						blocks.add(block);
					}
				}

			}
		}

	}
}
