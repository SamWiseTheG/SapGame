package application;

import java.util.ArrayList;

import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Enemy extends RectangleComponent
{
	private static final Image ENEMY = new Image("resources/graham.png");
	ImageView mainEnemy;
	
	protected static ArrayList<Enemy> enemiesArray = new ArrayList<Enemy>();
	//protected RectangleComponent component;
	protected Color enemyColor;
	
	public Enemy(Group enemyGroup, double xCoord, double yCoord) 
	{	
		mainEnemy = new ImageView(ENEMY);
		mainEnemy.setViewport(new Rectangle2D(0,0, 26, 45));
		mainEnemy.setLayoutX(xCoord);
		mainEnemy.setLayoutY(yCoord);
		
//		component = new Rectangle(xCoord, yCoord, 35, 35);
//		component.setFill(Color.CADETBLUE);
		
		
		//component = new Rectangle(xCoord, yCoord, 10, Color.CADETBLUE);
		enemyGroup.getChildren().add(mainEnemy);
		enemiesArray.add(0, this);
	}
	
	public static ArrayList<Enemy> getEnemiesArrayList() 
	{
		return enemiesArray;
	}
	
	public Bounds getBounds() 
	{
		return mainEnemy.getBoundsInParent();
	}
	
	public static void reset() 
	{
		enemiesArray.clear();
	}
}