package application;

import java.util.Random;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.*;

public class World extends GameLoop 
{
	Image rF;
	Rectangle rainforest;
	private static final String rainforestURL = "resources/background.png";

	public World(Stage primaryStage) 
	{
		super(primaryStage);
		super.stageWidth=1000;
		super.stageHeight=600;
	}

	@Override
	public void initBackground() 
	{
		rF = new Image(rainforestURL);
		rainforest = new Rectangle(1000, 600);
		rainforest.setFill(new ImagePattern(rF, 0,0, 1000, 600, false));
		componentsGroup.getChildren().add(rainforest);
	}

	@Override
	public void initStage() 
	{
		for(int i=100; i< 700; i+=100)
		{
			new Platform(componentsGroup,i, i, 150);
		}
		
		new Wall(componentsGroup,460, 325, 20, 75);
		new Wall(componentsGroup,560, 325, 20, 75);
		//So array isnt empty
		new Wall(componentsGroup,-100, -100, 1, 1);
		new PowerUp(componentsGroup, -100, -100,1);
		
		//powerup
		new PowerUp(componentsGroup, 225, 175,1);
		new PowerUp(componentsGroup, 625, 475,3);

		//PowerUp p1= new PowerUp(componentsGroup, 100, 100,1);
		//PowerUp p2= new PowerUp(componentsGroup, 200, 200,2);

		//new Platform(componentsGroup, 100.0, 330.0, 300);
		//new Platform(componentsGroup, 100, 420, 500);
		//new Platform(componentsGroup, 100.0, 510.0, 1000);

		mainChar = new Character(componentsGroup, 3);
		new Enemy(componentsGroup, 200,50);
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