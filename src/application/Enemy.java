package application;

import java.util.ArrayList;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Enemy extends RectangleComponent
{
	protected static ArrayList<Enemy> enemiesArray = new ArrayList<Enemy>();
	//protected RectangleComponent component;
	protected Color enemyColor;
	Group componentGrp;
	
	public Enemy(Group componentsGroup, double xCoord, double yCoord) 
	{	
		component = new Rectangle(0, yCoord,35,35);
		component.setWidth(35);
		component.setHeight(35);
		component.setFill(Color.DARKORANGE);
		//component = new Rectangle(xCoord, yCoord, 10, Color.CADETBLUE);
		componentsGroup.getChildren().add(component);
		enemiesArray.add(0, this);
	}
	
	public static ArrayList<Enemy> getEnemiesArrayList() 
	{
		return enemiesArray;
	}
	
	public Bounds getBounds() 
	{
		return component.getBoundsInParent();
	}
	
	public static void reset() 
	{
		enemiesArray.clear();
	}

	public void delete()
	{
		componentGrp.getChildren().remove(component);
		enemiesArray.remove(this);
		
	}
}