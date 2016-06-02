package application;

import java.awt.Component;
import java.util.ArrayList;
import javafx.geometry.Bounds;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.*;

public class PowerUp extends RectangleComponent
{
	private static final Image GREEN = new Image("resources/greenPowerup.png");
	private static final Image RED = new Image("resources/redPowerup.png");
	private static final Image BLUE = new Image("resources/bluePowerup.png");
	Group powerUpGroup;
	protected static ArrayList<PowerUp> powerUpArray = new ArrayList<PowerUp>();
	private int type;
	
	//constructor   precedent,  xcord,    ycord, what powerup gives
	public PowerUp( Group g, double x, double y, int type )
	{
		powerUpGroup = g;
		component = new Rectangle();
		powerUpArray.add( 0, this );
		component.setWidth(30);
		component.setHeight(40);
		component.setTranslateX( x );
		component.setTranslateY( y );
		if( type == 1 )
		{
		component.setFill( new ImagePattern(GREEN) );
		setType(1);
		}
		else if( type == 2 )
		{
		component.setFill( new ImagePattern(BLUE) );
		setType(2);
		}
		else if( type == 3 )
		{
		component.setFill( new ImagePattern(RED) );
		setType(3);
		}
		g.getChildren().add( component );
	}
	
	// for unit collision
	public Bounds getBounds() 
	{
		return component.getBoundsInParent();
	}
	public void setType(int type) 
	{
		this.type = type;
	}
	public int getType() 
	{
		return this.type;
	}
	
	// remove from the array list
	public void delete() 
	{
		powerUpGroup.getChildren().remove(component);
		powerUpArray.remove(this);
	}
	
	// getter of the array list
	public static ArrayList<PowerUp> getPowerUpArrayList() 
	{
		return powerUpArray;
	}
	
	// resets the array list
	public static void resetArrayList() 
	{
		powerUpArray.clear();
	}
	
	// increase jump height = 1 green
		
	// invincibility = 2 blue
	
	// extra life = 3 red
	
//	public static void main ( String [] args )
//	{
//		Group root = new Group();
//		Canvas c = new Canvas( 300, 300 );
//		new PowerUp( root, 2, 5, 1 );
//		root.getChildren().add( c );
//	}
}