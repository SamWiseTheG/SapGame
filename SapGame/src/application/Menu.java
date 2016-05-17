package application;

import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class Menu
{
	Stage stage = new Stage();
	Scene menuScene;
	
	public Menu (Stage primaryStage)
	{
		Group menuRoot = new Group();
		Image vr = new Image("resources/background.png", 1000, 600, false, false);
		ImageView iv = new ImageView(vr);		
		//Rectangle r = new Rectangle(5000,5000);
			//r.setFill(Color.valueOf("#EFE4B0"));  //#EFE4B0 (originally SALMON)
			//r.setStyle("-fx-base: #EFE4B0");
		Button play = new Button ("Play!");
			play.setLayoutX(250);
			play.setLayoutY(100);
			play.setPrefSize(100, 50);
		Button instructions = new Button ("Instructions");
			instructions.setLayoutX(750);
			instructions.setLayoutY(100);
			instructions.setStyle("-fx-base: #0099ff");
			instructions.setTextFill(Color.BLACK);
			instructions.setPrefSize(100, 50);
		Button credits = new Button ("Credits");
			credits.setLayoutX(250);
			credits.setLayoutY(400);
			credits.setStyle("-fx-base: #22ff00");
			credits.setPrefSize(100, 50);
		Button close = new Button ("Close");
			close.setLayoutX(750);
			close.setLayoutY(400);
			close.setStyle("-fx-base: #ff00ff");
			close.setTextFill(Color.BLACK);
			close.setPrefSize(100, 50);
		Text title = new Text (250,250,"Super Awesome Platformer!!!!!");
			//title.setLayoutX(250);
			title.setFill(Color.ORANGE);
			title.setFont(Font.font ("Verdana", FontWeight.BOLD, 30));
		Button back = new Button("back");		
			
		close.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle (ActionEvent event)
			{
				primaryStage.close();
			}
		});
		
		play.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle (ActionEvent event)
			{
				new World(primaryStage).display();	
			}
		});
					
		menuRoot.getChildren().addAll(iv, play, instructions, credits, close, title);		
		menuScene = new Scene (menuRoot, 1000, 600);
		
		primaryStage.setScene(menuScene);
		stage.setTitle("SUPER AWESOME PLATFORMER!!!!");
		stage.setResizable(false);
		stage=primaryStage;
		stage.show();
		
		back.setOnAction(e -> setprimaryStage(primaryStage, menuScene));
		instructions.setOnAction(e -> setInstructionsScene(primaryStage, back));
		credits.setOnAction(e -> setCreditsScene(primaryStage, back));	
	}
	
	public void setprimaryStage (Stage mStage, Scene altScene)
	{
		mStage.setScene(altScene);
	}
		
	public void setInstructionsScene(Stage mStage, Button back)
	{
		Group instrRoot = new Group();
		Rectangle r = new Rectangle(5000,5000);

		back.setLayoutX(300);
		back.setLayoutY(400);
		back.setStyle("-fx-base: #0099ff");
		back.setTextFill(Color.BLACK);
		
		Text instrText = new Text(25,175,"SPACE to jump\n\n"
										+ "E to punch");
			instrText.setFill(Color.DARKCYAN);
			instrText.setLayoutX(200);
			instrText.setFont(Font.font(null, FontWeight.BOLD, 30));
			
		Scene instructionScene = new Scene(instrRoot, 600, 600);
		instrRoot.getChildren().addAll(r, back, instrText);

		mStage.setScene(instructionScene);
	}
	
	public void setCreditsScene(Stage mStage, Button back)
	{
		Group credRoot = new Group();
		Rectangle r = new Rectangle(5000,5000);
		r.setFill(Color.SALMON);
		
		back.setLayoutX(300);
		back.setLayoutY(400);
		back.setStyle("-fx-base: #22ff00");
		back.setTextFill(Color.BLACK);
		
		Text credText = new Text("CREDITS:\n\n\n"
									+"Brad Eblin\n\n"
									+"Samuel Goldstein\n\n"
									+"Daniel Hannani\n\n"
									+"Nikka Yalung");
		credText.setFill(Color.GREEN);
		credText.setLayoutX(200);
		credText.setFont(Font.font(null, FontWeight.BOLD, 30));
		
		credRoot.getChildren().addAll(r, back, credText);
		Scene creditScene = new Scene(credRoot, 600, 600);
		mStage.setScene(creditScene);
	}
	
	public void displayMenu()
	{
		stage.setScene(menuScene);
		stage.centerOnScreen();
	}
}