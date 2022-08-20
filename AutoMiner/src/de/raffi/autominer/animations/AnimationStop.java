package de.raffi.autominer.animations;

import org.bukkit.entity.ArmorStand;
import org.bukkit.util.EulerAngle;

public class AnimationStop extends Animation{

	public AnimationStop(int delay) {
		super(delay);
		// TODO Auto-generated constructor stub
	}
	private double armRight, rightArmDecrease;
	private int index=0;
	
	@Override
	public void animate(ArmorStand stand) {
		
		stand.setRightArmPose(new EulerAngle(armRight, 0, 0));
		stand.setRightLegPose(EulerAngle.ZERO);
		stand.setLeftLegPose(EulerAngle.ZERO);
		
		
		
		armRight+=rightArmDecrease;
		index++;
		if(index>=5) stop();

	}
	@Override
	public void init(ArmorStand stand) {
		index=0;
		armRight = stand.getRightArmPose().getX();
		
		rightArmDecrease = armRight>0?armRight/5D:armRight/-5D;

		super.init(stand);
	}

}
