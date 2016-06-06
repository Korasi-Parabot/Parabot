package ikov.dungeoneering;

import org.parabot.environment.api.utils.Time;
import org.parabot.environment.scripts.framework.SleepCondition;
import org.parabot.environment.scripts.framework.Strategy;
import org.rev317.min.api.methods.Game;
import org.rev317.min.api.methods.Menu;
import org.rev317.min.api.methods.Npcs;

public class StartDungeon implements Strategy {

	public boolean activate() {
		return Npcs.getNearest(PDungeoneering.THOK_ID).length > 0 || Game.getOpenBackDialogId() == 2469 ; 
	}

	public void execute() {
		//System.out.println("Starting dungeon");
		PDungeoneering.gotRock = false;
		PDungeoneering.gotOrb = false;
		PDungeoneering.equipped = false;
		if(Game.getOpenBackDialogId() == 2469){
			Menu.sendAction(315, 794558464, 55, 2472, 48496, 1);//Second option dung chatbox
			Time.sleep(new SleepCondition(){
				public boolean isValid() {
					return Npcs.getNearest(PDungeoneering.THOK_ID).length == 0;
				}
			},1200);
			Time.sleep(3600);
		} else {
			Menu.sendAction(225, 1273, 0, 0, 48496, 4);//open start dung interface
			Time.sleep(new SleepCondition(){
				public boolean isValid() {
					return Game.getOpenBackDialogId() == 2469;
				}
			},6000);
		}
	}

}
