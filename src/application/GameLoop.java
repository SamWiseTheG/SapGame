package application;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Scanner;
import javafx.animation.AnimationTimer;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public abstract class GameLoop
{
	Enemy e;
	HUD hud;
	PowerUp powerUp;
	Scene scene;
	World world;
	Stage stage;
	Group root;
	Group componentsGroup;
	Rectangle rainforest;
	Platform platformOne;
	Image rF;
	Character mainChar;

	boolean spacePressed=false;
	boolean cheatMode=false;
	boolean checkDeath=false;
	
	boolean removePlatform=true;
	boolean firstPlatform=true;
	boolean morePlatforms=false;
	
	double jumpTimer;
	boolean hasScoreUp = false;
	boolean hasJump=false;
	boolean gotHealth=false;
	boolean gotHurt=false;
	boolean jumping=false;
	boolean falling=true;



	double stageWidth;
	double stageHeight;
	
	int gameSpeed = 7500;
	int jumpHeight=150;
	int movementSpeed=20;
	int spawnLogic = 59;

	String currentScore;
	double scoreTimer;
	double startTime=(double)System.currentTimeMillis()/1000;
	double score=0;
	double addedScore=0;

	private static final Image DEATH = new Image("resources/dead.png");
	private static final Image OLDDEATH = new Image("resources/oldDead.png");
	
	private final Media media = new Media(Paths.get("src/resources/MySong2.mp3").toUri().toString());
	private final MediaPlayer mediaPlayer = new MediaPlayer(media);

	public GameLoop(Stage primaryStage)
	{
		stage=primaryStage;
		root = new Group();
		componentsGroup = new Group();
		playMusic();
		initBackground();
		initStage();
		root.getChildren().add(componentsGroup);
		scene = new Scene (root, 1000, 600);

		new AnimationTimer()
		{
			public void handle(long now)
			{
				gravity();
				checkRunning();
				checkPlatformCollisions();
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
					componentsGroup.getChildren().clear();
					showDeathScreen( componentsGroup );
				}
			}
		}.start();
	}

	private void actRelease(KeyEvent k)
	{
		switch (k.getCode())
		{
			case E:
				cheatMode = false;
				mainChar.loadRunning(componentsGroup);
				break;
			case SPACE:


			default:
				break;
		}
	}

	private void actPress(KeyEvent k)
	{
		switch(k.getCode())				
		{
			case ESCAPE:
				stage.close();
				break;
			case W:
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
					st.play();
					st.setOnFinished(new EventHandler<ActionEvent>(){
						public void handle(ActionEvent arg0) 
						{
							falling=true;
							jumping=false;
							spacePressed=true;
						}
					});	
				}
				break;
			case E:
				if(((getClosestWall().getMinX())-(mainChar.getMaxX()) <= 50) 
						&& (mainChar.getMaxY()>=getClosestWall().getMaxY())
						&& (mainChar.getMinY()<=getClosestPlatform().getMinY()))
				{
					mainChar.punch(getClosestWall());
					cheatMode = true;
					mainChar.loadPunch();
					cheatMode=false;
				}
				break;
			case G:
				if(cheatMode)
				{
					movementSpeed=30;
					cheatMode=false;
				}
				else
				{
					movementSpeed=30;
					cheatMode=true;
				}
				break;
			case D:
				TranslateTransition rightTransition = new TranslateTransition(Duration.millis(1), mainChar.mainCharField);
				rightTransition.setByX(movementSpeed);
				rightTransition.play();
				break;
			case A:
				TranslateTransition leftTransition = new TranslateTransition(Duration.millis(1), mainChar.mainCharField);
				leftTransition.setByX(-movementSpeed);
				leftTransition.play();
				break;
			case S:
				if(!mainChar.isStateOnPlatform())
				{
					TranslateTransition downTransition = new TranslateTransition(Duration.millis(1), mainChar.mainCharField);
					downTransition.setByY(movementSpeed);
					downTransition.play();
				}
			default:
				break;
		}				
	}

	private void gravity()
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

	private void canJump() 
	{
		spacePressed=false;
	}

	private void checkPlatformCollisions()
	{
		Platform closestPlat = getClosestPlatform();

		double charMaxY = mainChar.getMaxY();
		double charMinX = mainChar.getMinX();
		double charMaxX = mainChar.getMaxX();

		if ( (charMaxY>=(closestPlat.getMinY()-5)) && ( charMaxY<=(closestPlat.getMinY()+5) ) 
				&& ( charMinX+21 <= closestPlat.getMaxX()) 
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
			falling=true;
		}
	}

	private Platform getClosestPlatform()
	{
		Platform closestPlat = Platform.platformsArray.get(0);

		double charMaxY = mainChar.mainCharField.getBoundsInParent().getMaxY();

		for (Platform p : Platform.getPlatformsArrayList()) 
		{
			if( (Math.abs(p.getMinY()-charMaxY) )<=(Math.abs(closestPlat.getMinY()-charMaxY))  )
			{
				closestPlat=p;
			}
		}
		return closestPlat;
	}

	private void checkForWallCollisions()
	{
		Wall closestWall = getClosestWall();

		boolean wallCollision = (mainChar.mainCharField.getBoundsInParent().intersects(closestWall.getBounds()));
		if (wallCollision)
		{
			closestWall.breakWall(closestWall);
			hud.removeHealth();
		}
	}

	private Wall getClosestWall()
	{
		Wall closestWall = Wall.wallsArray.get(0);

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

	private void checkPowerUpCollision()
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
	}

	private void checkEnemieCollision()
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

	private PowerUp getClosestPowerUp()
	{
		PowerUp closestPowerUp = PowerUp.powerUpArray.get(0);

		double charMinX = mainChar.mainCharField.getBoundsInParent().getMinX();
		double charMaxX = mainChar.mainCharField.getBoundsInParent().getMaxX();

		double charCenterX =  charMaxX-((charMaxX-charMinX)/2);

		for (PowerUp p : PowerUp.getPowerUpArrayList()) 
		{
			if( (Math.abs(p.getBounds().getMinX()-charCenterX) )<=(Math.abs(closestPowerUp.getBounds().getMinX()-charCenterX))  )
			{
				closestPowerUp=p;
			}
		}
		return closestPowerUp;
	}

	private Enemy getClosestEnemy()
	{
		Enemy closestEnemy = Enemy.enemiesArray.get(0);
		double charMinX = mainChar.mainCharField.getBoundsInParent().getMinX();
		double charMaxX = mainChar.mainCharField.getBoundsInParent().getMaxX();

		double charCenterX =  charMaxX-((charMaxX-charMinX)/2);

		for (Enemy e : Enemy.getEnemiesArrayList()) 
		{
			if( (Math.abs(e.getBounds().getMinX()-charCenterX) )<=(Math.abs(closestEnemy.getBounds().getMinX()-charCenterX))  )
			{
				closestEnemy=e;
			}
		}
		return closestEnemy;
	}

	private void checkbottomCollision()
	{
		double charMinY = mainChar.mainCharField.getBoundsInParent().getMinY();

		if(charMinY>=600)
		{
			hud.removeHealth();
			mainChar.setStateAlive(false);
		}
	}

	private void generateObjects()
	{
		int x= (int)(Math.random()*256)+1024;
		int y= (int)(Math.random()*400)+100;
		int platformWidth=150+(int)(Math.random()*(200));
		int wallHeight = 150;
		int randBinary = (int)(Math.random()*2);
		int nextPlatformHight = 130; 
		spawnLogic++;

		if( spawnLogic % 60 == 0)
		{
			int object=1+(int)(Math.random()*(15));
			boolean moveUp=false;
			PowerUp pow;
			Wall w;
			Enemy e;
			Duration speed=Duration.millis(gameSpeed);
			Platform p;

			if(firstPlatform)
			{
				platformOne = new Platform(componentsGroup, x, y,platformWidth);
				translatePlatform(speed, platformOne, x);
				firstPlatform=false;
			}
			int platformTop = (int)(Platform.getPlatformsArrayList().get(0).getMaxY());

			if(morePlatforms)
			{
				if(randBinary==1)
				{
					if(platformTop<=200)
					{
						p = new Platform(componentsGroup, x, platformTop+nextPlatformHight, platformWidth);
						translatePlatform(speed, p, x);
						moveUp=true;
					}
					else 
					{
						p = new Platform(componentsGroup, x, platformTop-nextPlatformHight, platformWidth);
						translatePlatform(speed, p, x);
						moveUp=false;
					}
				}
				else
				{
					if(platformTop>=400)
					{
						p = new Platform(componentsGroup, x, platformTop-nextPlatformHight, platformWidth);
						translatePlatform(speed, p, x);
						moveUp=false;
					}
					else 
					{
						p = new Platform(componentsGroup, x, platformTop+nextPlatformHight, platformWidth);
						translatePlatform(speed, p, x);
						moveUp=true;
					}
				}
				switch( object )
				{
					case 1:
						if(moveUp)
						{
							pow = new PowerUp(componentsGroup, x, platformTop+nextPlatformHight-40, 1);
							translatePowerUp(speed, pow, x, platformWidth);
						}
						else 
						{
							pow = new PowerUp(componentsGroup, x, platformTop-nextPlatformHight-40, 1);
							translatePowerUp(speed, pow, x, platformWidth);
						}
						break;
					case 2:
						if(moveUp)
						{
							pow = new PowerUp(componentsGroup, x, platformTop+nextPlatformHight-40, 2);
							translatePowerUp(speed, pow, x, platformWidth);
						}
						else 
						{
							pow = new PowerUp(componentsGroup, x, platformTop-nextPlatformHight-40, 2);
							translatePowerUp(speed, pow, x, platformWidth);
						}
						break;
					case 3: 
						if(moveUp)
						{
							pow = new PowerUp(componentsGroup, x, platformTop+nextPlatformHight-40, 3);
							translatePowerUp(speed, pow, x, platformWidth);
						}
						else 
						{
							pow = new PowerUp(componentsGroup, x, platformTop-nextPlatformHight-40, 3);
							translatePowerUp(speed, pow, x, platformWidth);
						}
						break;
					case 4:
					case 5:
					case 6:
						if(moveUp)
						{
							w = new Wall(componentsGroup, x, platformTop+nextPlatformHight-wallHeight, 20, wallHeight);
							translateWall(speed, w, x, platformWidth);
						}
						else 
						{
							w = new Wall(componentsGroup, x, platformTop-nextPlatformHight-wallHeight, 20, wallHeight);
							translateWall(speed, w, x, platformWidth);
						}
						break;
					case 7:
					case 8:
						if(moveUp)
						{
							e = new Enemy(componentsGroup, x+50 , platformTop+nextPlatformHight-35);
							translateEnemy( speed, e, x, platformWidth);
						}
						else 
						{
							e = new Enemy(componentsGroup, x+50 , platformTop-nextPlatformHight-35);
							translateEnemy( speed, e, x, platformWidth);
						}
						break;
					default:
						break;
				}
			}
			morePlatforms=true;
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

	private void translatePlatform(Duration speed, Platform p, int x)
	{
		TranslateTransition platTranslate  = new TranslateTransition(speed, p.component);
		platTranslate.setFromX(x);
		platTranslate.setToX(-1000);
		platTranslate.play();
	}
	private void translatePowerUp(Duration speed, PowerUp pow, int x,int platformWidth)
	{
		TranslateTransition powTranslate  = new TranslateTransition(speed, pow.component);
		powTranslate.setFromX(x+(platformWidth*.75));
		powTranslate.setToX(-1000+(platformWidth*.75));
		powTranslate.play();
	}
	private void translateWall(Duration speed, Wall w, int x,int platformWidth)
	{
		TranslateTransition wTranslate = new TranslateTransition(speed, w.component);
		wTranslate.setFromX(x+(platformWidth*.75));
		wTranslate.setToX(-1000+(platformWidth*.75)); // 925 and 950
		wTranslate.play();
	}
	private void translateEnemy(Duration speed, Enemy e, int x, int platformWidth)
	{
		TranslateTransition eTranslate = new TranslateTransition(speed, e.component);
		eTranslate.setFromX(x+(platformWidth*.75));
		eTranslate.setToX(-1000+(platformWidth*.75));
		eTranslate.play();
	}



	private void playMusic()
	{
		mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
		mediaPlayer.play();
	}

	private void checkRunning()
	{
		if(!mainChar.getStateCanJump())
		{
			mainChar.loadJump();
		}
		else if( mainChar.isStateRunning() == false)
		{
			mainChar.loadRunning(componentsGroup);
		}
	}

	private void backToMenuScreen(Stage stage) 
	{
		new Menu(stage).displayMenu();
	} 

	private void showDeathScreen(Group g) {
		ImageView death = new ImageView(DEATH);
		ImageView oldDeath = new ImageView(OLDDEATH);

		Button backToMenu = new Button("Menu");
		Button submitScore = new Button("Submit");

		backToMenu.setLayoutX(450);
		backToMenu.setLayoutY(500);
		backToMenu.setFont(new Font("Roboto", 15));
		backToMenu.setStyle("-fx-base: #AA0121");
		backToMenu.setTextFill(Color.BISQUE);
		backToMenu.setPrefSize(100, 50);
		backToMenu.setOnAction(e -> backToMenuScreen(stage));

		TextField highScoreInput = new TextField();
		highScoreInput.setLayoutX(350);
		highScoreInput.setLayoutY(450);

		submitScore.setLayoutX(550);
		submitScore.setLayoutY(450);
		submitScore.setFont(new Font("Roboto", 15));
		submitScore.setPrefSize(100, 25);
		submitScore.setOnAction(e -> editHighScore(highScoreInput.getText()));
		mediaPlayer.stop();

		if(checkHighScore())
		{
			g.getChildren().add(death);
			g.getChildren().add(highScoreInput);
			g.getChildren().add(submitScore);
		}
		else 
		{
			g.getChildren().add(oldDeath);
			g.getChildren().add(backToMenu);

		}
	}

	private boolean checkHighScore() 
	{
		String highScoreString="";
		double highScoreDouble =0;

		try 
		{
			File highScoreFile = new File("src/resources/highScores.txt");
			Scanner input =new Scanner(highScoreFile);
			highScoreString = input.nextLine();
			input.close();
		}
		catch(Exception e)
		{

		}
		highScoreDouble=(Double.parseDouble(highScoreString.replaceAll("[\\D]", ""))/10);

		Double finalHighScore = Double.parseDouble(currentScore);
		if(finalHighScore>highScoreDouble)
		{
			return true;
		}
		else 
		{
			return false;
		}
	}

	private void editHighScore(String highScoreName)
	{
		try 
		{
			File highScoreFile = new File("src/resources/highScores.txt");
			PrintWriter pr = new PrintWriter(highScoreFile);
			pr.println("High score: " + highScoreName+ " - " + currentScore);
			pr.close();
		}
		catch(Exception e)
		{
			System.out.println("FILE ERROR");
		}
		backToMenuScreen(stage);
	}

	public abstract void initStage();
	public abstract void initBackground();
}