package pkhonor.fishing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.parabot.environment.api.interfaces.Paintable;
import org.parabot.environment.api.utils.Random;
import org.parabot.environment.api.utils.Time;
import org.parabot.environment.api.utils.Timer;
import org.parabot.environment.scripts.Category;
import org.parabot.environment.scripts.Script;
import org.parabot.environment.scripts.ScriptManifest;
import org.parabot.environment.scripts.framework.SleepCondition;
import org.parabot.environment.scripts.framework.Strategy;
import org.rev317.min.Loader;
import org.rev317.min.api.methods.Game;
import org.rev317.min.api.methods.Inventory;
import org.rev317.min.api.methods.Menu;
import org.rev317.min.api.methods.Npcs;
import org.rev317.min.api.methods.Players;
import org.rev317.min.api.methods.SceneObjects;
import org.rev317.min.api.methods.Skill;
import org.rev317.min.api.wrappers.Area;
import org.rev317.min.api.wrappers.Item;
import org.rev317.min.api.wrappers.Npc;
import org.rev317.min.api.wrappers.SceneObject;
import org.rev317.min.api.wrappers.Tile;

@ScriptManifest(
        author = "Random (Kendal), edited by Korasi",
        category = Category.FISHING,
        description = "Fishes ALL Fish in PkHonor. You can start with the tools in your inventory or bank. Allows Banking and Power-Fishing. Functional in both Catherby and the Premium Skilling Zone. Also supports Progressive Leveling, which can be used for building new accounts (keep in mind that you still need the tools in your inventory/bank).",
        name = "RDM Fisher",
        servers = { "PkHonor" },
        version = 0.13
)

public class RDMFisher extends Script implements Paintable {

    private final ArrayList<Strategy> Strategies = new ArrayList<Strategy>();
    private Timer scriptTimer = new Timer();
    private ScriptManifest Manifest = (ScriptManifest) RDMFisher.class.getAnnotation(ScriptManifest.class);

    private static Image backgroundIMG;

    DecimalFormat formatter = new DecimalFormat("#,###,###,###");

    private static int FISHING_ROD = 308;
    private static int LOBSTER_POT = 302;
    private static int SMALL_FISHING_NET = 304;
    private static int HARPOON = 312;
    private static int LIVING_MINERALS = 14619;

    private static final Fish[] FISH = {Fish.SHRIMP, Fish.TUNA, Fish.LOBSTER, Fish.BASS, Fish.SWORDFISH, Fish.MONKFISH, Fish.SHARK, Fish.MANTA_RAY, Fish.ROCKTAIL};
    private static final int[] FISH_IDS = { 318, 360, 378, 364, 372, 7945, 384, 390, 14616 };
    private enum Fish {
        SHRIMP("Shrimp", 1, SMALL_FISHING_NET, 316, 0, getImage("http://vignette2.wikia.nocookie.net/2007scape/images/d/dc/Raw_shrimps.png")),
        TUNA("Tuna", 35, HARPOON, 324, 2, getImage("http://vignette3.wikia.nocookie.net/2007scape/images/b/b0/Raw_tuna.png")),
        LOBSTER("Lobster", 40, LOBSTER_POT, 324, 0, getImage("http://vignette2.wikia.nocookie.net/2007scape/images/0/00/Raw_lobster.png")),
        BASS("Bass", 49, SMALL_FISHING_NET, 326, 2, getImage("http://vignette3.wikia.nocookie.net/2007scape/images/6/66/Raw_bass.png")),
        SWORDFISH("Swordfish", 50, HARPOON, 324, 2, getImage("http://vignette2.wikia.nocookie.net/2007scape/images/4/4d/Raw_swordfish.png")),
        MONKFISH("Monkfish", 62, SMALL_FISHING_NET, 326, 0, getImage("http://vignette1.wikia.nocookie.net/2007scape/images/7/70/Raw_monkfish.png")),
        SHARK("Shark", 76, HARPOON, 334, 2, getImage("http://vignette2.wikia.nocookie.net/2007scape/images/1/1f/Raw_shark.png")),
        MANTA_RAY("Manta Ray", 81, SMALL_FISHING_NET, 334, 0, getImage("http://vignette2.wikia.nocookie.net/2007scape/images/7/70/Raw_manta_ray.png")),
        ROCKTAIL("Rocktail", 93, FISHING_ROD, 315, 0, getImage("http://i.imgur.com/8ygr6AW.png"));

