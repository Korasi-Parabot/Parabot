package ikov.pestcontrol;

import org.parabot.environment.api.utils.Random;
import org.parabot.environment.api.utils.Time;
import org.parabot.environment.scripts.framework.SleepCondition;
import org.parabot.environment.scripts.framework.Strategy;
import org.rev317.min.api.methods.Menu;
import org.rev317.min.api.methods.Players;
import org.rev317.min.api.methods.SceneObjects;
import org.rev317.min.api.wrappers.SceneObject;

public class EnterBoat implements Strategy {

	public boolean activate() {
		if(SceneObjects.getNearest(PPestcontrol.GANGPLANK_ID).length > 0){
			if(SceneObjects.getNearest(PPestcontrol.GANGPLANK_ID)[0] != null){
				if(!PPestcontrol.inArea(2658, 2637, 2665, 2643) 
						&& Players.getMyPlayer().getLocation().getY() > 2630)
						return true;
			}
		}
		//return Players.getMyPlayer().getLocation().getY() == 2639 && Players.getMyPlayer().getLocation().getX() == 2657;
		return false;
	}
	public void execute() {
		//System.out.println("EnterBoat");
		PPestcontrol.praying = false;
		SceneObject[] gangplank = SceneObjects.getNearest(PPestcontrol.GANGPLANK_ID);
		if(gangplank.length > 0){
			if(gangplank[0] != null){
				Menu.sendAction(502, gangplank[0].hashCode(), gangplank[0].getLocalRegionX(), gangplank[0].getLocalRegionY(), PPestcontrol.GANGPLANK_ID, 3);
				Time.sleep(new SleepCondition(){
					public boolean isValid() {
						return PPestcontrol.inArea(2658, 2637, 2665, 2643);
					}
				},6000);
				PPestcontrol.randomizedPath = Random.between(0, 4);
				if(Random.between(0, 2)==1
						&&!PPestcontrol.praying){
						Menu.sendAction(1500, 132, 0, 0,14308,2);
						PPestcontrol.praying = true;
					}
		    	//System.out.println("Randomized path: "+PPestcontrol.randomizedPath+ ":"+PPestcontrol.PORTAL_SIDES[PPestcontrol.randomizedPath]);
			}
		}
		Time.sleep(100);
	}

}
