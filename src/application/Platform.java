package application;

import java.util.ArrayList;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Platform extends RectangleComponent
{
	protected static ArrayList<Platform> platformsArray = new ArrayList<Platform>();
	//Rectangle component;
	
	public Platform(Group componentsGroup, double x, double y, double width, double height, Color color) 
	{
		this(componentsGroup, x, y, width);
		component.setHeight(height);
		component.setFill(color);
		platformsArray.add(0, this);
	}
	
	public Platform(Group componentsGroup, double x, double y, double width) 
	{
		//Set up component
		component = new Rectangle();
		component.setWidth(width);
		component.setHeight(20);
		component.setFill(Color.DARKMAGENTA);
		component.setTranslateX(x);
		component.setTranslateY(y);
		
		//Add Platform ArrayList
		platformsArray.add(0, this);
		
		//Add component to root of passed in scene
		componentsGroup.getChildren().add(component);
	}
	
	public static ArrayList<Platform> getPlatformsArrayList() 
	{
		return platformsArray;
	}
	
	public void delete() 
	{
		platformsArray.remove(this);
	}
	
	public static void reset() 
	{
		platformsArray.clear();
	}
}