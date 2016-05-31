package application;

import java.util.Random;

import javafx.animation.Interpolator;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class World extends GameLoop 
{
	Image rF;
	Rectangle rainforest;
	//	private static final String rainforestURL = "resources/greenBackground.png";
	private static final Image RAINFOREST = new Image("resources/greenBackground.png");
	protected ImageView background;

	public World(Stage primaryStage) 
	{
		super(primaryStage);
		super.stageWidth=1000;
		super.stageHeight=600;
	}

	@Override
	public void initBackground() 
	{
		background = new ImageView(RAINFOREST);
		TranslateTransition tt = new TranslateTransition(Duration.millis(10000), background);
		tt.setFromX(0);
		tt.setToX(-1024);
		tt.setInterpolator( Interpolator.LINEAR );
		tt.setCycleCount(Timeline.INDEFINITE);
		componentsGroup.getChildren().add(background);
		tt.play();
	}

	@Override
	public void initStage() 
	{
		//		for(int i=100; i< 700; i+=100)
		//		{s
		//			new Platform(componentsGroup,i, i, 150);
		//		}

		//new Wall(componentsGroup,460, 325, 20, 75);
		//new Wall(componentsGroup,560, 325, 20, 75);
		//So array isnt empty
		new Platform(componentsGroup,100, 100, 100);
		new Wall(componentsGroup,2000, 2000, 1, 1);
		new PowerUp(componentsGroup, 2000, 2000,1);

		//powerup
		//new PowerUp(componentsGroup, 225, 175,1);
		//new PowerUp(componentsGroup, 625, 475,3);

		//PowerUp p1= new PowerUp(componentsGroup, 100, 100,1);
		//PowerUp p2= new PowerUp(componentsGroup, 200, 200,2);

		//new Platform(componentsGroup, 100.0, 330.0, 300);
		//new Platform(componentsGroup, 100, 420, 500);
		//new Platform(componentsGroup, 100.0, 510.0, 1000);

		mainChar = new Character(componentsGroup, 3);
		//new Enemy(componentsGroup, 200,50);
		hud = new HUD(componentsGroup, 3);
	}

	public static void createPlatform(Group componentsGroup, Random n)
	{

		new Platform(componentsGroup, 900,(n.nextInt(20))*40,100);

	}

	public void display()
	{
		stage.setScene(scene);
		stage.centerOnScreen();
	}
}