package de.raffi.autominer.animations;

import org.bukkit.entity.ArmorStand;
import org.bukkit.util.EulerAngle;

public class AnimationWalking extends Animation {

	public AnimationWalking(int delay) {
		super(delay);
	}

	private double radian=0;
	
	@Override
	public void animate(ArmorStand stand) {
		stand.setLeftLegPose(new EulerAngle(Math.sin(radian%(Math.PI*2)+Math.PI/3), 0,0));
		stand.setRightLegPose(new EulerAngle(-Math.sin(radian%(Math.PI*2)+Math.PI/3), 0,0));
		radian+=Math.PI/10;
	

		radian%=Math.PI*2;
		
		
		
		
	}

}
