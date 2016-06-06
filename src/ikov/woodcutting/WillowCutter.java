package ikov.woodcutting;

import java.util.ArrayList;
import org.parabot.environment.api.utils.Time;
import org.parabot.environment.scripts.Category;
import org.parabot.environment.scripts.Script;
import org.parabot.environment.scripts.ScriptManifest;
import org.parabot.environment.scripts.framework.SleepCondition;
import org.parabot.environment.scripts.framework.Strategy;
import org.rev317.min.api.methods.Bank;
import org.rev317.min.api.methods.GroundItems;
import org.rev317.min.api.methods.Inventory;
import org.rev317.min.api.methods.Players;
import org.rev317.min.api.methods.SceneObjects;
import org.rev317.min.api.wrappers.GroundItem;
import org.rev317.min.api.wrappers.SceneObject;
import org.rev317.min.api.wrappers.Tile;

@ScriptManifest(author = "Tezi", category = Category.WOODCUTTING,
        description = "Chops willows in Draynor and banks them", name = "WillowFucker",
        servers = { "Ikov" }, version = 1.1)

public class WillowCutter extends Script {
    private final ArrayList<Strategy> strategies = new ArrayList<Strategy>();

    int wtrees = 0;
    int count = 0;
    int nests = 0;
    int count2 = 0;
    public boolean onExecute() {

        strategies.add(new Chop()); //Then it will drop, if we put drop first then this would happen.
        strategies.add(new Bankit());

        provide(strategies);
        return true;
    }

    public void onFinish() {
        System.out.println("WillowFucker Made By Tezi");
    }
    public class Chop implements Strategy {

        @Override
        public boolean activate() {
            // TODO Auto-generated method stub
            return !Inventory.isFull();
        }

        @Override
        public void execute() {
            SceneObject[] Tree = SceneObjects.getNearest(5551);
            GroundItem[] NestID = GroundItems.getNearest(5070, 5071, 5072, 5073, 5074);
            if(NestID.length > 0 && NestID != null){
                NestID[0].take();
                Time.sleep(1000);
            }
            if(Tree != null && Tree.length > 0 && Players.getMyPlayer().getAnimation() == -1){
                Time.sleep(500);
                if(SceneObjects.getNearest(5551) != null){
                    Tree[0].interact(SceneObjects.Option.CHOP_DOWN);
                    Time.sleep(new SleepCondition() {
                        @Override
                        public boolean isValid() {
                            return Players.getMyPlayer().getAnimation() != -1;


                        }
                    }, 5000);

                }
            }

        }
    }
    public class Bankit implements Strategy{

        @Override
        public boolean activate() {
            // TODO Auto-generated method stub
            return Inventory.isFull();
        }

        @Override
        public void execute() {

            if(!Players.getMyPlayer().getLocation().equals(new Tile(3092, 3245))){
                new Tile(3092, 3245).walkTo();
                Time.sleep(1000);

            }
            else if(Players.getMyPlayer().getLocation().equals(new Tile(3092, 3245))){
                if(!Bank.isOpen()){
                    SceneObject[] _Bank = SceneObjects.getNearest(2213);
                    _Bank[0].interact(SceneObjects.Option.USE);
                    Time.sleep(1000);
                }
                if(Bank.isOpen()){
                    count = Inventory.getCount(1520);
                    count2 = Inventory.getCount(5070, 5071, 5072, 5073, 5074);
                    wtrees += count;
                    nests += count2;
                    Bank.depositAllExcept(1360, 6740);
                    Time.sleep(100);
                    System.out.println("Logs chopped so far: " + wtrees + "\nNests collected so far: " + nests);
                }
            }

        }


    }

}