        private final String name;
        private final int reqLevel;
        private final int reqItem;
        private final int fishingSpotID;
        private final int actionIndex;
        private Image itemPicture;

        Fish(String name, int reqLevel, int reqItem, int fishingSpotID, int actionIndex, Image itemPicture) {
            this.name = name;
            this.reqLevel = reqLevel;
            this.reqItem = reqItem;
            this.fishingSpotID = fishingSpotID;
            this.actionIndex = actionIndex;
            this.itemPicture = itemPicture;
        }
    }


    private Area CATHERBY = new Area(new Tile(2799, 3430), new Tile(2844, 3452));
    private Area PREMIUM_SKILLING = new Area(new Tile(2280, 3139), new Tile(2297, 3159));

    private static int DEPOSIT_ALL = 432;
    private static int WITHDRAW_1 = 632;
    private static int WITHDRAW_ALL_BUT_ONE = 432;
    private static int BANK_INTERFACE = 23350;

    boolean startScript = false;
    int index = 0;
    boolean ProgressOnLevel = false;
    String FishingLocation = "";
    boolean useBank = true;

    int startExperience = Skill.FISHING.getExperience();

    int[] bankedFish = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    @Override
    public boolean onExecute() {
        backgroundIMG = getImage("http://i.imgur.com/cciyTiy.png");

        GUI g = new GUI();
        while(g.isVisible()){
            Time.sleep(100);}

        if(startScript == false)
            return false;

        if(ProgressOnLevel) {
            index = 0;
            Strategies.add(new ProgressLevel());
        }

        if(FISH[index].reqLevel > Skill.FISHING.getLevel()) {
            System.out.println("You need a fishing level of " + FISH[index].reqLevel + " to fish " + FISH[index].name + ".");
            return false;
        }

        if(FISH[index] == Fish.ROCKTAIL && getPlayerRank() < 20) {
            System.out.println("You need to be a Premium Player to catch Rocktails.");
            return false;
        }

        Strategies.add(new GetEquipment());
        Strategies.add(new DropFish());
        Strategies.add(new StoreFish());
        Strategies.add(new FishSpot());
        Strategies.add(new OpenBank());

        while (ProgressOnLevel && index + 1 < FISH.length - 1 && FISH[index + 1].reqLevel <= Skill.FISHING.getLevel())
            index++;

        System.out.println("Now fishing " + FISH[index].name + ".");
        System.out.println("Fishing location: " + FishingLocation + ".");
        System.out.println("Your rank: " + getPlayerRank() + ".");
        Menu.sendAction(679, 383, 13, 6261);
        provide(Strategies);
        return true;
    }

    @Override
    public void onFinish() {
        for(int index = 0; index < FISH_IDS.length; index++) {
            Item[] Fish = Inventory.getItems(FISH_IDS[index]);
            if(Fish.length > 0 && Fish != null) {
                try {
                    bankedFish[index] += Fish.length;
                } catch(Exception _e) {}
            }
        }

        System.out.println("=== " + Manifest.name() + " v" + Manifest.version() + " ===");
        System.out.println("Ran for: " + scriptTimer.toString());
        System.out.println("Experience gained: " + formatter.format((Skill.FISHING.getExperience() - startExperience)) + " (" + formatter.format(scriptTimer.getPerHour((Skill.FISHING.getExperience() - startExperience))) + " XP/HR)");
        for(int fish = 0; fish < bankedFish.length; fish++) {
            if(bankedFish[fish] > 0) {
                if(fish <= 8)
                    System.out.println(formatter.format(bankedFish[fish]) + "x " + FISH[fish].name + " (" + formatter.format(scriptTimer.getPerHour(bankedFish[fish])) + " Fish/PH)");
            }
        }
    }

