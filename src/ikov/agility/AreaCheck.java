package ikov.agility;
import org.parabot.environment.scripts.framework.Strategy;
import org.parabot.environment.api.utils.Time;
import org.rev317.min.api.methods.Menu;
import org.rev317.min.api.methods.SceneObjects;
import org.rev317.min.api.wrappers.Area;
import org.rev317.min.api.wrappers.SceneObject;
import org.rev317.min.api.wrappers.Tile;

public class AreaCheck implements Strategy {

    Area nearRope = new Area(new Tile(3005, 3953));

    public boolean activate() {

        //If RopeSwing isn't within reach, then execute.

        SceneObject ropeSwing = getSwing();
        if(ropeSwing != null) {
            if (ropeSwing.distanceTo() > 10) {
                return false;
            }
        }
        return true;
    }

    public void execute() {

        //Teleport and run back to RopeSwing.

        SWAgility.status = "Teleporting...";
        Menu.sendAction(1107, 1111574190, 46, 13, 2309, 0); //Magic book
        Time.sleep(1000);
        Menu.sendAction(315, 36257792, 53, 1541, 2213, 1); //Skilling
        Time.sleep(2000);
        Menu.sendAction(315, 184, 0, 2498, 2213, 1); //Next
        Time.sleep(2000);
        Menu.sendAction(315, 184, 0, 2496, 2213, 1); //Agility
        Time.sleep(2000);
        Menu.sendAction(315, 107347968, 36, 2497, 6552, 1); //Wild
        Time.sleep(5000);

        SceneObject[] Door = SceneObjects.getNearest(2309);
        Door[0].interact(1);
        SWAgility.status = "Opening door...";
        Time.sleep(12000);
        new Tile(3004, 3937).walkTo();
        SWAgility.status = "Walking to pipe";
        Time.sleep(8000);
        Menu.sendAction(502, 1111237948, 60, 74, 2288, 3); //Pipe
        SWAgility.status = "Climbing pipe...";
        Time.sleep(10000);
        new Tile(3005, 3953).walkTo();
        SWAgility.status = "Walking...";
        Time.sleep(8000);

    }

    private SceneObject getSwing(){
        for(SceneObject ropeSwing : SceneObjects.getNearest(2283)){
            if(ropeSwing != null){
                return ropeSwing;
            }
        }
        return null;
    }

}