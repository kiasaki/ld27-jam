package ld27jam.entities;

import ld27jam.World;
import ld27jam.input.ControlEvent;
import ld27jam.input.KeyMapping;
import ld27jam.res.AnimatedSprite;
import ld27jam.res.ImageSheet;
import ld27jam.res.Sounds;
import ld27jam.states.GameOverState;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class Character extends Entity
{
	public static Sound HEARTBEAT;
	
	public float lightVariation = 0.1f, lightBase = 300, lightMoment = 0, speed = 0.13f;
	public Color lightColor = new Color(255, 230, 180);
	public boolean isMovingDiagonally;
	public float maxSanity = 10;
	public float sanity = maxSanity;
	public int toNextHeartbeat;
	public ImageSheet stillSheet, walkingSheet;
	
	public int animUp = 5, animUpLeft = 6, animLeft = 7, animDownLeft = 0, animDown = 1, animDownRight = 2, animRight = 3, animUpRight = 4;
	
	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta, World world) throws SlickException
	{
		sanity -= 1f / (world.gd.level.timeStageDurations[(int) Math.floor(10 - sanity)] * 60f);
		
		// Gameover check
		if (sanity <= 0.0001f)
		{
			sbg.enterState(GameOverState.ID, new FadeOutTransition(Color.black, 500), new FadeInTransition(Color.black, 800));
			disposeOfEvents();
		}
		
		else
		{
			float sanityRatio = ((30 - Math.min(maxSanity /  sanity, 50)) / 30);
			lightMoment += 0.005f;
			lightVariation = (float) Math.sin(lightMoment) * 0.05f;
			lightColor.r = sanityRatio * 0.4f + 0.6f;
			lightColor.g = sanityRatio * 0.95f;
			lightColor.b = sanityRatio * 0.4f + 0.35f;
			lightBase = sanityRatio * 100 + 200;
		}
		
		if (toNextHeartbeat > 0)
			toNextHeartbeat--;
		else
		{
			toNextHeartbeat = (int)(sanity * 5) + 20;
			if (HEARTBEAT == null)
				HEARTBEAT = Sounds.get("res/audio/Heartbeat.ogg");
			HEARTBEAT.play(-0.05f * sanity + 1.25f, 1f - sanity * 0.05f);
		}
		
		super.update(gc, sbg, delta, world);
		isMovingDiagonally = true;
		imageSheet = walkingSheet;
		// Up
		if (KeyMapping.Up.isDown())
			if (KeyMapping.Left.isDown())
				imageSheet.setFrameY(animUpLeft);
			else if (KeyMapping.Right.isDown())
				imageSheet.setFrameY(animUpRight);
			else
			{
				imageSheet.setFrameY(animUp);
				isMovingDiagonally = false;
			}
		// Down
		else if (KeyMapping.Down.isDown())
			if (KeyMapping.Left.isDown())
				imageSheet.setFrameY(animDownLeft);
			else if (KeyMapping.Right.isDown())
				imageSheet.setFrameY(animDownRight);
			else
			{
				imageSheet.setFrameY(animDown);
				isMovingDiagonally = false;
			}
		// Left
		else if (KeyMapping.Left.isDown())
		{
			imageSheet.setFrameY(animLeft);
			isMovingDiagonally = false;
		}
		// Right
		else if (KeyMapping.Right.isDown())
		{
			imageSheet.setFrameY(animRight);
			isMovingDiagonally = false;
		}
		else
		{
			imageSheet = stillSheet;
			stillSheet.setFrameY(walkingSheet.getFrameY());
		}
	}
	
	ControlEvent left, right, up, down;
	
	public void init (final World world)
	{
		final Character character = this;
		left = new ControlEvent()
		{
			@Override
			public void keyDown() 
			{
			}
			@Override
			public void keyUp()
			{
			}
			@Override
			public void keyIsDown() 
			{
				if (isMovingDiagonally)
					move(new Vector2f(-speed * 0.6f, speed * 0.6f), world);
				else
					move(new Vector2f(-speed, speed), world);
				world.spatialMap.update(character);
			}
		};
		right = new ControlEvent()
		{
			@Override
			public void keyDown() 
			{
			}
			@Override
			public void keyUp()
			{
			}
			@Override
			public void keyIsDown() 
			{
				if (isMovingDiagonally)
					move(new Vector2f(speed * 0.6f, -speed * 0.6f), world);
				else
					move(new Vector2f(speed, -speed), world);
				world.spatialMap.update(character);
			}
		};
		up = new ControlEvent()
		{
			@Override
			public void keyDown() 
			{
			}
			@Override
			public void keyUp()
			{
			}
			@Override
			public void keyIsDown() 
			{
				if (isMovingDiagonally)
					move(new Vector2f(-speed * 0.6f, -speed * 0.6f), world);
				else
					move(new Vector2f(-speed, -speed), world);
				world.spatialMap.update(character);
			}
		};
		down = new ControlEvent()
		{
			@Override
			public void keyDown() 
			{
			}
			@Override
			public void keyUp()
			{
			}
			@Override
			public void keyIsDown() 
			{
				if (isMovingDiagonally)
					move(new Vector2f(speed * 0.6f, speed * 0.6f), world);
				else
					move(new Vector2f(speed, speed), world);
				world.spatialMap.update(character);
			}
		};
		KeyMapping.Left.subscribe(left);
		KeyMapping.Right.subscribe(right);
		KeyMapping.Up.subscribe(up);
		KeyMapping.Down.subscribe(down);
	}
	
	public void disposeOfEvents()
	{
		if (left != null)
			KeyMapping.Left.unsubscribe(left);
		if (right != null)
			KeyMapping.Right.unsubscribe(right);
		if (up != null)
			KeyMapping.Up.unsubscribe(up);
		if (down != null)
			KeyMapping.Down.unsubscribe(down);
	}
	
	public Character(Vector2f position) throws SlickException
	{
		super(position, new Vector2f(0.8f, 0.8f), true, new Vector2f(-14, -64), null);
		
		stillSheet = new ImageSheet("res/sprites/CharacterStill.png",64, 80);
		walkingSheet = new AnimatedSprite("res/sprites/CharacterWalk.png", 64, 80, 5);
		
		imageSheet = stillSheet;
	}
}
