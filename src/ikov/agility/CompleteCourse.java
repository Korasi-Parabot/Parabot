package ikov.agility;

import com.sun.org.apache.bcel.internal.generic.SWAP;
import javafx.scene.Scene;
import org.parabot.environment.api.utils.Time;
import org.parabot.environment.input.Mouse;
import org.parabot.environment.scripts.framework.Strategy;
import org.rev317.min.api.methods.*;
import org.rev317.min.api.wrappers.*;

import java.util.*;


public class CompleteCourse implements Strategy {

    public static final int ROPESWING = 2282;
    public static final int LOGBALANCE = 2294;

    Area infrontSwing = new Area(new Tile(3005, 3953));

    public boolean activate() {
        SceneObject ropeSwing = getSwing();
        if(ropeSwing != null) {
            if (ropeSwing.distanceTo() < 10) {
                return true;
            }
        }
        return true;
    }

    public void execute() {
        int baseX = Game.getBaseX();
        int baseY = Game.getBaseY();

        System.out.println(baseX + " - " + baseY);
        Time.sleep(2000);

        //ropeswing
        int oldExp = Skill.AGILITY.getExperience();
        try {
            while (Skill.AGILITY.getExperience() == oldExp) {
                SceneObject[] ropeSwing = SceneObjects.getNearest(2282);
                ropeSwing[0].interact(SceneObjects.Option.FIRST);
                Time.sleep(500);
                SWAgility.status = "Swinging...";
                SWAgility.expGained = Skill.AGILITY.getExperience() - SWAgility.startingExp;
                Time.sleep(3000);
            }
        } catch (ArrayIndexOutOfBoundsException | NullPointerException ex) {System.out.println("error occurred for " + SWAgility.status);}
        modCheck();

        //log
        try {
            Collection<SceneObject> log = SceneObjects.getSceneObjectsAtTile(2550-baseX, 3546-baseY, true);
            log.iterator().next().interact(SceneObjects.Option.FIRST);
            Time.sleep(500);
            SWAgility.status = "Walking log..." + Game.getBaseX() + " - " +  Game.getBaseY();
            SWAgility.expGained = Skill.AGILITY.getExperience() - SWAgility.startingExp;
            Time.sleep(4000);

            oldExp = Skill.AGILITY.getExperience();
            while (Skill.AGILITY.getExperience() == oldExp) {
                log.iterator().next().interact(SceneObjects.Option.FIRST);
                Time.sleep(500);
                SWAgility.status = "Walking log...";
                SWAgility.expGained = Skill.AGILITY.getExperience() - SWAgility.startingExp;
                Time.sleep(9000);
            }
        } catch (ArrayIndexOutOfBoundsException | NullPointerException ex) {System.out.println("error occurred for " + SWAgility.status);}
        modCheck();

        //net
        try {
            oldExp = Skill.AGILITY.getExperience();
            while (Skill.AGILITY.getExperience() == oldExp) {
                SceneObject[] net = SceneObjects.getNearest(2284);
                net[0].interact(SceneObjects.Option.FIRST);
                Time.sleep(500);
                SWAgility.status = "Climbing...";
                SWAgility.expGained = Skill.AGILITY.getExperience() - SWAgility.startingExp;
                Time.sleep(3000);
            }
        } catch (ArrayIndexOutOfBoundsException | NullPointerException ex) {System.out.println("error occurred for " + SWAgility.status);}
        modCheck();

        //ledge
        try {
            oldExp = Skill.AGILITY.getExperience();
            while (Skill.AGILITY.getExperience() == oldExp) {
                Collection<SceneObject> ledges = SceneObjects.getSceneObjectsAtTile(2535 - baseX, 3547 - baseY, true);
                SceneObject ledge = (SceneObject) ledges.toArray()[1];
                Menu.sendAction(502, ledge.getHash(), ledge.getLocalRegionX(), ledge.getLocalRegionY(), 2302, 3);
                Time.sleep(500);
                SWAgility.status = "Crossing ledge..." + Game.getBaseX() + " - " + Game.getBaseY();
                SWAgility.expGained = Skill.AGILITY.getExperience() - SWAgility.startingExp;
                Time.sleep(3000);
            }
        } catch (ArrayIndexOutOfBoundsException | NullPointerException ex) {System.out.println("error occurred for " + SWAgility.status);}
        modCheck();

        //ladder
        try {
            SceneObject[] ladder = SceneObjects.getNearest(3205);
            Time.sleep(1000);
            ladder[0].interact(SceneObjects.Option.FIRST);
            Time.sleep(500);
            SWAgility.status = "down...";
            SWAgility.expGained = Skill.AGILITY.getExperience() - SWAgility.startingExp;
            Time.sleep(3000);
        } catch (ArrayIndexOutOfBoundsException | NullPointerException ex) {System.out.println("error occurred for " + SWAgility.status);}
        modCheck();

        try {
            new Tile(2535, 3553).walkTo();
            SWAgility.status = "Walking to walls...";
            Time.sleep(5000);
            //wall1
            Collection<SceneObject> walls = SceneObjects.getSceneObjectsAtTile(2536-baseX, 3553-baseY, true);
            SceneObject wall = (SceneObject)walls.toArray()[0];
            Menu.sendAction(502, wall.getHash(), wall.getLocalRegionX(), wall.getLocalRegionY(), 1948, 3);
            Time.sleep(500);
            SWAgility.status = "Wall jump...";
            SWAgility.expGained = Skill.AGILITY.getExperience() - SWAgility.startingExp;
            Time.sleep(4000);
            //wall 2
            Collection<SceneObject> walls2 = SceneObjects.getSceneObjectsAtTile(2539-baseX, 3553-baseY, true);
            SceneObject wall2 = (SceneObject)walls2.toArray()[0];
            Menu.sendAction(502, wall2.getHash(), wall2.getLocalRegionX(), wall2.getLocalRegionY(), 1948, 3);
            Time.sleep(500);
            SWAgility.status = "Wall jump...";
            SWAgility.expGained = Skill.AGILITY.getExperience() - SWAgility.startingExp;
            Time.sleep(4000);
            //wall 3
            Collection<SceneObject> walls3 = SceneObjects.getSceneObjectsAtTile(2542-baseX, 3553-baseY, true);
            SceneObject wall3 = (SceneObject)walls3.toArray()[0];
            Menu.sendAction(502, wall3.getHash(), wall3.getLocalRegionX(), wall3.getLocalRegionY(), 1948, 3);
            Time.sleep(500);
            SWAgility.status = "Wall jump...";
            SWAgility.expGained = Skill.AGILITY.getExperience() - SWAgility.startingExp;
            Time.sleep(4000);
        } catch (ArrayIndexOutOfBoundsException | NullPointerException ex) {
            System.out.println("error occurred for " + SWAgility.status);
            SceneObject[] ladder = SceneObjects.getNearest(3205);
            ladder[0].interact(SceneObjects.Option.FIRST);
            Time.sleep(500);
            SWAgility.status = "down...";
            SWAgility.expGained = Skill.AGILITY.getExperience() - SWAgility.startingExp;
            Time.sleep(3000);
        }
        modCheck();

        try {
            new Tile(2551, 3554).walkTo();
            SWAgility.status = "Back to start";
            SWAgility.laps++;
            Time.sleep(3000);
        } catch (ArrayIndexOutOfBoundsException | NullPointerException ex) {System.out.println("error occurred for " + SWAgility.status);}
        modCheck();

        System.out.println("lap number: " + SWAgility.laps);
    }

    private SceneObject getSwing(){
        for(SceneObject ropeSwing : SceneObjects.getNearest(ROPESWING)){
            if(ropeSwing != null){
                return ropeSwing;
            }
        }
        return null;
    }


    String[] staffList = new String[] {
            "David",
            "Blade",
            "Galkon",
            "Fergus",
            "Jmml",
            "Supreme",
            "J4mes",
            "Biggy",
            "Dork",
            "Duke",
            "Health Alert",
            "Hell Kid",
            "ImR3dxx",
            "North Carolina",
            "Sufyaan",
            "Tha Chris",
            "xElyStorm",
            "Zum Zum Zen",
            "4ndreas",
            "Financial",
            "Fotis1",
            "Gre partyhat",
            "J A M E S 3",
            "loot n skoot",
            "Tale",
            "K O R A S I"
    };

    public void modCheck() {
        Player[] players = Players.getPlayers();

        for(Player p : players) {

            final String name = p.getName();
            for (String s : staffList) {
                if (name.equals(s)) {
                    //logout
                    System.out.println(s + " present, logging out");
                    Mouse.getInstance().click(749,10,true);
                    Time.sleep(1000);
                    Mouse.getInstance().click(660,373,true);
                }
            }

        }
    }

}