package ikov.pestcontrol;

import org.parabot.environment.scripts.framework.Strategy;
import org.rev317.min.api.methods.Game;

public class Winner implements Strategy {

	public boolean activate() {
		return Game.getOpenBackDialogId() == 4893 && System.currentTimeMillis() - PPestcontrol.lastWin > 30000;
	}

	public void execute() {
		PPestcontrol.lastWin = System.currentTimeMillis();
		PPestcontrol.gamesWon++;
	}

}