    public class GetEquipment implements Strategy {
        @Override
        public boolean activate() {
            if(Game.getOpenInterfaceId() == BANK_INTERFACE && (!gotRequiredItems() || (FISH[index] == Fish.ROCKTAIL && Inventory.getCount(LIVING_MINERALS) == 0)))
                return true;
            return false;
        }

        @Override
        public void execute() {
            Time.sleep(500);
            int[] bankIds = Loader.getClient().getInterfaceCache()[5382].getItems();
            for (int i = 0; i < bankIds.length; i++) {
                if(bankIds[i] == FISH[index].reqItem && Inventory.getCount(FISH[index].reqItem) == 0) {
                    Menu.sendAction(WITHDRAW_1, bankIds[i] - 1, i, 5382);
                    Time.sleep(500);
                    return;
                } else if(bankIds[i] == LIVING_MINERALS && Inventory.getCount(LIVING_MINERALS) == 0) {
                    Menu.sendAction(WITHDRAW_ALL_BUT_ONE, bankIds[i] - 1, i, 5382);
                    Time.sleep(500);
                    return;
                }
            }
            Time.sleep(new SleepCondition() {
                @Override
                public boolean isValid() {
                    return (gotRequiredItems());
                }
            }, 1000);
            if(!gotRequiredItems()) {
                System.out.println("You don't have the required tools available.");
                Time.sleep(new SleepCondition() {
                    @Override
                    public boolean isValid() {
                        return (gotRequiredItems());
                    }
                }, 5000);
            }
        }
    }

