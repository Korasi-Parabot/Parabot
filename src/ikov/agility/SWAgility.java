package ikov.agility;

import org.parabot.environment.api.interfaces.Paintable;
import org.parabot.environment.api.utils.Timer;
import org.parabot.environment.scripts.Category;
import org.parabot.environment.scripts.Script;
import org.parabot.environment.scripts.ScriptManifest;
import org.parabot.environment.scripts.framework.Strategy;
import org.rev317.min.api.methods.Skill;
import org.rev317.min.api.wrappers.*;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

@ScriptManifest(
        name = "SWAgility",
        category = Category.AGILITY,
        description = "Basic wildy course rope swinger.",
        author = "Tez",
        version = 1.1,
        servers = { "Ikov" })

public class SWAgility extends Script implements Paintable {

    private final ArrayList<Strategy> strategies = new ArrayList<Strategy>();
    private static long startTime = System.currentTimeMillis();
    private Timer exphTimer;
    final static Area agilityArea = new Area(
            new Tile(2535, 5781),
            new Tile(2535, 5787),
            new Tile(2547, 5789),
            new Tile(2547, 5781));

    public static String status = "Starting up...";
    public static int laps = 0;
    public static int startingExp = Skill.AGILITY.getExperience();
    public static int expGained;
    public static int exp;
    public static boolean isatWildy;

    //Paint
    private final RenderingHints antialiasing = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    private final Color color1 = new Color(153, 255, 255, 136);
    private final Color color2 = new Color(102, 255, 255);
    private final Color color3 = new Color(0, 0, 0);

    private final BasicStroke stroke1 = new BasicStroke(1);

    private final Font font1 = new Font("Trebuchet MS", 1, 19);
    private final Font font2 = new Font("Trebuchet MS", 3, 14);

    public void paint(Graphics g1) {
        Graphics2D g = (Graphics2D)g1;
        g.setRenderingHints(antialiasing);

//        g.setColor(color1);
//        g.fillRoundRect(353, 210, 176, 250, 16, 16);
        g.setColor(color2);
        g.setStroke(stroke1);
        g.drawRoundRect(553, 210, 176, 250, 16, 16);
        g.setFont(font1);
        g.setColor(color3);
        g.drawString("TezAgility - Tez", 568, 232);
        g.setFont(font2);
        g.drawString("EXP: " + (numberFormat(expGained)), 560, 278);
        g.drawString("EXP/H: " + (numberFormat(exphTimer.getPerHour(expGained))), 559, 309);
        g.drawString("RUNTIME: " + (runTime()), 559, 338);
        g.drawString("Version: 1.1", 559, 429);
        g.drawString("Made by Tez - Parabot", 565, 455);
        g.drawString("STATUS: " + status, 560, 365);
        g.drawString("LAPS: " + laps, 560, 395);

    }

    public boolean onExecute() {
        exphTimer = new Timer();

        strategies.add(new CompleteCourse());
     //   strategies.add(new AreaCheck());
        provide(strategies);
        return true;
    }

    String runTime()
    {
        DecimalFormat nf = new DecimalFormat("00");

        long millis = System.currentTimeMillis() - startTime;
        long hours = millis / (1000 * 60 * 60);
        millis -= hours * (1000 * 60 * 60);
        long minutes = millis / (1000 * 60);
        millis -= minutes * (1000 * 60);
        long seconds = millis / 1000;

        return nf.format(hours) + ":" + nf.format(minutes) + ":" + nf.format(seconds);
    }


    public String numberFormat(int item)
    {
        DecimalFormat form = new DecimalFormat("#,###");
        return "" + form.format(item);
    }


    public void onFinish() {
        System.out.println("Finished.");

    }

}