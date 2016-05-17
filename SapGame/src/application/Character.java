package application;

import java.util.*;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Character 
{
	Enemy enemy;
	Wall wall;
	private int lives=5;
	private int speed;
	private boolean stateOnPlatform;
	private boolean stateAlive=true;
	private boolean stateInvincible;
	private boolean stateOnEnemy;
	private boolean stateOnWall;
	private boolean stateBreakWall=false;
	private boolean stateCanJump;
	private double xPos;
	private double yPos;
	Rectangle mainCharField;
	
	public Character(Group g, int lifeCount)
	{
		new Timer().schedule(new TimerTask() {public void run() {stateInvincible = false;}
		}, 0, 2000);
		mainCharField = new Rectangle (50,50);
		mainCharField.setLayoutX(100);
		mainCharField.setLayoutY(0);
		mainCharField.setFill(Color.RED);
		g.getChildren().add(mainCharField);
	}
	
	public void jump()
	{
		
	}
	
	public void punch (Wall wall)
	{
		wall.breakWall(wall);
	}
	
	//Get Bounds
	public double getMinY()
	{
		return mainCharField.getBoundsInParent().getMinY();
	}
	
	public double getMaxY()
	{
		return mainCharField.getBoundsInParent().getMaxY();
	}
	public double getMinX()
	{
		return mainCharField.getBoundsInParent().getMinX();
	}
	
	public double getMaxX()
	{
		return mainCharField.getBoundsInParent().getMaxX();
	}
	
	///Movement
	public void move(double x, double y)
	{
		
	}
	
	//adds a Life
	public void addLife(int lives)
	{
		lives++;
	}
	
	//Removes a life
	
	////Setters/Getters
	public int getLives() 
	{
		return lives;
	}

	public void setLives(int lives) 
	{
		this.lives = lives;
	}

	public int getSpeed()
	{
		return speed;
	}

	public void setSpeed(int speed)
	{
		this.speed = speed;
	}

	public boolean isStateOnPlatform()
	{
		return stateOnPlatform;
	}

	public void setStateOnPlatform(boolean stateOnPlatform) 
	{
		this.stateOnPlatform = stateOnPlatform;
	}

	public boolean isStateAlive() 
	{
		return stateAlive;
	}

	public void setStateAlive(boolean stateAlive) 
	{
		this.stateAlive = stateAlive;
	}

	public boolean isStateInvincible()
	{
		return stateInvincible;
	}

	public void setStateInvincible(boolean stateInvincible) 
	{
		this.stateInvincible = stateInvincible;
	}

	public double getxPos()
	{
		return xPos;
	}

	public void setxPos(double xPos)
	{
		this.xPos = xPos;
	}

	public double getyPos() 
	{
		return yPos;
	}

	public void setyPos(double yPos) 
	{
		this.yPos = yPos;
	}

	public Enemy getEnemy()
	{
		return enemy;
	}

	public void setEnemy(Enemy enemy) 
	{
		this.enemy = enemy;
	}

	public Wall getWall() 
	{
		return wall;
	}

	public void setWall(Wall wall) {
		this.wall = wall;
	}

	public boolean isStateOnEnemy() 
	{
		return stateOnEnemy;
	}

	public void setStateOnEnemy(boolean stateOnEnemy) 
	{
		this.stateOnEnemy = stateOnEnemy;
	}

	public boolean isStateOnWall() 
	{
		return stateOnWall;
	}

	public void setStateOnWall(boolean stateOnWall) 
	{
		this.stateOnWall = stateOnWall;
	}

	public boolean getStateCanJump() 
	{
		return stateCanJump;
	}

	public void setStateCanJump(boolean stateCanJump)
	{
		this.stateCanJump = stateCanJump;
	}
}