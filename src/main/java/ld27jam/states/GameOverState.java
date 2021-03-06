package ld27jam.states;

import ld27jam.helpers.FontFactory;
import ld27jam.res.Sounds;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class GameOverState extends BasicGameState {

	public static final int ID = 4;
	private StateBasedGame sbg;

	private long started;
	private UnicodeFont uFont = FontFactory.get().getFont(60, java.awt.Color.WHITE);
	private UnicodeFont uFontSmall = FontFactory.get().getFont(18, java.awt.Color.WHITE);

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException 
	{
		this.sbg = sbg;
		this.started = System.currentTimeMillis();
	}

	@Override
	public void keyReleased(int key, char c) 
	{
		if (key == Input.KEY_ESCAPE)
			exitGame();
		if (key == Input.KEY_ENTER)
			retry();
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException 
	{
		// Game over
		uFont.drawString(gc.getWidth()/2-(uFont.getWidth("GAME OVER")/2), gc.getHeight()/2-25, "GAME OVER");
		// Info
		String text = "Press enter to try again, escape to exit...";
		uFontSmall.drawString(gc.getWidth()/2-(uFontSmall.getWidth(text)/2), gc.getHeight()/2+125, text);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException 
	{
	}
	
	@Override
	public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException
	{
		Sounds.get("res/audio/StaticOver.ogg").loop(1, 0.75f);
	}
	@Override
	public void leave(GameContainer gc, StateBasedGame sbg) throws SlickException
	{
		Sounds.get("res/audio/StaticOver.ogg").stop();
	}

	public void retry()
	{
		if (System.currentTimeMillis() - this.started > 2000)
		{
			((GameState)sbg.getState(GameState.ID)).reinit();
			sbg.enterState(GameState.ID, new FadeOutTransition(Color.white, 700), new FadeInTransition(Color.white));
		}
	}
	public void exitGame()
	{
		if (System.currentTimeMillis() - this.started > 2000)
		{
			((GameState)sbg.getState(GameState.ID)).reinit();
			sbg.enterState(MenuState.ID, new FadeOutTransition(Color.white, 700), new FadeInTransition(Color.white));
		}
	}

	@Override
	public int getID() 
	{
		return ID;
	}

}
