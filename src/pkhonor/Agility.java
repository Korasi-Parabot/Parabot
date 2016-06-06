package pkhonor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.parabot.core.Context;
import org.parabot.environment.api.interfaces.Paintable;
import org.parabot.environment.input.Keyboard;
import org.parabot.environment.input.Mouse;
import org.parabot.environment.scripts.Script;
import org.parabot.environment.scripts.ScriptManifest;
import org.parabot.environment.scripts.framework.LoopTask;
import org.parabot.environment.scripts.Category;
import org.rev317.min.api.events.MessageEvent;
import org.rev317.min.api.events.listeners.MessageListener;
import org.rev317.min.api.methods.Game;
import org.rev317.min.api.methods.Inventory;
import org.rev317.min.api.methods.Menu;
import org.rev317.min.api.methods.Players;
import org.rev317.min.api.methods.SceneObjects;
import org.rev317.min.api.methods.Skill;
import org.rev317.min.api.wrappers.Item;
import org.rev317.min.api.wrappers.Player;
import org.rev317.min.api.wrappers.SceneObject;
import org.rev317.min.api.wrappers.Tile;

@ScriptManifest(author = "Fatboy", category = Category.AGILITY, description = "Trains agility for both the normal course and wildy course.", name = "Agility", servers = { "PkHonor" }, version = 2.0)
public class Agility extends Script implements Paintable, LoopTask,
        MessageListener {
    enum State {
        IDLE, agility, tele, log, deposit, teleHome, stuck,
    }
    private State state = State.IDLE;
    private Methods m = new Methods();
    public int coins = 996;
    private int currentXp;
    private long homeTimer = 0;

    public boolean onExecute() {
        System.out.println("enter execute");
        Context.getInstance().getRandomHandler().clearActiveRandoms();
        m.startTime = System.currentTimeMillis();
        m.startXp = Skill.getCurrentExperience(m.agility);
        lastXp = Skill.getCurrentExperience(m.agility);
        gainAt = System.currentTimeMillis();
        return true;
    }

    public void getStatus() {
        try {
//            if (!m.runOn()) {
//                m.toggleRun();
//                return;
//            }
//            if (m.hasItem(coins)) {
//                state = State.deposit;
//                return;
//            }
            if (!m.atAgility() && m.getLevel(m.agility) < 70
                    || !m.atWildAgility() && m.getLevel(m.agility) >= 70) {
                state = State.tele;
                return;
            }
            // if (upstairs()) {
            // state = State.tele;
            // return;
            // }

            // if (relog) {
            // state = State.log;
            // return;
            // }

            state = State.agility;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleState() {
        System.out.println("handling state: " + state);
        try {

            if (state == State.tele) {
                tele();
                return;
            }

            if (state == State.log) {
                log();
                return;
            }

            if (state == State.deposit) {
                m.deposit(coins);
                return;
            }

            agility();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void teleHome() {
        if (System.currentTimeMillis() - homeTimer < 300000)
            return;

        Keyboard.getInstance().sendKeys("::home");
        homeTimer = System.currentTimeMillis();
        sleep(1000);
    }

    public boolean agilityDone() {
        if (m.getLevel(m.agility) < 99)
            return false;

        if (m.atWildAgility())
            return false;

        return true;
    }

    public void tele() {
        if (m.getLevel(m.agility) >= 70) {
            teleWild();
            return;
        }

        m.teleSkilling("agility");
    }

    public void teleWild() {
        if (!m.atWildAgility() && !m.atEdge()) {
            m.teleShops();
            return;
        }

        if (Game.getOpenBackDialogId() == 2492) {
            Menu.sendAction(315, 0, 0, 2497);
            sleep(750);
            Menu.sendAction(315, 0, 0, 2497);
            return;
        }

        // gainAt = System.currentTimeMillis();
        // stuck = false;
        m.interactO(6282, 0);

    }

    public void login() {
        Mouse.getInstance().click(new Point(365, 300), true);
        System.out.println("Logging in...");
        sleep(1000);
    }

    public void log() {
        if (m.isLoggedIn()) {
            m.logout();
        }
    }

    public boolean upstairs() {
        final Player me = Players.getMyPlayer();
        if (me.getLocation().getY() <= 3424) {
            return true;
        }

        return false;
    }

    public int normalID() {
        final Player me = Players.getMyPlayer();

        if (Game.getPlane() == 2) {
            if (me.getLocation().getX() <= 2477)
                return 2312;
            else
                return 2314;
        }
        if (Game.getPlane() == 1)
            return 2313;
        if (me.getLocation().getX() >= 2483 && me.getLocation().getY() <= 3431) {
            if (me.getLocation().getY() <= 3425)
                return 2286;
            else
                return 154;
        }
        if (me.getLocation().getY() < 3431)
            return 2285;

        return 2295;
    }

    public void agility() {

        if (m.getLevel(m.agility) >= 70) {
            agilityWild();
            return;
        }

        agilityNormal();

    }

    public void agilityNormal() {
        // if (m.animating())
        // return;

        for (SceneObject s : SceneObjects.getAllSceneObjects()) {
            if (s.getLocation().distanceTo() <= 20) {
                if (s.getId() == normalID()) {
                    s.interact(0);
                    sleep(1000);
                    return;
                }
            }
        }
    }

    public void agilityWild() {
        if (!inCourse()) {
            enterCourse();
            return;
        }

        if (doingAction())
            return;

        if (m.animId() > 0)
            return;

        // if (m.isMoving())
        // return;

        // if (wildID() == 2328)
        // System.out.println(wildID());

        for (SceneObject s : SceneObjects.getAllSceneObjects()) {
            if (s.getLocation().distanceTo() <= 15) {
                if (s.getId() == wildID()) {
                    // sleep(750);
                    s.interact(index());
                    sleep(2000);
                    return;
                }
            }
        }
    }

    boolean doingAction() {
        final Tile me = Players.getMyPlayer().getLocation();

        if (me.getY() >= 3938 && me.getY() <= 3949) {// pipe
            if (me.getX() == 3004)
                return true;
        }

        // swing
        if (me.getY() >= 3954 && me.getY() <= 3957) {
            if (me.getX() == 3005)
                return true;
        }

        // lava
        if (me.getX() >= 2997 && me.getX() <= 3001) {
            if (me.getY() == 3960)
                return true;
        }

        // log
        if (me.getX() >= 2995 && me.getX() <= 3001) {
            if (me.getY() == 3945)
                return true;
        }

        return false;
    }

    public int index() {
        final Tile me = Players.getMyPlayer().getLocation();

        return 0;
    }

    public int wildID() {

        final Tile me = Players.getMyPlayer().getLocation();

        if (me.getY() < 3934 || me.getX() >= 2997 && me.getY() <= 3938)
            return 2288;

        if (me.getX() >= 3003 && me.getY() <= 3953)
            return 2283;

        if (me.getX() >= 3002 && me.getY() >= 3958)
            return 2311;

        if (me.getX() <= 3002 && me.getX() > 2995 && me.getY() >= 3944)
            return 2297;

        return 2328;
    }

    public boolean inCourse() {
        final Player me = Players.getMyPlayer();

        if (me.getLocation().getY() < 3917)
            return false;

        return true;
    }

    public void enterCourse() {
        m.interactO(2309, 0);
    }

    @Override
    public void paint(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        g.setColor(Color.WHITE);

        final Player me = Players.getMyPlayer();

        // g.drawString("pray?: " + usingPrayer() + "", 595, 350);
        g.drawString("State: " + state + "", 595, 390);
        g.drawString("Loc: " + me.getLocation() + "", 595, 410);
        g.drawString("xp/hr: " + m.perHour(m.xpGained(m.agility)) + "", 595,
                430);
        g.drawString("Last gain: " + lastGain() + "", 595, 450);
        // TODO Auto-generated method stub

    }

    long timeLeft() {
        return 60 - ((System.currentTimeMillis() - logAt) / 1000);
    }

    long lastGain() {
        return (System.currentTimeMillis() - gainAt) / 1000;
    }

    long logAt;
    int lastXp;
    long gainAt;

    @Override
    public int loop() {
        try {
            if (m.isLoggedIn()) {
                currentXp = Skill.getCurrentExperience(m.agility);
                if (currentXp != lastXp) {
                    lastXp = Skill.getCurrentExperience(m.agility);
                    gainAt = System.currentTimeMillis();
                    System.out.println("Updated xp");
                }
            }

            if (!m.isLoggedIn()) {
                m.login(4000);
                return 0;
            }

            if (m.sentAt > 0) {
                if (System.currentTimeMillis() - m.sentAt > 30000) {
                    m.sentAt = 0;
                }
            }

            if (m.rTimer > 0) {
                if (System.currentTimeMillis() - m.rTimer > 30000) {
                    m.rTimer = 0;
                }
            }

            // m.printAllPrayersWithin();
            // m.showIds();

            for (Item i : Inventory.getItems()) {
                if (i.getId() == 6963 || i.getId() == 5313
                        || i.getId() == m.jailOre && !m.atJail()) {
                    Menu.sendAction(847, i.getId() - 1, i.getSlot(), 3214);
                    sleep(1000);
                }

            }

            if (m.atJail()) {
                m.handleJail();
                return 0;
            }

            if (m.inRandom()) {
                m.handleRandom();
                return 0;

            }

            m.extras();

            getStatus();
            handleState();

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    @Override
    public void messageReceived(MessageEvent arg0) {
        // TODO Auto-generated method stub
        m.message = arg0.getMessage();

    }
}