    public class ProgressLevel implements Strategy {
        @Override
        public boolean activate() {
            if(ProgressOnLevel) {
                if(index + 1 < FISH.length - 1) {
                    if(FISH[index + 1].reqLevel <= Skill.FISHING.getLevel()) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void execute() {
            index++;
            System.out.println("Moved onwards to the next fish!");
            System.out.println("Now fishing " + FISH[index].name + ".");
            Menu.sendAction(679, 383, 13, 6261);
        }
    }

    public class DropFish implements Strategy {
        @Override
        public boolean activate() {
            if(!useBank) {
                if(Inventory.getCount(FISH[index].reqItem) > 0 && Inventory.getCount() != 28
                        && (Players.getMyPlayer().getAnimation() != -1 && Players.getMyPlayer().getAnimation() != 1353)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void execute() {
            Item[] DroppableFish = Inventory.getItems(FISH_IDS);
            for(Item DropFish : DroppableFish) {
                try {
                    bankedFish[find(FISH_IDS, DropFish.getId())]++;
                } catch (Exception _e) {}
                DropFish.drop();
                Time.sleep(200);
            }
            Time.sleep(2000);
        }
    }

    public int find(int[] array, int value) {
        for(int i = 0; i < array.length; i++)
            if(array[i] == value)
                return i;
        return -1;
    }

    public class FishSpot implements Strategy {
        Npc[] FishingSpot;
        @Override
        public boolean activate() {
            if(Inventory.getCount(FISH[index].reqItem) > 0 && Inventory.getCount() != 28
                    && (Players.getMyPlayer().getAnimation() == -1 || Players.getMyPlayer().getAnimation() == 1353)) {
                return true;
            }
            return false;
        }

        @Override
        public void execute() {
            if(inArea(CATHERBY, Players.getMyPlayer().getLocation()) && FishingLocation != "Premium Zone" || inArea(PREMIUM_SKILLING, Players.getMyPlayer().getLocation()) && FishingLocation == "Premium Zone") {
                FishingSpot = Npcs.getNearest(FISH[index].fishingSpotID);
                enableRun();
                if(FishingSpot.length > 0 && FishingSpot != null) {
                    try {
                        FishingSpot[0].interact(FISH[index].actionIndex);
                        Time.sleep(new SleepCondition() {
                            @Override
                            public boolean isValid() {
                                return (Players.getMyPlayer().getAnimation() != -1);
                            }
                        }, FishingSpot[0].distanceTo() * 400 + 600);
                    } catch (Exception _e) {}
                } else {

                    /** Disclaimer: **
                     *  This is possibly the dirtiest fix I've ever done for
                     *  ANY problem that occured; the Walking class is not
                     *  supported atm, so I had to think of a solution in which
                     *  I was able to run to the fishing spots.
                     *
                     *  I did it by interacting with a f*cking boulder.
                     *  Yeah, it atleast works so idc.
                     */

                    SceneObject[] Boulders = SceneObjects.getNearest(444);
                    if(Boulders.length > 0) {
                        try {
                            for(SceneObject Boulder : Boulders) {
                                if(inArea(CATHERBY, Boulder.getLocation())) {
                                    Boulder.interact(0);
                                    Time.sleep(Boulder.distanceTo() * 250 + 400);
                                    return;
                                }
                            }
                        } catch (Exception _e) {}
                    } else {
                        TeleportToFishing();
                    }
                }
            } else {
                TeleportToFishing();
            }
        }
    }

    public class OpenBank implements Strategy {
        SceneObject[] BankBooths;
        @Override
        public boolean activate() {
            if(Game.getOpenInterfaceId() != BANK_INTERFACE && ((Inventory.getCount() == 28 && useBank) || !gotRequiredItems())) {
                return true;
            }
            return false;
        }

        @Override
        public void execute() {
            BankBooths = SceneObjects.getNearest(2213);
            enableRun();
            if(BankBooths.length > 0 && BankBooths != null) {
                try {
                    int rdmIndex;
                    if((FishingLocation != "Premium Zone" && FISH[index] != Fish.MONKFISH) || BankBooths[0].distanceTo() > 2)
                        rdmIndex = Random.between(0, 100) % BankBooths.length;
                    else
                        rdmIndex = 0;

                    BankBooths[rdmIndex].interact(0);
                    Time.sleep(new SleepCondition() {
                        @Override
                        public boolean isValid() {
                            return (Game.getOpenInterfaceId() == BANK_INTERFACE);
                        }
                    }, BankBooths[rdmIndex].distanceTo() * 300 + 600);
                } catch (Exception _e) {}
            } else {
                TeleportToFishing();
            }
        }
    }

    public void TeleportToFishing() {
        if(FishingLocation == "Premium Zone") {
            Menu.clickButton(7455);
            Time.sleep(50);
            Menu.clickButton(2473);
            Time.sleep(3000);
        } else {
            Menu.clickButton(1170);
            Time.sleep(50);
            Menu.clickButton(2498);
            Time.sleep(50);
            Menu.clickButton(2494);
            Time.sleep(3000);
        }
    }

    public boolean gotRequiredItems() {
        if(Inventory.getCount(FISH[index].reqItem) > 0 && FISH[index] != Fish.ROCKTAIL) {
            return true;
        } else if((Inventory.getCount(FISH[index].reqItem) > 0) && (FISH[index] == Fish.ROCKTAIL)
                && (Inventory.getCount(LIVING_MINERALS) > 0)) {
            return true;
        }
        return false;
    }

    public static int getPlayerRank() {
        try
        {
            // Get declared field: if-statement above "::region", checking for <= 45

            Field client = Loader.getClient().getClass().getDeclaredField("ab");
            client.setAccessible(true);

            return client.getInt(Loader.getClient());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean inArea(Area givenArea, Tile givenTile) {
        Tile leftUpper = givenArea.getPoints()[0];
        Tile rightBottom = givenArea.getPoints()[1];
        if(givenTile.getX() > leftUpper.getX() && givenTile.getX() < rightBottom.getX()
                && givenTile.getY() > leftUpper.getY() && givenTile.getY() < rightBottom.getY())
            return true;
        return false;
    }

    public class StoreFish implements Strategy {
        @Override
        public boolean activate() {
            if(Game.getOpenInterfaceId() == BANK_INTERFACE && (Inventory.getCount(FISH_IDS) > 0 && useBank)) {
                return true;
            }
            return false;
        }

        @Override
        public void execute() {
            for(int index = 0; index < FISH_IDS.length; index++) {
                Item[] Fish = Inventory.getItems(FISH_IDS[index]);
                if(Fish.length > 0 && Fish != null) {
                    try {
                        bankedFish[index] += Fish.length;
                        Menu.sendAction(DEPOSIT_ALL, Fish[0].getId() - 1, Fish[0].getSlot(), 5064);
                        Time.sleep(200);
                    } catch(Exception _e) {}
                }
            }
            Time.sleep(new SleepCondition() {
                @Override
                public boolean isValid() {
                    return (Inventory.getCount(FISH_IDS) == 0);
                }
            }, 1000);
        }
    }

    public static boolean isRunEnabled() {
        try
        {
            // Get declared field: "Toggle run mode On"

            Field client = Loader.getClient().getClass().getDeclaredField("Q");
            client.setAccessible(true);
            return client.getBoolean(Loader.getClient());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static void enableRun() {
        if(isRunEnabled() == false) {
            Menu.sendAction(1050, 379, 15, 3214, 2);
            Time.sleep(new SleepCondition() {
                @Override
                public boolean isValid() {
                    return (isRunEnabled());
                }
            }, 1000);
        }
    }

    @Override
    public void paint(Graphics Graphs) {
        if(startScript) {
            Graphs.drawImage(backgroundIMG, 400, 5, null);

            Graphics2D g = (Graphics2D) Graphs;
            g.setColor(Color.WHITE);
            g.drawString(Manifest.name(), 420, 20);
            g.drawString("Runtime: " + scriptTimer.toString(), 404, 36);
            g.drawString("XP:", 405, 52);
            g.drawString("P/H:", 404, 64);

            g.drawString(formatter.format((Skill.FISHING.getExperience() - startExperience)), 434, 52);
            g.drawString(formatter.format(scriptTimer.getPerHour((Skill.FISHING.getExperience() - startExperience))), 434, 64);

            g.setColor(new Color(0, 0, 0, 128));
            g.fillRect(468, 76, 44, 45);
            g.setColor(Color.black);
            g.drawLine(468, 76, 511, 76);
            g.drawLine(468, 76, 468, 120);
            g.drawLine(468, 121, 511, 121);
            g.drawLine(512, 76, 512, 120);

            Graphs.drawImage(FISH[index].itemPicture, 478, 88, null);
            g.setColor(Color.black);
            int fishAmount;
            if(useBank) {
                fishAmount = (bankedFish[index] + Inventory.getCount(FISH_IDS[index]));
            } else {
                fishAmount = bankedFish[index];
            }
            g.drawString("x" + fishAmount, 473, 89);
            g.setColor(new Color(255, 152, 31));
            g.drawString("x" + fishAmount, 472, 88);
        }
    }

    public static Image getImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e) {
            return null;
        }
    }

    public class GUI extends JFrame implements ActionListener {
        private static final long serialVersionUID = 7519153641069525353L;

        private JPanel contentPane;

        JCheckBox chckbxUseBank;
        JButton btnStart;
        JComboBox<String> comboBox;
        JComboBox<String> cbBoxLocation;
        JCheckBox chckbxProgressiveLeveling;
        JLabel lblSelectYourFish;

        String[] FishPremium = new String[FISH.length - 5];
        String[] FishCatherby = new String[FISH.length];

        public GUI() {
            for(int id = 0; id < FISH.length; id++) {
                FishCatherby[id] = FISH[id].name;
            }
            for(int id = 5; id < FISH.length; id++) {
                FishPremium[id - 5] = FISH[id].name;
            }

            setTitle("RDM Fisher");
            setResizable(false);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setBounds(100, 100, 247, 291);
            contentPane = new JPanel();
            contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
            setContentPane(contentPane);
            contentPane.setLayout(null);

            btnStart = new JButton("Start!");
            btnStart.setBounds(66, 185, 107, 46);
            contentPane.add(btnStart);
            btnStart.addActionListener(this);

            comboBox = new JComboBox<>(FishCatherby);
            comboBox.setBounds(49, 60, 139, 20);
            contentPane.add(comboBox);

            chckbxUseBank = new JCheckBox("Use Bank");
            chckbxUseBank.setSelected(true);
            chckbxUseBank.setBounds(81, 137, 69, 23);
            contentPane.add(chckbxUseBank);

            lblSelectYourFish = new JLabel("Select your fish:");
            lblSelectYourFish.setHorizontalAlignment(SwingConstants.CENTER);
            lblSelectYourFish.setBounds(67, 45, 107, 14);
            contentPane.add(lblSelectYourFish);

            cbBoxLocation = new JComboBox<>(new String[]{"Catherby", "Premium Zone"});
            cbBoxLocation.setBounds(49, 106, 139, 20);
            if(getPlayerRank() < 20)
                cbBoxLocation.setEnabled(false);
            contentPane.add(cbBoxLocation);
            cbBoxLocation.addActionListener(this);

            JLabel lblSelectYourLocation = new JLabel("Select your location:");
            if(getPlayerRank() < 20)
                lblSelectYourLocation.setEnabled(false);
            lblSelectYourLocation.setHorizontalAlignment(SwingConstants.CENTER);
            lblSelectYourLocation.setBounds(67, 91, 107, 14);
            contentPane.add(lblSelectYourLocation);

            chckbxProgressiveLeveling = new JCheckBox("Progress to next fish when available*");
            chckbxProgressiveLeveling.setBounds(18, 159, 211, 23);
            contentPane.add(chckbxProgressiveLeveling);
            chckbxProgressiveLeveling.addActionListener(this);

            JLabel lblWontGo = new JLabel("* Won't go past Mantas");
            lblWontGo.setBounds(6, 243, 121, 14);
            contentPane.add(lblWontGo);

            JLabel lblNewLabel = new JLabel("RDM Fisher v" + Manifest.version());
            lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
            lblNewLabel.setFont(new Font("Verdana", Font.PLAIN, 17));
            lblNewLabel.setBounds(45, 6, 147, 32);
            contentPane.add(lblNewLabel);

            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
            setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource().equals(chckbxProgressiveLeveling)){
                if(chckbxProgressiveLeveling.isSelected()) {
                    lblSelectYourFish.setEnabled(false);
                    comboBox.setEnabled(false);
                } else {
                    lblSelectYourFish.setEnabled(true);
                    comboBox.setEnabled(true);
                }
            }
            if(e.getSource().equals(btnStart)){
                index = (cbBoxLocation.getSelectedItem().toString() == "Premium Zone") ? comboBox.getSelectedIndex() + 5 : comboBox.getSelectedIndex();
                FishingLocation = cbBoxLocation.getSelectedItem().toString();
                ProgressOnLevel = chckbxProgressiveLeveling.isSelected();
                useBank = chckbxUseBank.isSelected();
                startScript = true;
                setVisible(false);
            }
            if(e.getSource().equals(cbBoxLocation)){
                DefaultComboBoxModel<String> model;
                if(cbBoxLocation.getSelectedItem().toString() == "Premium Zone") {
                    model = new DefaultComboBoxModel<String>( FishPremium );
                    System.out.println("Premium");
                    comboBox.setModel( model );
                } else {
                    model = new DefaultComboBoxModel<String>( FishCatherby );
                    System.out.println("Catherby");
                    comboBox.setModel( model );
                }
            }
        }
    }
}