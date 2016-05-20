package application;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import application.SpriteAnimation;
import javafx.animation.Animation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

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
//	Rectangle mainCharField;
	
	ImageView mainCharField;
	
	//Spritesheet stuff
	private static final Image IMAGE = new Image("resources/running.png");
	private static final int COLUMNS = 2;
	private static final int COUNT = 4;
	private static final int OFFSET_X = 0;
	private static final int OFFSET_Y = 0;
	private static final int WIDTH = 59;
	private static final int HEIGHT = 60;
	

	
	public Character(Group g, int lifeCount)
	{
		
		new Timer().schedule(new TimerTask() {public void run() {stateInvincible = false;}
		}, 0, 2000);
//		mainCharField = new Rectangle (50,50);
//		mainCharField.setLayoutX(100);
//		mainCharField.setLayoutY(0);
//		mainCharField.setFill(Color.TRANSPARENT);
//		g.getChildren().add(mainCharField);
		
		mainCharField = new ImageView(IMAGE);
		mainCharField.setViewport(new Rectangle2D(OFFSET_X, OFFSET_Y, WIDTH,HEIGHT ));
		mainCharField.setLayoutX(100);
		mainCharField.setLayoutY(-2);
		
		Animation animation = new SpriteAnimation(
				mainCharField, Duration.millis(400),
				COUNT, COLUMNS, OFFSET_X, OFFSET_Y,
				WIDTH, HEIGHT
		);
		g.getChildren().add(mainCharField);
		animation.setCycleCount(Animation.INDEFINITE);
		animation.play();
	
	}
	
	
	public Image loadCharPunching() throws IOException {
		final InputStream punch = Files.newInputStream(Paths.get("resources/punch.png"));
		Image charPunch = new Image(punch);
		return charPunch;
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