package ikov.pestcontrol;

import org.parabot.environment.api.utils.Time;
import org.parabot.environment.scripts.framework.SleepCondition;
import org.parabot.environment.scripts.framework.Strategy;
import org.rev317.min.api.methods.Npcs;
import org.rev317.min.api.methods.Players;

public class AttackPortal implements Strategy {

	public boolean activate() {
		if(PPestcontrol.PORTAL_TILES[PPestcontrol.randomizedPath] != null){
			return PPestcontrol.PORTAL_TILES[PPestcontrol.randomizedPath].distanceTo() < 20
					&& !Players.getMyPlayer().isInCombat();
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public void execute() {
		//System.out.println("AttackPortal");
		if(Npcs.getNearest(PPestcontrol.PORTAL_IDS[PPestcontrol.randomizedPath]).length > 0){
			//System.out.println("Finding Portal");
			if(Npcs.getNearest(PPestcontrol.PORTAL_IDS[PPestcontrol.randomizedPath])[0] != null){
				//System.out.println("Portal not null");
				Npcs.getNearest(PPestcontrol.PORTAL_IDS[PPestcontrol.randomizedPath])[0].interact(1);
				Time.sleep(new SleepCondition(){
					public boolean isValid() {
						return Players.getMyPlayer().isInCombat()
								|| Npcs.getNearest(PPestcontrol.PORTAL_IDS[PPestcontrol.randomizedPath]).length == 0
								|| Players.getMyPlayer().getY() > 2620;
					}
				},6000);
			}
		} else {
			//System.out.println("Finding Brawler");
			if(Npcs.getNearest(PPestcontrol.BRAWLER_ID).length > 0){
				if(Npcs.getNearest(PPestcontrol.BRAWLER_ID)[0] != null){
					//System.out.println("Brawler not null");
					Npcs.getNearest(PPestcontrol.BRAWLER_ID)[0].interact(1);
					//System.out.println("Interacting with brawler");
					Time.sleep(new SleepCondition(){
						public boolean isValid() {
							return Players.getMyPlayer().isInCombat()
									|| Players.getMyPlayer().getY() > 2620;
						}
					},6000);
				}
			}
		}
		//System.out.println("Succesfully finished AttackPortal");
		Time.sleep(200);
	}

}
