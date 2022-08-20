package de.raffi.autominer.animations;

import org.bukkit.entity.ArmorStand;
import org.bukkit.util.EulerAngle;

public class AnimationMine extends Animation{

	private boolean repeatInfiniy;
	public AnimationMine(boolean repeatInfinty) {
		super(2);
		this.repeatInfiniy = repeatInfinty;
	}
	private double radian;
	
	@Override
	public void animate(ArmorStand stand) {
		stand.setRightArmPose(new EulerAngle(1.5D*Math.sin(-radian), 0,0));
		radian+=Math.PI/6;
	
		if(radian>=Math.PI) {
			if(!repeatInfiniy)
				stop();
		}
		radian%=Math.PI;
	}

}
