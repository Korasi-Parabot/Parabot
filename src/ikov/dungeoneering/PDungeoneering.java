package ikov.dungeoneering;

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
import org.rev317.min.api.methods.Skill;
import org.rev317.min.api.wrappers.Tile;


@ScriptManifest(author="Agrodon", category = Category.DUNGEONEERING, description="Dungeoneering - Rushes C2", name="PDungeoneering", servers={"ikov"}, version=1.0)
public class PDungeoneering extends Script implements Paintable, MessageListener{
	private final ArrayList<Strategy> strategies = new ArrayList<Strategy>();

    public static boolean gotRock = false;
    public static boolean equipped = false;
	public static boolean gotOrb = false;

	public static final int THOK_ID = 9713;
	public static final int ROCK_ID = 1481;
	public static final int ORB_ID = 6822;
	public static final int SCULPTURE_ID = 1417;
	public static final int CRATE_ID = 357;
	public static final int[] BOSS_IDS = {10044, 10064, 9989, 9916, 10116, 9934, 10110};
	public static final Tile ROCK_TILE = new Tile(2622, 9835);
	public static final Tile BOSS_TILE = new Tile(2564, 9849);
	public static final Tile CRATE_TILE = new Tile(2563, 9847);

	private int dungCount = 0;
	private int deathCount = 0;
	private int startExp = Skill.getCurrentExperience(23);
	private long startTime = System.currentTimeMillis();
	
	
    @Override
    public boolean onExecute() {
    	/*for(int i = 0; i < Skill.values().length; i++){
    		System.out.println(i+":"+Skill.values()[i]);
    	}*/
    	strategies.add(new StartDungeon());
    	strategies.add(new GetRock());
    	strategies.add(new GetOrb());
    	strategies.add(new SummonBoss());
    	strategies.add(new FightBoss());
        provide(strategies);
        return true;
    }
    @Override
    public void onFinish() {
    }
    
	public void paint(Graphics g) {
	    long millis = System.currentTimeMillis() - startTime;
	    int deathHr = (int) (deathCount*3600000.0D/millis);
	    int expGained = Skill.getCurrentExperience(23)-startExp;
	    int expHr = (int) (expGained*3600000.0D/millis);
	    int dungHr = (int) (dungCount*3600000.0D/millis);
	    long hours = millis / 3600000L;
	    millis -= hours * 3600000L;
	    long minutes = millis / 60000L;
	    millis -= minutes * 60000L;
	    long seconds = millis / 1000L;
	    Graphics2D g2 = (Graphics2D)g;
		shadowedString(g2,"Runtime: "+hours+":"+minutes+":"+seconds, 560,400);
		shadowedString(g2,"Deaths(/hr): "+deathCount+"("+deathHr+")", 560, 415);
		shadowedString(g2,"Dungeons(/hr): "+dungCount+"("+dungHr+")", 560, 430);
		shadowedString(g2,"Exp gained(/hr): "+ (expGained<100000 ? expGained : expGained/1000 + "k")+"("+(expHr<100000 ? expHr : expHr/1000 + "k")+")", 560, 445);
		shadowedString(g2,"PDungeoneering",640,463);
	}
	
	public void shadowedString(Graphics g, String text, int x, int y){
		g.setColor(Color.black);
		g.drawString(text, x+1, y-1);
		g.setColor(Color.white);
		g.drawString(text, x, y);
	}
	public void messageReceived(MessageEvent m) {
		String msg = m.getMessage();
		if(msg.contains("You have destroyed your boss:"))
			dungCount++;
		if(msg.contains("new life"))
			deathCount++;
	}
}
