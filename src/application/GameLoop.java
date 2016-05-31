package application;

import java.awt.Component;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
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
	HUD hud;
	PowerUp powerUp;
	Scene scene;
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

	int jumpHeight=100;
	int movementSpeed=20;
	int i = 40;

	double jumpTimer;
	double invincibleTimer;
	double startTime=System.currentTimeMillis();

	double score=0;
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
				score=System.currentTimeMillis()-startTime;
				scene.setOnKeyPressed(k -> actPress(k));
				scene.setOnKeyReleased(k -> actRelease(k));
				if((System.currentTimeMillis()-startTime>5000) && removePlatform)
				{
					Platform p = Platform.getPlatformsArrayList().get(Platform.getPlatformsArrayList().size()-1);
					p.delete();
					removePlatform=false;
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
		//			pr.println("High score: " + result.get()+ " - " + (int)score);
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
		case SPACE:
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
		case C:
			if((getClosestWall().getMinX())-(mainChar.getMaxX()) <= 20)
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

		case W:
			if (mainChar.getStateCanJump())
			{
				//spacePressed=true;
				TranslateTransition ttUp = new TranslateTransition(Duration.millis(1), mainChar.mainCharField);
				ttUp.setByY(-100);
				ttUp.play();
			}
			break;

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

		//closestPlat.component.setFill(Color.ORANGE);

		if ( (charMaxY>=(closestPlat.getMinY()-10)) && ( charMaxY<=(closestPlat.getMinY()+10) ) 
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
			if(p.getType()==3)
			{
				hud.addHealth();
				invincibleTimer=System.currentTimeMillis();
				hasInvincible=true;
				hud.showPowerUp(3);

			}
			if(p.getType()==1 && !hasJump)
			{
				jumpHeight=150;
				jumpTimer=System.currentTimeMillis();
				hud.showPowerUp(1);
				hasJump=true;
			}
			p.delete();

		}
		if(System.currentTimeMillis()-jumpTimer>=10000 && hasJump)//10 seconds
		{
			jumpHeight=75;
			hud.removePowerUp(1);
			hasJump=false;
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
		Duration speed=Duration.millis(5000);
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

		int x= (int)(Math.random()*256)+768;
		int y= (int)(Math.random()*400)+100;

		int platformWidth=200+(int)(Math.random()*(250+1));
		int wallHeight = 150;
		int powType = (int)(Math.random()*3)+1;

		//System.out.println(powType);
		//System.out.println("x: " + x);
		//System.out.println("y: " + y);
		//System.out.println("width: " + width);


		i++;
		//System.out.println( i );

		if( i % 40 == 0)
		{
			Platform p = new Platform(componentsGroup, x, y,platformWidth);
			//PowerUp pow = new PowerUp(componentsGroup, x, (y-15), powType);
			//Wall w = new Wall(componentsGroup, x, y-wallHeight, 20, wallHeight);
			//Enemy e = new Enemy(componentsGroup, x , (y-35));
			int object=1+(int)(Math.random()*(8));
			System.out.println(object);
			PowerUp pow1,pow2,pow3;
			Wall w;
			Enemy e;
			Duration speed=Duration.millis(5000);


			switch( object )
			{
			case 1:
				pow1 = new PowerUp(componentsGroup, x, (y-15), 1);
				TranslateTransition pow1Translate  = new TranslateTransition(speed, pow1.component);
				pow1Translate.setFromX(x+(platformWidth*.75));
				pow1Translate.setToX(-1000+(platformWidth*.75));
				pow1Translate.play();
				break;
			case 2:
				pow2 = new PowerUp(componentsGroup, x, (y-15), 2);
				TranslateTransition pow2Translate  = new TranslateTransition(speed, pow2.component);
				pow2Translate.setFromX(x+(platformWidth*.75));
				pow2Translate.setToX(-1000+(platformWidth*.75));
				pow2Translate.play();
				break;
			case 3: 
				pow3 = new PowerUp(componentsGroup, x, (y-15), 3);
				TranslateTransition pow3Translate  = new TranslateTransition(speed, pow3.component);
				pow3Translate.setFromX(x+(platformWidth*.75));
				pow3Translate.setToX(-1000+(platformWidth*.75));
				pow3Translate.play();
				break;
			case 4:
				w = new Wall(componentsGroup, x, y-wallHeight, 20, wallHeight);
				TranslateTransition wTranslate = new TranslateTransition(speed, w.component);
				wTranslate.setFromX(x+(platformWidth*.75));
				wTranslate.setToX(-1000+(platformWidth*.75)); // 925 and 950
				wTranslate.play();
				break;
			case 5:
				e = new Enemy(componentsGroup, x , (y-35));
				TranslateTransition eTranslate = new TranslateTransition(speed, e.component);
				eTranslate.setFromX(x+(platformWidth*.75));
				eTranslate.setToX(-1000+(platformWidth*.75));
				eTranslate.play();
				break;
			default:
				break;

			}

			TranslateTransition platTranslate  = new TranslateTransition(speed, p.component);

			platTranslate.setFromX(x);
			platTranslate.setToX(-1000);
			platTranslate.play();
			//System.out.println("Array size: " + Platform.getPlatformsArrayList().size());
		}
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