package ld27jam.entities;

import java.util.ArrayList;

import org.newdawn.slick.geom.Vector2f;

public class RoomTemplate {

	String stringTemplate;
	char[][] template;
	private Vector2f exitsOffset;
	
	public RoomTemplate(String template)
	{
		this.stringTemplate = template;
		String[] lines = template.split("\n");
		this.template = new char[lines[0].length()][lines.length];
		for (int y = 0; y < lines.length; y++) 
		{
			for (int x = 0; x < lines[y].length(); x++) 
			{
				this.template[x][y] = lines[y].charAt(x);
			}
		}
	}
	
	public int getHeight()
	{
		return this.template[0].length;
	}
	
	public int getWidth()
	{
		return this.template.length;
	}
	
	public TileType getTileAtCell(int x, int y)
	{
		char tile = template[x][y];
		switch (tile) 
		{
			case 'f':
				return TileType.Floor;
			case 'e':
				return TileType.EndRoomFloor;
			case 'E':
				return TileType.End;
			case '+':
				return TileType.Door;
			case 'S':
				return TileType.StartingPoint;
			default:
				return TileType.None;
		}
	}
	
	public ArrayList<Vector2f> exits()
	{
		ArrayList<Vector2f> exitsArray = new ArrayList<Vector2f>();
		
		for (int x_in_room = 0; x_in_room < this.getWidth(); x_in_room++) 
		{
			for (int y_in_room = 0; y_in_room < this.getHeight(); y_in_room++) 
			{
				if (this.template[x_in_room][y_in_room] == '+') 
					exitsArray.add(new Vector2f(x_in_room + this.exitsOffset.x, y_in_room + this.exitsOffset.y));
			}
		}
		
		return exitsArray;
	}
	
	public void setExitsOffset(Vector2f off)
	{
		this.exitsOffset = off;
	}
	
	public static RoomTemplate getStartingRoom()
	{
		return new RoomTemplate("     +     \n" +
								"   fffff   \n" +
								"    fff    \n" +
								" f fffff f \n" +
								" fffffffff \n" +
								"+ffffSffff+\n" +
								" fffffffff \n" +
								" f fffff f \n" +
								"    fff    \n" +
								"   fffff   \n" +
								"     +     \n");
	}
	
	public static RoomTemplate getFinishingRoom()
	{
		return new RoomTemplate("         \n" +
								" +     + \n" +
								"+ee   ee+\n" +
								"  eeeee  \n" +
								"  eeEee  \n" +
								"  eeeee  \n" +
								"+ee   ee+\n" +
								" +     + \n" +
								"         ");
	}
}
