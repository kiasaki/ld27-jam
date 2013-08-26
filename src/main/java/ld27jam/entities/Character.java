package ld27jam.entities;

import ld27jam.World;
import ld27jam.res.AnimatedSprite;
import ld27jam.res.ImageSheet;
import ld27jam.res.Sounds;
import ld27jam.spatialData.AABB;
import ld27jam.spatialData.Region;
import ld27jam.states.GameOverState;
import ld27jam.states.WinState;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class Character extends Entity
{
	public static Sound HEARTBEAT, STATIC;
	
	public float lightVariation = 0.1f, lightBase = 300, lightMoment = 0, speed = 0.12f;
	public Color lightColor = new Color(255, 230, 180);
	public boolean isMovingDiagonally;
	public float maxSanity = 9.9999f;
	public float sanity = maxSanity;
	public int toNextHeartbeat;
	public int invincibility;
	public ImageSheet stillSheet, walkingSheet;
	
	public int animUp = 5, animUpLeft = 6, animLeft = 7, animDownLeft = 0, animDown = 1, animDownRight = 2, animRight = 3, animUpRight = 4;

	public void hitSanity(float hitSanity, World world, Vector2f knockback)
	{
		if (invincibility <= 0)
		{
			sanity -= hitSanity;
			world.setShake(10, 15);
			invincibility = 30;
			move(knockback, world);
			try
			{
				if (STATIC == null)
					STATIC = Sounds.get("res/audio/Static.ogg");
				STATIC.play(1, 0.75f);
			}
			catch(SlickException ex){}
		}
	}
	public void updateAnim(GameContainer gc)
	{
		isMovingDiagonally = true;
		imageSheet = walkingSheet;
		
		// Up
		if (gc.getInput().isKeyDown(Input.KEY_W) || gc.getInput().isKeyDown(Input.KEY_UP))
			if (gc.getInput().isKeyDown(Input.KEY_A) || gc.getInput().isKeyDown(Input.KEY_LEFT))
				imageSheet.setFrameY(animUpLeft);
			else if (gc.getInput().isKeyDown(Input.KEY_D) || gc.getInput().isKeyDown(Input.KEY_RIGHT))
				imageSheet.setFrameY(animUpRight);
			else
			{
				imageSheet.setFrameY(animUp);
				isMovingDiagonally = false;
			}
		// Down
		else if (gc.getInput().isKeyDown(Input.KEY_S) || gc.getInput().isKeyDown(Input.KEY_DOWN))
			if (gc.getInput().isKeyDown(Input.KEY_A) || gc.getInput().isKeyDown(Input.KEY_LEFT))
				imageSheet.setFrameY(animDownLeft);
			else if (gc.getInput().isKeyDown(Input.KEY_D) || gc.getInput().isKeyDown(Input.KEY_RIGHT))
				imageSheet.setFrameY(animDownRight);
			else
			{
				imageSheet.setFrameY(animDown);
				isMovingDiagonally = false;
			}
		// Left
		else if (gc.getInput().isKeyDown(Input.KEY_A) || gc.getInput().isKeyDown(Input.KEY_LEFT))
		{
			imageSheet.setFrameY(animLeft);
			isMovingDiagonally = false;
		}
		// Right
		else if (gc.getInput().isKeyDown(Input.KEY_D) || gc.getInput().isKeyDown(Input.KEY_RIGHT))
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
	
	public void gameover(StateBasedGame sbg)
	{
		HEARTBEAT.play(0.5f, 1f);
		HEARTBEAT.play(0.75f, 1f);
		HEARTBEAT.play(1f, 1f);
		HEARTBEAT.play(1.25f, 1f);
		HEARTBEAT.play(1.5f, 1f);
		HEARTBEAT.play(1.75f, 1f);
		HEARTBEAT.play(2f, 1f);
		sbg.enterState(GameOverState.ID, new FadeOutTransition(Color.black, 500), new FadeInTransition(Color.black, 800));
	}
	public void nextLevel(StateBasedGame sbg)
	{
		sbg.enterState(WinState.ID, new FadeOutTransition(Color.white, 500), new FadeInTransition(Color.white, 500));
	}
	public void heartbeat() throws SlickException
	{
		if (toNextHeartbeat > 0)
		toNextHeartbeat--;
		else
		{
			toNextHeartbeat = (int)(sanity * 5) + 20;
			if (HEARTBEAT == null)
				HEARTBEAT = Sounds.get("res/audio/Heartbeat.ogg");
			HEARTBEAT.play(-0.05f * sanity + 1.25f, 1f - sanity * 0.05f);
		}
	}
	public void updateSanity(World world, StateBasedGame sbg)
	{
		sanity = Math.max(0, Math.min(maxSanity, sanity));
		sanity -= 1f / (world.gd.level.timeStageDurations[(int) Math.floor(9.9999f - sanity)] * 60f);
		
		// Gameover check
		if (sanity <= 0.0001f)
			gameover(sbg);
		else
		{
			float sanityRatio = ((30 - Math.min(maxSanity /  sanity, 50)) / 30);
			if (invincibility > 0)
			{
				invincibility--;
				sanityRatio *= Math.random();
			}
			lightMoment += 0.005f;
			lightVariation = (float) Math.sin(lightMoment) * 0.05f;
			lightColor.r = sanityRatio * 0.4f + 0.6f;
			lightColor.g = sanityRatio * 0.95f;
			lightColor.b = sanityRatio * 0.4f + 0.35f;
			lightBase = sanityRatio * 100 + 200;
		}
	}
	public void checkForInteraction(World world, StateBasedGame sbg, GameContainer gc)
	{
		for (Tile tile : world.getTilesInRegion(this))
		{
			switch(tile.type)
			{
				case SpikeTrap:
					world.changeTileTypeAt(tile.x, tile.y, TileType.SpikeTrapOpened);
					hitSanity(0.5f, world, new Vector2f());
					break;
				case SpikeTrapOpened:
					hitSanity(0.5f, world, new Vector2f());
					break;
				case End:
					sanity = Math.min(maxSanity, sanity + 0.05f);
					if (sanity >= maxSanity)
						nextLevel(sbg);
					break;
				default:
					break;
			}
		}
		
		AABB front = new Region(getPosition().copy(), getSize());
		if (gc.getInput().isKeyDown(Input.KEY_A) || gc.getInput().isKeyDown(Input.KEY_LEFT))
		{
			front.getPosition().x -= getSize().x;
			front.getPosition().y += getSize().y;
		}
		if (gc.getInput().isKeyDown(Input.KEY_D) || gc.getInput().isKeyDown(Input.KEY_RIGHT))
		{
			front.getPosition().x += getSize().x;
			front.getPosition().y -= getSize().y;
		}
		if (gc.getInput().isKeyDown(Input.KEY_W) || gc.getInput().isKeyDown(Input.KEY_UP))
		{
			front.getPosition().x -= getSize().x;
			front.getPosition().y -= getSize().y;
		}
		if (gc.getInput().isKeyDown(Input.KEY_S) || gc.getInput().isKeyDown(Input.KEY_DOWN))
		{
			front.getPosition().x += getSize().x;
			front.getPosition().y += getSize().y;
		}
		for (Tile tile : world.getTilesInRegion(front))
		{
			Item key;
			switch(tile.type)
			{
				case ChestClosedEast:
					world.changeTileTypeAt(tile.x, tile.y, TileType.ChestOpenedEast);
					world.inventory.items.add(new Item(ItemType.Key));
					break;
				case ChestClosedWest:
					world.changeTileTypeAt(tile.x, tile.y, TileType.ChestOpenedWest);
					world.inventory.items.add(new Item(ItemType.Key));
					break;
				case ChestClosedNorth:
					world.changeTileTypeAt(tile.x, tile.y, TileType.ChestOpenedNorth);
					world.inventory.items.add(new Item(ItemType.Key));
					break;
				case ChestClosedSouth:
					world.changeTileTypeAt(tile.x, tile.y, TileType.ChestOpenedSouth);
					world.inventory.items.add(new Item(ItemType.Key));
					break;
				case DoorWE:
					world.propagateTileChangeAt(tile.x, tile.y, TileType.DoorWE, TileType.OpenedDoorWE);
					break;
				case DoorNS:
					world.propagateTileChangeAt(tile.x, tile.y, TileType.DoorNS, TileType.OpenedDoorNS);
					break;
				case LockedDoorWE:
					key = null;
					for (Item item : world.inventory.items)
						if (item.type == ItemType.Key)
						{
							key = item;
							break;
						}
					if (key != null)
					{
						world.propagateTileChangeAt(tile.x, tile.y, TileType.LockedDoorWE, TileType.OpenedDoorWE);
						world.inventory.items.remove(key);
						return;
					}
					break;
				case LockedDoorNS:
					key = null;
					for (Item item : world.inventory.items)
						if (item.type == ItemType.Key)
						{
							key = item;
						}
					if (key != null)
					{
						world.propagateTileChangeAt(tile.x, tile.y, TileType.LockedDoorNS, TileType.OpenedDoorNS);
						world.inventory.items.remove(key);
						return;
					}
					break;
				default:
					break;
			}
		}
	}
	public void checkForInput(GameContainer gc, World world)
	{
		if (gc.getInput().isKeyDown(Input.KEY_A) || gc.getInput().isKeyDown(Input.KEY_LEFT))
		{
			if (isMovingDiagonally)
				move(new Vector2f(-speed * 0.6f, speed * 0.6f), world);
			else
				move(new Vector2f(-speed, speed), world);
			world.spatialMap.update(this);
		}
		if (gc.getInput().isKeyDown(Input.KEY_D) || gc.getInput().isKeyDown(Input.KEY_RIGHT))
		{
			if (isMovingDiagonally)
				move(new Vector2f(speed * 0.6f, -speed * 0.6f), world);
			else
				move(new Vector2f(speed, -speed), world);
			world.spatialMap.update(this);
		}
		if (gc.getInput().isKeyDown(Input.KEY_W) || gc.getInput().isKeyDown(Input.KEY_UP))
		{
			if (isMovingDiagonally)
				move(new Vector2f(-speed * 0.6f, -speed * 0.6f), world);
			else
				move(new Vector2f(-speed, -speed), world);
			world.spatialMap.update(this);
		}
		if (gc.getInput().isKeyDown(Input.KEY_S) || gc.getInput().isKeyDown(Input.KEY_DOWN))
		{
			if (isMovingDiagonally)
				move(new Vector2f(speed * 0.6f, speed * 0.6f), world);
			else
				move(new Vector2f(speed, speed), world);
			world.spatialMap.update(this);
		}
	}
	
	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta, World world) throws SlickException
	{
		updateSanity(world, sbg);
		heartbeat();
		updateAnim(gc);
		checkForInteraction(world, sbg, gc);
		checkForInput(gc, world);
		
		super.update(gc, sbg, delta, world);
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g, Vector2f camera, Color color) throws SlickException
	{
		if (invincibility % 2 == 0)
			super.render(gc, sbg, g, camera, color);
	}
	
	public void init (final World world)
	{}
	
	public Character(Vector2f position) throws SlickException
	{
		super(position, new Vector2f(0.8f, 0.8f), true, new Vector2f(-14, -64), null);
		
		stillSheet = new ImageSheet("res/sprites/CharacterStill.png",64, 80);
		walkingSheet = new AnimatedSprite("res/sprites/CharacterWalk.png", 64, 80, 5);
		
		imageSheet = stillSheet;
	}
}
