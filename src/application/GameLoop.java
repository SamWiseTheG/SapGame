package application;

import java.awt.Component;
import java.text.DecimalFormat;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public abstract class GameLoop
{
	private boolean spacePressed=false;
	double stageWidth;
	double stageHeight;
	Enemy e;
	HUD hud;
	PowerUp powerUp;
	Scene scene;
	World world;
	Stage stage;
	Group root;
	Group componentsGroup;
	Rectangle rainforest;
	Image rF;
	Character mainChar;
	//Menu m;
	//
	boolean falling=true;
	boolean cheatMode=false;
	boolean gotHealth=false;
	boolean gotHurt=false;
	boolean jumping=false;

	boolean dPressed=false;
	boolean aPressed=false;
	boolean checkDeath=false;
	boolean removePlatform=true;
	boolean firstPlatform=true;
	boolean morePlatforms=false;
	boolean hasScoreUp = false;

	int gameSpeed = 7500;
	int jumpHeight=150;
	int movementSpeed=20;
	int spawnLogic = 59;

	double jumpTimer;
	double scoreTimer;
	double invincibleTimer;
	double startTime=(double)System.currentTimeMillis()/1000;

	double score=0;
	double addedScore=0;
	String currentScore;
	boolean hasJump=false;
	boolean hasInvincible=false;

	////Make Platforms move
	//	TranslateTransition tPlatform;
	//	ParallelTransition ptGravity;
	//	ParallelTransition ptJumping;
	/////// I commented out the few lines that deal with it cuz it barely works

	public GameLoop(Stage primaryStage)
	{
		System.out.println("here");

		stage=primaryStage;
		root = new Group();
		componentsGroup = new Group();

		initBackground();
		initStage();
		root.getChildren().add(componentsGroup);
		scene = new Scene (root, 1000, 600);
		//movePlatform();
		//tPlatform = new TranslateTransition(Duration.seconds(5), f.component);
		//tPlatform.setByX(-1000);
		
		//-----------KeyEvents--------------//
		new AnimationTimer()
		{
			public void handle(long now)
			{
//				if(hud.healthCount.size()>0)
//				{
				gravity();
				//moveChar();
				checkForPlatformCollisions();
				checkForWallCollisions();
				checkbottomCollision();
				generateObjects();
				deleteObjects();
				checkPowerUpCollision();
				checkEnemieCollision();
				score=(((double)System.currentTimeMillis()/1000)-startTime);
				DecimalFormat df = new DecimalFormat("#0.0");
				currentScore = df.format( score + addedScore );
				hud.setScore(currentScore);
				System.out.println(currentScore);
				
				scene.setOnKeyPressed(k -> actPress(k));
				scene.setOnKeyReleased(k -> actRelease(k));
				if((System.currentTimeMillis()-startTime*1000>5000) && removePlatform)
				{
					Platform p = Platform.getPlatformsArrayList().get(Platform.getPlatformsArrayList().size()-1);
					p.delete();
					removePlatform=false;
				}
				if( hud.healthCount.size() <= 0 )
				{
					stop();
				}
				//System.out.println(hud.healthCount.size());
				//				}
				//				else 
				//				{
				//					this.stop();
				//				}
				//}
			}
		}.start();
		
		//System.out.println("here");
		//score=System.currentTimeMillis()-startTime;
		//		TextInputDialog dialog = new TextInputDialog();
		//		dialog.setTitle("Score");
		//		dialog.setHeaderText("Score");
		//		dialog.setContentText("Please enter your name:");
		//		Optional<String> result = dialog.showAndWait();
		//		try 
		//		{
		//			File highScores = new File("src/resources/highScores.txt");
		//			PrintWriter pr = new PrintWriter(highScores);
		//			pr.println("High score: " + result.get()+ " - " + (int)score%1000);
		//			pr.close();
		//		}
		//		catch(Exception e)
		//		{
		//			System.out.println("FILE ERROR");
		//		}
	}

	public void stop(KeyEvent k, Rectangle r)
	{
		//		if(k.getCode()==KeyCode.A)
		//			spacePressed=false;
	}

	public void actRelease(KeyEvent k)
	{
		switch (k.getCode())
		{
			case D:
				dPressed=false;
				break;
			case A:
				aPressed=false;
				break;
			default:
				break;
		}
	}
	public void actPress(KeyEvent k)
	{
		switch(k.getCode())				
		{
			case W:
				if(!spacePressed && mainChar.getStateCanJump())
				{
					jumping=true;
					TranslateTransition t1 = new TranslateTransition(Duration.millis(300),mainChar.mainCharField);
					t1.setByY(-jumpHeight);

					TranslateTransition t2 = new TranslateTransition(Duration.millis(100),mainChar.mainCharField);
					t2.setByY(0);

					SequentialTransition st = new SequentialTransition();
					TranslateTransition moveChar = new TranslateTransition(Duration.millis(100), mainChar.mainCharField);

					moveChar.setByX(movementSpeed*2);
					st.getChildren().addAll(t1,t2);
					//					if(dPressed)
					//					{
					//						moveChar.play();
					//						st.play();
					//						st.setOnFinished(new EventHandler<ActionEvent>(){
					//							public void handle(ActionEvent arg0) 
					//							{
					//								falling=true;
					//								jumping=false;
					//								spacePressed=true;
					//							}
					//						});
					//					}
					//					else
					//					{
					st.play();
					st.setOnFinished(new EventHandler<ActionEvent>(){
						public void handle(ActionEvent arg0) 
						{
							falling=true;
							jumping=false;
							spacePressed=true;
						}
					});	
					//}
				}

				break;

				//punch
			case E:
				if((getClosestWall().getMinX())-(mainChar.getMaxX()) <= 30 )
				{
					//					mainChar.loadPunch();
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
					movementSpeed=30;
					cheatMode=true;
				}
				break;
				//movement keys
			case D:
				dPressed=true;
				TranslateTransition ttt = new TranslateTransition(Duration.millis(1), mainChar.mainCharField);
				ttt.setByX(movementSpeed);
				ttt.play();
				break;

			case A:
				aPressed=true;
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

		//closestPlat.component.setFill(Color.ORANGE);

		if ( (charMaxY>=(closestPlat.getMinY()-5)) && ( charMaxY<=(closestPlat.getMinY()+10) ) 
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
			mainChar.setStateCanJump(false);
			//closestPlat.component.setFill(Color.DARKMAGENTA);
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
				//closestPlat.component.setFill(Color.BLUE);
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
			hud.removeHealth();
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
			if(p.getType()==1 && !hasJump)
			{
				jumpHeight=200;
				jumpTimer=System.currentTimeMillis();
				hud.showPowerUp(1);
				hasJump=true;
			}
			if(p.getType()==2 && !hasScoreUp)
			{
				addedScore += 10;
				hud.showPowerUp(2);
				hasScoreUp = true;
			}
			if(p.getType()==3)
			{
				hud.addHealth();
				//invincibleTimer=System.currentTimeMillis();
				//hasInvincible=true;
				//hud.showPowerUp(3);

			}
			p.delete();

		}
		
		if (!mainChar.mainCharField.getBoundsInParent().intersects(p.getBounds()))
		{
		hasScoreUp=false;
		}
		
		if(System.currentTimeMillis()-jumpTimer>=10000 && hasJump)//10 seconds
		{
			jumpHeight=150;
			hud.removePowerUp(1);
			hasJump=false;
		}
		if(System.currentTimeMillis()-scoreTimer>=5000 && hasScoreUp)//5 seconds
		{
			hud.removePowerUp(2);
			hasScoreUp=false;
		}
		if(System.currentTimeMillis()-invincibleTimer>=5000 && hasInvincible)//5 seconds
		{
			//MAKE INVINSIBLE...SOMEHOW
			hud.removePowerUp(3);
		}
	}
	public void checkEnemieCollision()
	{
		Enemy e = getClosestEnemy();
		if (mainChar.mainCharField.getBoundsInParent().intersects(e.getBounds()))
		{
			if(!gotHurt)
			{
				hud.removeHealth();
				gotHurt=true;
			}		
		}
		if (!(mainChar.mainCharField.getBoundsInParent().intersects(e.getBounds())))
		{
				gotHurt=false;	
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

	public Enemy getClosestEnemy()
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
		double charMinY = mainChar.mainCharField.getBoundsInParent().getMinY();

		if(charMinY>=600)
		{
			hud.removeHealth();
			mainChar.setStateAlive(false);
		}
	}

	private void movePlatform()
	{
		Platform p = Platform.platformsArray.get(0);
		Duration speed=Duration.millis(gameSpeed);
		TranslateTransition tt = new TranslateTransition(speed, p.component);

		tt.setFromX(500);
		tt.setToX(-200);
		tt.setCycleCount(Animation.INDEFINITE);
		tt.setAutoReverse(true);
		tt.play();
	}
	private void generateObjects()
	{
		//double charMaxY = mainChar.mainCharField.getBoundsInParent().getMaxY();
		//double charMinY = mainChar.mainCharField.getBoundsInParent().getMaxY();
		//double charMinX = mainChar.mainCharField.getBoundsInParent().getMinX();
		//double charMaxX = mainChar.mainCharField.getBoundsInParent().getMaxX();

		//Min + (int)(Math.random() * ((Max - Min) + 1))
		//int x=(400+(int)(charMaxX+ (Math.random() * ((400)) + 1)));

		int x= (int)(Math.random()*256)+1024;
		int y= (int)(Math.random()*400)+100;

		int platformWidth=150+(int)(Math.random()*(200));
		int wallHeight = 150;
		int randBinary = (int)(Math.random()*2);

		//System.out.println(randBinary);
		//int powType = (int)(Math.random()*3)+1;
		//System.out.println(powType);
		//System.out.println("x: " + x);
		//System.out.println("y: " + y);
		//System.out.println("width: " + width);


		spawnLogic++;
		//System.out.println( i );

		if( spawnLogic % 60 == 0)
		{

			//PowerUp pow = new PowerUp(componentsGroup, x, (y-15), powType);
			//Wall w = new Wall(componentsGroup, x, y-wallHeight, 20, wallHeight);
			//Enemy e = new Enemy(componentsGroup, x , (y-35));

			int object=1+(int)(Math.random()*(15));

			boolean moveUp=false;

			//System.out.println(object);
			PowerUp pow;
			Wall w;
			Enemy e;
			Duration speed=Duration.millis(gameSpeed);

			Platform p;

			if(firstPlatform)
			{
				p = new Platform(componentsGroup, x, y,platformWidth);
				translatePlatform(speed, p, x);
				firstPlatform=false;
			}

			int platformTop = (int)(Platform.getPlatformsArrayList().get(0).getMaxY());
			//System.out.println(platformTop);

			if(morePlatforms)
			{
				if(randBinary==1)
				{
					if(platformTop<=200)
					{
						p = new Platform(componentsGroup, x, platformTop+150, platformWidth);
						translatePlatform(speed, p, x);
						moveUp=true;
					}
					else 
					{
						p = new Platform(componentsGroup, x, platformTop-150, platformWidth);
						translatePlatform(speed, p, x);
						moveUp=false;
					}
				}
				else
				{
					if(platformTop>=400)
					{
						p = new Platform(componentsGroup, x, platformTop-150, platformWidth);
						translatePlatform(speed, p, x);
						moveUp=false;
					}
					else 
					{
						p = new Platform(componentsGroup, x, platformTop+150, platformWidth);
						translatePlatform(speed, p, x);
						moveUp=true;
					}			
				}				
				switch( object )
				{
					case 1:
						if(moveUp)
						{
							pow = new PowerUp(componentsGroup, x, platformTop+150-15, 1);
							translatePowerUp(speed, pow, x, platformWidth);
						}
						else 
						{
							pow = new PowerUp(componentsGroup, x, platformTop-150-15, 1);
							translatePowerUp(speed, pow, x, platformWidth);
						}
						break;
					case 2:
						if(moveUp)
						{
							pow = new PowerUp(componentsGroup, x, platformTop+150-15, 2);
							translatePowerUp(speed, pow, x, platformWidth);
						}
						else 
						{
							pow = new PowerUp(componentsGroup, x, platformTop-150-15, 2);
							translatePowerUp(speed, pow, x, platformWidth);
						}
						break;
					case 3: 
						if(moveUp)
						{
							pow = new PowerUp(componentsGroup, x, platformTop+150-15, 3);
							translatePowerUp(speed, pow, x, platformWidth);
						}
						else 
						{
							pow = new PowerUp(componentsGroup, x, platformTop-150-15, 3);
							translatePowerUp(speed, pow, x, platformWidth);
						}
						break;
					case 4:
					case 5:
					case 6:
						if(moveUp)
						{
							w = new Wall(componentsGroup, x, platformTop+150-wallHeight, 20, wallHeight);
							translateWall(speed, w, x, platformWidth);
						}
						else 
						{
							w = new Wall(componentsGroup, x, platformTop-150-wallHeight, 20, wallHeight);
							translateWall(speed, w, x, platformWidth);
						}
						break;
					case 7:
					case 8:
						if(moveUp)
						{
							e = new Enemy(componentsGroup, x , platformTop+150-35);
							translateEnemy( speed, e, x, platformWidth);
						}
						else 
						{
							e = new Enemy(componentsGroup, x , platformTop-150-35);
							translateEnemy( speed, e, x, platformWidth);
						}
						break;
					default:
						break;

				}
			}
			morePlatforms=true;

			//System.out.println("Array size: " + Platform.getPlatformsArrayList().size());
		}
	}

	public void translatePlatform(Duration speed, Platform p, int x)
	{
		TranslateTransition platTranslate  = new TranslateTransition(speed, p.component);
		platTranslate.setFromX(x);
		platTranslate.setToX(-1000);
		platTranslate.play();
	}
	public void translatePowerUp(Duration speed, PowerUp pow, int x,int platformWidth)
	{
		TranslateTransition powTranslate  = new TranslateTransition(speed, pow.component);
		powTranslate.setFromX(x+(platformWidth*.75));
		powTranslate.setToX(-1000+(platformWidth*.75));
		powTranslate.play();
	}
	public void translateWall(Duration speed, Wall w, int x,int platformWidth)
	{
		TranslateTransition wTranslate = new TranslateTransition(speed, w.component);
		wTranslate.setFromX(x+(platformWidth*.75));
		wTranslate.setToX(-1000+(platformWidth*.75)); // 925 and 950
		wTranslate.play();
	}
	public void translateEnemy(Duration speed, Enemy e, int x, int platformWidth)
	{
		TranslateTransition eTranslate = new TranslateTransition(speed, e.component);
		eTranslate.setFromX(x+(platformWidth*.75));
		eTranslate.setToX(-1000+(platformWidth*.75));
		eTranslate.play();
	}

	private void deleteObjects()
	{
		try 
		{
			if(Platform.getPlatformsArrayList().size()>0)
			{
				for(Platform p : Platform.getPlatformsArrayList())
				{
					if(p.getMaxX()<-5)
					{
						p.delete();
					}
				}
			}
			if(PowerUp.getPowerUpArrayList().size()>0)
			{
				for(PowerUp pow : PowerUp.getPowerUpArrayList())
				{
					if(pow.getBounds().getMaxX()<-5)
					{
						pow.delete();
					}
				}
			}
			if(Wall.getWall().size()>0)
			{
				for(Wall w : Wall.getWall())
				{
					if(w.getMaxX()<-5)
					{
						w.breakWall(w);
					}
				}
			}
			if(Enemy.getEnemiesArrayList().size()>0)
			{
				for(Enemy e : Enemy.getEnemiesArrayList())
				{
					if(e.getMaxX()<-5)
					{
						e.delete();
					}
				}
			}
		}
		catch(Exception e)
		{

		}
	}

	public void moveChar() 
	{
		TranslateTransition moveChar = new TranslateTransition(Duration.millis(100), mainChar.mainCharField);
		moveChar.setCycleCount(Animation.INDEFINITE);
		moveChar.setByX(movementSpeed);
		moveChar.setAutoReverse(true);
		moveChar.play();
	}

	public abstract void initStage();
	public abstract void initBackground();

	public void display() 
	{
		stage.setScene(scene);
		stage.centerOnScreen();
	}
}