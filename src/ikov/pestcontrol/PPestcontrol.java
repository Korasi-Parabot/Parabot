package ikov.pestcontrol;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import org.parabot.environment.api.interfaces.Paintable;
import org.parabot.environment.scripts.Category;
import org.parabot.environment.scripts.Script;
import org.parabot.environment.scripts.ScriptManifest;
import org.parabot.environment.scripts.framework.Strategy;
import org.rev317.min.api.events.MessageEvent;
import org.rev317.min.api.events.listeners.MessageListener;
import org.rev317.min.api.methods.Players;
import org.rev317.min.api.wrappers.Tile;


@ScriptManifest(author="Agrodon", category = Category.OTHER, description="Pest Control", name="PPestcontrol", servers={"ikov"}, version=0.1)
public class PPestcontrol extends Script implements Paintable, MessageListener{
    private final ArrayList<Strategy> strategies = new ArrayList<Strategy>();
    
    public static final String[] PORTAL_SIDES = {"West", "SouthWest", "SouthEast","East"};
    public static final int[] PORTAL_IDS = {6142,6145,6144,6143};
    public static final Tile[] PORTAL_TILES = {new Tile(2628,2592), 
    											new Tile(2645,2570),
    											new Tile(2669,2571),
    											new Tile(2680,2590)};
    public static final Tile CENTER_TILE = new Tile(2656, 2590);
    public static final int GANGPLANK_ID = 14315;
    public static final int BRAWLER_ID = 3776;
    
    public static boolean praying = false;
    public static int randomizedPath = 0;
    public static long lastWin = 0;
    public static int gamesWon = 0;
    public static int gamesLost = 0;
    public long startTime = System.currentTimeMillis();

    
    @Override
    public boolean onExecute() {
    	strategies.add(new Winner());
    	strategies.add(new EnterBoat());
    	strategies.add(new ToPortal());
    	strategies.add(new AttackPortal());
        provide(strategies);
        return true;
    }
    @Override
    public void onFinish() {
    }
	public void paint(Graphics g) {
	    Graphics2D g2 = (Graphics2D)g;
	    long millis = System.currentTimeMillis() - startTime;
	    int winsHr = (int) (gamesWon*1*3600000.0D/millis);
	    //int lostHr = (int) (gamesLost*3600000.0D/millis);
	    long hours = millis / 3600000L;
	    millis -= hours * 3600000L;
	    long minutes = millis / 60000L;
	    millis -= minutes * 60000L;
	    long seconds = millis / 1000L;
		shadowedString(g2,"Runtime: "+hours+":"+minutes+":"+seconds, 560,415, Color.white);
		//shadowedString(g2,"Wins(/hr): "+gamesWon+" ("+winsHr+")", 560, 415, Color.white);
		shadowedString(g2,"Points(/hr): "+gamesWon*10+" ("+winsHr*10+")", 560, 430, Color.white);
		shadowedString(g2,"PPestcontrol",650,463, Color.white);
		}
	public void shadowedString(Graphics g, String text, int x, int y, Color mainColor){
		g.setColor(Color.black);
		g.drawString(text, x+1, y-1);
		g.setColor(mainColor);
		g.drawString(text, x, y);
	}
	public void messageReceived(MessageEvent m) {
		String msg = m.getMessage();
		if(msg.contains("no points"))
			gamesLost++;
	}
	
	public static boolean inArea(int x, int y, int x2, int y2){
		return Players.getMyPlayer().getX() <= x2
				&& Players.getMyPlayer().getX() >= x
				&& Players.getMyPlayer().getY() <= y2
				&& Players.getMyPlayer().getY() >= y;
	}

}
