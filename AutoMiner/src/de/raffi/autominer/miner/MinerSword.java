package de.raffi.autominer.miner;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.json.simple.JSONObject;

import de.raffi.autominer.animations.AnimationPunch;
import de.raffi.autominer.configuration.Settings;
import de.raffi.autominer.exception.PathfinderException;
import de.raffi.autominer.pathfinder.Path;
import de.raffi.autominer.pathfinder.PathFinder;
import de.raffi.autominer.utils.WalkingDispatcher;

public class MinerSword extends Miner{

	
	
	public MinerSword(Location loc, String displayName, double health, double foodLevel, boolean allowsTeleportation,
			JSONObject additional) {
		super(loc, displayName, health, foodLevel, allowsTeleportation, additional);
		// TODO Auto-generated constructor stub
	}
	public MinerSword(Location loc, String displayName, double health, double foodLevel, boolean allowsTeleportation) {
		super(loc, displayName, health, foodLevel, allowsTeleportation);
		// TODO Auto-generated constructor stub
	}

	private boolean attacking, pathFinding;
	private LivingEntity attack;
	@Override
	public void doJob() {
		if(getHealth()<=0) {
			stopDoJob();
			return;
		}
			
		LivingEntity nextEntity = null;
		for(Entity living : getLocation().getWorld().getNearbyEntities(getLocation(), 20, 3, 20)) {
			if(!(living instanceof LivingEntity))
				continue;
			if(living instanceof Player || living instanceof ArmorStand)
				continue;
			if(nextEntity==null) {
				nextEntity = (LivingEntity) living;
				continue;
			}

			if(living.equals(getArmorStand()))
				continue;
			double livingDistance = getLocation().distance(living.getLocation());
			if(livingDistance < Settings.MINER_SWORD_TRACKINGDISTANCE_MAX && getLocation().distance(nextEntity.getLocation())>livingDistance) //max distance 10;
				nextEntity = (LivingEntity) living;
		}
		if(nextEntity==null) 
			return;
		
		
		if(attack!=null) {
			if(attack.isDead()||attack.getHealth()<=0) attacking = false;
			if(getLocation().distance(attack.getLocation())<=3.5) {
				if(!hasAnimation()) {
					rotateHeadTo(attack.getLocation());
					startAnimation(new AnimationPunch());
					attack.damage(6D,getArmorStand());
					attackVelocity(attack);
					for(Entity e : getLocation().getWorld().getNearbyEntities(getLocation(), 4, 4, 4)) {
						if(e instanceof Item) {
							Item stack = (Item) e;
							getInventory().addItem(stack.getItemStack());
							e.remove();
						}
					}
				}
			} 
		} else attacking = false;
		
		if(attacking)
			return;
		
		attack = nextEntity;
		
		if(pathFinding)
			return;
		try {
			Path path = PathFinder.findPath(getLocation(), nextEntity.getLocation());
			WalkingDispatcher w = new WalkingDispatcher(path, this, 3, 0, ()->{
				getArmorStand().setRightLegPose(EulerAngle.ZERO);
				getArmorStand().setLeftLegPose(EulerAngle.ZERO);
				pathFinding = false;
				attacking = false;
			});
			w.start();
			stopAnimation();
			attacking = true;
			pathFinding = true;
		} catch (PathfinderException e) {
			if(!doAllowTeleportation()) return;
			setLocation(findNear(nextEntity.getLocation()));
			rotateHeadTo(nextEntity.getLocation());
			setStatusText("§4Error whilst getting path");
		}
		
		
	}
	private Location findNear(Location loc) {
		for(int x = -1; x < 1; x++) {
			for(int z = -1; z < 1; z++) {
				Block b = loc.getWorld().getBlockAt(loc.getBlockX()+x, loc.getBlockY(), loc.getBlockZ()+z);
				if(b.getType()==Material.AIR) return b.getLocation();
					
			}
		}
		return loc;
	}
	private void attackVelocity(LivingEntity target) {
		target.setVelocity(getArmorStand().getLocation().getDirection());
	}
	@Override
	public void init() {
		this.attacking = false;
		this.pathFinding = false;
		
	}

	@Override
	public ItemStack getHandItem() {
		return new ItemStack(Material.DIAMOND_SWORD);
	}

}
