package application;

import java.io.*;
import java.util.*;

import com.sun.corba.se.spi.orbutil.fsm.Input;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SAP extends Application 
{
	@Override
	public void start(Stage primaryStage) 
	{
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() 
		{
			public void handle(WindowEvent event) 
			{
				System.exit(0);
			}
		});
		new Menu(primaryStage).displayMenu();
		primaryStage.setTitle("Super Awesome Platformer!!!!");
		//layout.setStyle("-fx-background-color: green;")
		primaryStage.show();
	}

	public static void main(String[] args) 
	{		
		launch(args);
	}
}
