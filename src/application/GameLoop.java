package application;

import java.io.File;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.Random;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
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
	Random r= new Random();


	private static final Image DEATH = new Image("resources/dead.png");
	boolean falling=true;
	boolean cheatMode=false;
	boolean gotHealth=false;
	boolean gotHurt=false;
	boolean jumping=false;

	boolean dPressed=false;
	boolean aPressed=false;
	boolean checkDeath=false;

	int jumpHeight=100;
	int movementSpeed=20;

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
				if(hud.healthCount.size()>0)
				{
					gravity();
					//moveChar();
					checkForPlatformCollisions();
					checkForWallCollisions();
					checkbottomCollision();
					checkPowerUpCollision();
					checkEnemieCollision();
					scene.setOnKeyPressed(k -> actPress(k));
					scene.setOnKeyReleased(k -> actRelease(k));
					

//					System.out.println(hud.healthCount.size());
				}
				else
				{
					if((hud.healthCount.size() <= 0)) {
						
					}
//					this.stop();
//					if((hud.healthCount.size())<=0)
//					{ 
//						System.out.println("here");
//						score=System.currentTimeMillis()-startTime;
//						TextInputDialog dialog = new TextInputDialog();
//						dialog.setTitle("Score");
//						dialog.setHeaderText("Score");
//						dialog.setContentText("Please enter your name:");
//						Optional<String> result = dialog.showAndWait();
//						try 
//						{
//							File highScores = new File("src/resources/highScores.txt");
//							PrintWriter pr = new PrintWriter(highScores);
//							pr.println("High score: " + result.get()+ " - " + (int)score);
//							pr.close();
//						}
//						catch(Exception e)
//						{
//							System.out.println("FILE ERROR");
//						}
//					}
				}

			}
		}.start();


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
			case ESCAPE:
				stage.close();
				break;
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
					movementSpeed=2;
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
			mainChar.setStateCanJump(false);
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
			if(p.getType()==1)
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
		}
		if(System.currentTimeMillis()-invincibleTimer>=5000 && hasInvincible)//5 seconds
		{
			//MAKE INVINSIBLE...SOMEHOW
			hud.removePowerUp(3);
		}
	}
	public void checkEnemieCollision()
	{
		Enemy e = getClosestEnemie();
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
			
			hud.removeHealth();
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
	
	public void showDeathScreen(Group g) {
		ImageView death = new ImageView(DEATH);
		g.getChildren().add(death);
		
		Button backToMenu = new Button("Back To Menu");
		backToMenu.setLayoutX(450);
		backToMenu.setLayoutY(500);
		backToMenu.setFont(new Font("Roboto", 15));
		backToMenu.setPrefSize(150, 50);
		backToMenu.setOnAction(e -> backToMenuScreen(stage));	
		componentsGroup.getChildren().add(backToMenu);
	}
	
	public void backToMenuScreen(Stage stage) {
		new Menu(stage).displayMenu();
	}
}