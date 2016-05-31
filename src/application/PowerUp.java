package application;

import java.awt.Component;
import java.util.ArrayList;
import javafx.geometry.Bounds;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

public class PowerUp
{
	// the shape of the power up
	Circle component;
	// the group sets precedent of the created objects
	Group powerUpGroup;
	// the list of all the power ups
	protected static ArrayList<PowerUp> powerUpArray = new ArrayList<PowerUp>();
	private int type;
	
	//constructor   precedent,  xcord,    ycord, what powerup gives
	public PowerUp( Group g, double x, double y, int type )
	{
		powerUpGroup = g;
		component = new Circle();
		powerUpArray.add( 0, this );
		component.setRadius( 15 );
		component.setTranslateX( x );
		component.setTranslateY( y );
		if( type == 1 )
		{
			component.setFill( Color.FORESTGREEN );
			setType(1);
		}
		else if( type == 2 )
		{
			component.setFill( Color.DARKBLUE );
			setType(2);
		}
		else if( type == 3 )
		{
			component.setFill( Color.CRIMSON );
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