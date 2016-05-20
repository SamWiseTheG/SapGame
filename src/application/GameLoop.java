package application;

import java.util.Random;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public abstract class GameLoop
{
	
	private boolean spacePressed=false;
	double stageWidth;
	double stageHeight;
	Enemy e;
	HUD health;
	PowerUp powerUp;
	Scene scene;
	Stage stage;
	Group root;
	Group componentsGroup;
	Rectangle rainforest;
	Image rF;
	Character mainChar;
	//Menu m;
	Random r= new Random();
	boolean falling=true;
	boolean cheatMode=false;
	boolean gotHealth=false;
	boolean gotHurt=false;
	boolean jumping=false;
	int movementSpeed=5;

	Wall wall;

	
	////Make Platforms move
	//	TranslateTransition tPlatform;
	//	ParallelTransition ptGravity;
	//	ParallelTransition ptJumping;
	/////// I commented out the few lines that deal with it cuz it barely works

	public GameLoop(Stage primaryStage)
	{
		stage=primaryStage;
		root = new Group();
		componentsGroup = new Group();
		initBackground();
		initStage();
		root.getChildren().add(componentsGroup);
		scene = new Scene (root, 1000, 600);
		movePlatform();

		//tPlatform = new TranslateTransition(Duration.seconds(5), f.component);
		//tPlatform.setByX(-1000);

		//-----------KeyEvents--------------//
		new AnimationTimer()
		{
			public void handle(long now)
			{
				gravity();
				checkForPlatformCollisions();
				checkForWallCollisions();
				checkbottomCollision();
				checkPowerUpCollision();
				checkEnemieCollision();
				scene.setOnKeyPressed(k -> act(k));

			}
		}.start();
	}

	public void stop(KeyEvent k, Rectangle r)
	{
		//		if(k.getCode()==KeyCode.A)
		//			spacePressed=false;
	}

	public void act(KeyEvent k)
	{
		switch(k.getCode())				
		{
			case SPACE:
				if(!spacePressed)
				{
					spacePressed=true;
					jumping=true;
					TranslateTransition t1 = new TranslateTransition(Duration.millis(300),mainChar.mainCharField);
					t1.setByY(-50);

					TranslateTransition t2 = new TranslateTransition(Duration.millis(100),mainChar.mainCharField);
					t2.setByY(0);

					SequentialTransition st = new SequentialTransition();
					st.getChildren().addAll(t1,t2);
					st.play();
					st.setOnFinished(new EventHandler<ActionEvent>(){
						public void handle(ActionEvent arg0) 
						{
							falling=true;
							jumping=false;
						}
					});			
				}

				break;

				//punch
			case C:
				if((getClosestWall().getMinX())-(mainChar.getMaxX()) <= 20)
				{
					mainChar.punch(getClosestWall());
				}
				break;


				//----------------START DEVELOPMENT TOOLS-----------------

				//turn gravity on/off
			case G:
				if(cheatMode)
				{
					//mainChar.mainCharField.setFill(Color.GREEN);
					movementSpeed=30;
					cheatMode=false;
				}
				else
				{
					//mainChar.mainCharField.setFill(Color.BLUE);
					movementSpeed=2;
					cheatMode=true;
				}
				break;
				//movement keys
			case D:
				TranslateTransition ttt = new TranslateTransition(Duration.millis(1), mainChar.mainCharField);
				ttt.setByX(movementSpeed);
				ttt.play();
				break;

			case A:
				TranslateTransition tttt = new TranslateTransition(Duration.millis(1), mainChar.mainCharField);
				tttt.setByX(-movementSpeed);
				tttt.play();
				break;

//			case W:
//				if (mainChar.getStateCanJump())
//				{
//					//spacePressed=true;
//					TranslateTransition ttUp = new TranslateTransition(Duration.millis(1), mainChar.mainCharField);
//					ttUp.setByY(-100);
//					ttUp.play();
//				}
//				break;

			case S:
				if(!mainChar.isStateOnPlatform())
				{
					TranslateTransition ttDown = new TranslateTransition(Duration.millis(1), mainChar.mainCharField);
					ttDown.setByY(movementSpeed);
					ttDown.play();
				}

			default:
				break;
				//---------------------END DEVELOPMENT TOOLS------------------------------------------------
		}				
	}

	//	public void canJump()
	//	{
	//		spacePressed=false;
	//	}

	public void gravity()
	{
		TranslateTransition tt = new TranslateTransition(Duration.millis(1), mainChar.mainCharField);
		//ptGravity = new ParallelTransition();
		//ptGravity.getChildren().addAll(tt,tPlatform);
		tt.setByY(5);
		if(!jumping)
		{
			if(falling && !cheatMode)	
			{
				mainChar.setStateCanJump(false);
				tt.play();
			}
			else
			{
				mainChar.setStateCanJump(true);
			}
		}
	}
	public void canJump() 
	{
		spacePressed=false;
	}

	public void checkForPlatformCollisions()
	{
		Platform closestPlat = getClosestPlatform();

		double charMaxY = mainChar.getMaxY();
		double charMinX = mainChar.getMinX();
		double charMaxX = mainChar.getMaxX();

		closestPlat.component.setFill(Color.ORANGE);

		if ( (charMaxY>=(closestPlat.getMinY()-5)) && ( charMaxY<=(closestPlat.getMinY()+5) ) 
				&& ( charMinX+20 <= closestPlat.getMaxX()) 
				&& (charMaxX-12 >= closestPlat.getMinX()) )
		{
			mainChar.setStateOnPlatform(true);
			falling=false;
			canJump();
		}
		else
		{
			mainChar.setStateOnPlatform(false);
			closestPlat.component.setFill(Color.DARKMAGENTA);
			falling=true;
		}
		//
		//		if (mainChar.mainCharField.getBoundsInParent().intersects(closestPlat.getBounds()) && spacePressed==false)
		//		{
		//			mainChar.setStateCanJump(true);
		//		}
		//		else
		//		{
		//			mainChar.setStateCanJump(false);
		//		}
	}

	public Platform getClosestPlatform()
	{
		Platform closestPlat = Platform.platformsArray.get(0);

		double charMaxY = mainChar.mainCharField.getBoundsInParent().getMaxY();

		for (Platform p : Platform.getPlatformsArrayList()) 
		{
			if( (Math.abs(p.getMinY()-charMaxY) )<=(Math.abs(closestPlat.getMinY()-charMaxY))  )
			{
				closestPlat=p;
				closestPlat.component.setFill(Color.BLUE);
			}
		}
		return closestPlat;
	}

	public void checkForWallCollisions()
	{
		Wall closestWall = getClosestWall();

		boolean wallCollision = (mainChar.mainCharField.getBoundsInParent().intersects(closestWall.getBounds()));
		//closestWall.component.setFill(Color.ORANGE);
		if (wallCollision)

		{
			//mainChar.setStateCanJump(true);
			//mainChar.mainCharField.setFill(Color.RED);
			closestWall.breakWall(closestWall);
			health.removeHealth();
		}
		else
		{
			//closestWall.component.setFill(Color.DARKMAGENTA);
			//mainChar.mainCharField.setFill(Color.YELLOW);
			//mainChar.setStateCanJump(false);
		}
	}

	public Wall getClosestWall()
	{
		Wall closestWall = Wall.wallsArray.get(0);

		double charMaxY = mainChar.getMaxY();
		double charMinY = mainChar.getMaxY();
		double charMinX = mainChar.getMinX();
		double charMaxX = mainChar.getMaxX();

		double charCenterX =  charMaxX-((charMaxX-charMinX)/2);

		for (Wall w : Wall.getWall()) 
		{
			if( (Math.abs(w.getMinX()-charCenterX) )<=(Math.abs(closestWall.getMinX()-charCenterX))  )
			{
				closestWall=w;
				//closestWall.component.setFill(Color.BLUE);
			}
		}
		return closestWall;
	}

	public void checkPowerUpCollision()
	{
		PowerUp p = getClosestPowerUp();
		if (mainChar.mainCharField.getBoundsInParent().intersects(p.getBounds()))
		{
			p.delete();
			if(!gotHealth && p.getType()==3)
			{
				health.addHealth();
				gotHealth=true;
			}
		}
	}
	public void checkEnemieCollision()
	{
		Enemy e = getClosestEnemie();
		if (mainChar.mainCharField.getBoundsInParent().intersects(e.getBounds()))
		{
			if(!gotHurt)
			{
				health.removeHealth();
				gotHurt=true;
			}		
		}
	}

	public PowerUp getClosestPowerUp()
	{
		PowerUp closestPowerUp = PowerUp.powerUpArray.get(0);

		double charMaxY = mainChar.mainCharField.getBoundsInParent().getMaxY();
		double charMinY = mainChar.mainCharField.getBoundsInParent().getMaxY();
		double charMinX = mainChar.mainCharField.getBoundsInParent().getMinX();
		double charMaxX = mainChar.mainCharField.getBoundsInParent().getMaxX();

		double charCenterX =  charMaxX-((charMaxX-charMinX)/2);

		for (PowerUp p : PowerUp.getPowerUpArrayList()) 
		{
			if( (Math.abs(p.getBounds().getMinX()-charCenterX) )<=(Math.abs(closestPowerUp.getBounds().getMinX()-charCenterX))  )
			{
				closestPowerUp=p;
				//closestWall.component.setFill(Color.BLUE);
			}
		}
		return closestPowerUp;
	}

	public Enemy getClosestEnemie()
	{
		Enemy closestEnemy = Enemy.enemiesArray.get(0);

		double charMaxY = mainChar.mainCharField.getBoundsInParent().getMaxY();
		double charMinY = mainChar.mainCharField.getBoundsInParent().getMaxY();
		double charMinX = mainChar.mainCharField.getBoundsInParent().getMinX();
		double charMaxX = mainChar.mainCharField.getBoundsInParent().getMaxX();

		double charCenterX =  charMaxX-((charMaxX-charMinX)/2);

		for (Enemy e : Enemy.getEnemiesArrayList()) 
		{
			if( (Math.abs(e.getBounds().getMinX()-charCenterX) )<=(Math.abs(closestEnemy.getBounds().getMinX()-charCenterX))  )
			{
				closestEnemy=e;
				//closestWall.component.setFill(Color.BLUE);
			}
		}
		return closestEnemy;
	}

	public void checkbottomCollision()
	{
		double charMaxY = mainChar.mainCharField.getBoundsInParent().getMaxY();

		if(charMaxY>=600)
		{
			health.removeHealth();
			mainChar.setStateAlive(false);
		}
	}

	private void movePlatform()
	{
		Platform p = Platform.platformsArray.get(3);
		Duration speed=Duration.millis(10000);
		TranslateTransition tt = new TranslateTransition(speed, p.component);
		tt.setFromX(500);
		tt.setToX(50);
		tt.setCycleCount(Animation.INDEFINITE);
		tt.setAutoReverse(true);
		tt.play();
	}

	public abstract void initStage();
	public abstract void initBackground();

	public void display() 
	{
		stage.setScene(scene);
		stage.centerOnScreen();
	}
}