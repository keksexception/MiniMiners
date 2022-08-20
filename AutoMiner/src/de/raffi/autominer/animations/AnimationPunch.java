package de.raffi.autominer.animations;

import org.bukkit.entity.ArmorStand;
import org.bukkit.util.EulerAngle;

public class AnimationPunch extends Animation{

	public AnimationPunch() {
		super(2);
		// TODO Auto-generated constructor stub
	}
	private double radian=Math.PI;
	@Override
	public void animate(ArmorStand stand) {
		stand.setRightArmPose(new EulerAngle(-Math.sin(radian), 0,0));
		radian-=Math.PI/6;
	
		if(radian<=0) {
			stop();
		}
		
	}

}
