package ikov.pestcontrol;

import org.parabot.environment.api.utils.Time;
import org.parabot.environment.scripts.framework.SleepCondition;
import org.parabot.environment.scripts.framework.Strategy;
import org.rev317.min.api.methods.Menu;
import org.rev317.min.api.methods.Npcs;
import org.rev317.min.api.methods.Players;
import org.rev317.min.api.methods.Walking;
//import org.rev317.min.api.wrappers.Player;

public class ToPortal implements Strategy {

	public boolean activate() {
		try{
			return Players.getMyPlayer().getLocation().getY() < 2620 
				&& Npcs.getNearest(PPestcontrol.PORTAL_IDS[PPestcontrol.randomizedPath]).length == 0
				&& PPestcontrol.PORTAL_TILES[PPestcontrol.randomizedPath].distanceTo() > 10;
		}catch(NullPointerException e){
			//System.out.println("This nasty nullpointer kept me busy for a while.^");
		};
		return false;
		}

	public void execute() {
		//System.out.println("ToPortal");
		if(!PPestcontrol.praying){
			Menu.sendAction(1500, 132, 0, 0,14308,2);
			PPestcontrol.praying = true;
		}
		if(Players.getMyPlayer().getLocation().getY() > 2610){
			Walking.walkTo(PPestcontrol.CENTER_TILE);
			Time.sleep(new SleepCondition(){
				public boolean isValid() {
					return Players.getMyPlayer().getLocation().getY() < 2595;
				}
			},6000);
		} else {
			/*int totalX = 0;
			int totalY = 0;
			for(Player p: Players.getPlayers()){
				totalX += p.getLocation().getX();
				totalY += p.getLocation().getY();
			}
			int avgX = totalX/Players.getPlayers().length;
			int avgY = totalY/Players.getPlayers().length;
			System.out.println("Average Player location: "+avgX +":"+avgY);*/
			Walking.walkTo(PPestcontrol.PORTAL_TILES[PPestcontrol.randomizedPath]);
			Time.sleep(new SleepCondition(){
				public boolean isValid() {
					return Npcs.getNearest(PPestcontrol.PORTAL_IDS[PPestcontrol.randomizedPath]).length > 0;
				}
			},6000);
			//System.out.println("Succesfully finished ToPortal");
		}
		Time.sleep(200);
	}

}
