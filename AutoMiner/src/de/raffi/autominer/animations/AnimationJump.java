package de.raffi.autominer.animations;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

public class AnimationJump extends Animation{

	private double radian;
	private Location original;
	public AnimationJump() {
		super(2);
	}
	
	@Override
	public void animate(ArmorStand stand) {
		
		stand.teleport(original.clone().add(0, Math.sin(radian)*1, 0));
		
		if(radian>=Math.PI) {
			stop();
			stand.teleport(original);
		}
		
		radian+=Math.PI/6;
	}
	@Override
	public void init(ArmorStand stand) {
		this.original = stand.getLocation().clone();
	}

}
