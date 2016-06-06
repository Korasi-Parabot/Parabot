package ikov.dungeoneering;

import org.parabot.environment.api.utils.Time;
import org.parabot.environment.scripts.framework.SleepCondition;
import org.parabot.environment.scripts.framework.Strategy;
import org.rev317.min.api.methods.Inventory;
import org.rev317.min.api.methods.Menu;
import org.rev317.min.api.methods.Players;
import org.rev317.min.api.methods.Walking;
import org.rev317.min.api.wrappers.Tile;

public class GetOrb implements Strategy {

	public boolean activate() {
		return ((PDungeoneering.gotRock
				&& Players.getMyPlayer().getLocation().getY() > 9000))
				&& !PDungeoneering.gotOrb;
	}

	public void execute() {
		//System.out.println("Getting orb");
		if(Players.getMyPlayer().getLocation().getX() < 2565){
			Menu.sendAction(502, 1079596979, 51, 47, 357, 3);//Search crate
			Time.sleep(new SleepCondition(){
				public boolean isValid() {
					return Inventory.contains(PDungeoneering.ORB_ID);
				}
			},7200);
		} else {
			if(Players.getMyPlayer().getLocation().getX() > 2605)
				Walking.walkTo(new Tile(2600,9838));
			else if (Players.getMyPlayer().getLocation().getX() > 2575){
				Walking.walkTo(new Tile(2572,9845));
			} else {
				Walking.walkTo(new Tile(2563, 9849));
			}
			Time.sleep(1200);
		}
		if(Inventory.contains(PDungeoneering.ORB_ID))
			PDungeoneering.gotOrb = true;
	}
	
	/*public boolean canSeeCrate(){
		for(SceneObject obj : SceneObjects.getAllSceneObjects()){
			if(obj.getId() == PDungeoneering.CRATE_ID && obj.getLocation() == PDungeoneering.CRATE_TILE)
				return true;
		}
		return false;
	}*/

}
