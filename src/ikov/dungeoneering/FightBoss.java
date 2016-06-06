package ikov.dungeoneering;

import org.parabot.environment.api.utils.Time;
import org.parabot.environment.scripts.framework.SleepCondition;
import org.parabot.environment.scripts.framework.Strategy;
import org.rev317.min.api.methods.Menu;
import org.rev317.min.api.methods.Npcs;
import org.rev317.min.api.wrappers.Npc;

public class FightBoss implements Strategy {
	private int nulledBossCheck = 0;
	
	public boolean activate() {
		for(int i = 0; i < PDungeoneering.BOSS_IDS.length; i++){
			if (Npcs.getNearest(PDungeoneering.BOSS_IDS[i]) != null){
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public void execute() {
		//System.out.println("Fighting boss");
		try{
			//if(Npcs.getNearest().length > 0)
			//	System.out.println("Nearest NPC: "+Npcs.getNearest()[0].getLocation());
			for(int i = 0; i < PDungeoneering.BOSS_IDS.length; i++){
				if (Npcs.getNearest(PDungeoneering.BOSS_IDS)[i] != null){
					final Npc boss = Npcs.getNearest(PDungeoneering.BOSS_IDS)[i];
					if(!boss.isInCombat()){
						boss.interact(1);
						Time.sleep(new SleepCondition(){
							public boolean isValid() {
								return boss.isInCombat() || Npcs.getNearest(PDungeoneering.THOK_ID).length > 0;
							}
						},3600);
					}
					nulledBossCheck = 0;
					//System.out.println("Boss: "+i+" ("+PDungeoneering.BOSS_IDS[i]+ ") "+boss.getLocation()+" animation: "+boss.getAnimation() + "  Health: "+boss.getHealth()+"%");
				}
			}
		}catch(Exception e){
			if(e.getMessage().contains("0")){
				//System.out.println("Possible nulled boss? ArrayIndex 0 error");
				nulledBossCheck ++;
			}
			Time.sleep(600);
		}
		if(nulledBossCheck > 10){
			nulledBossCheck = 0;
			System.out.println("BOSS NULLED - QUITTING DUNGEON");
			Menu.sendAction(315, 5832704, 493, 16035, 356, 1);
			Time.sleep(600);
		}
	}
}
