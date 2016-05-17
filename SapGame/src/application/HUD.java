package application;

import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.image.*;

public class HUD 
{
	Group HUDGroup = new Group();	
	ArrayList<ImageView> healthCount = new ArrayList<ImageView>();
	int healthCountPos;
	int x = 13;
	int y = 11;
	ImageView livesView;
	private static final Image HEARTS = new Image("resources/chocolate.png");
	
	public HUD(Group root, int lifeCount) 
	{
		root.getChildren().add(HUDGroup);
		for (int i = 0; i <= lifeCount-1; i++) 
		{
			livesView = new ImageView(HEARTS);
			livesView.setFitWidth(50);
			livesView.setPreserveRatio(true);
			livesView.setSmooth(true);
	
			healthCount.add(livesView);
			healthCount.trimToSize();

			livesView.setTranslateX(x);
			livesView.setTranslateY(y);

			HUDGroup.getChildren().add(livesView);
			x = x + 35;
		}
		healthCountPos = healthCount.size() - 1;
	}
	
	public void removeHealth() 
	{
		if (healthCountPos > -1) 
		{
			HUDGroup.getChildren().remove(healthCount.get(healthCountPos));
			healthCount.remove(healthCount.size() - 1);
			healthCount.trimToSize();
			healthCountPos--;
			x = x - 35;
		}
	}
	
	public void addHealth() 
	{	
		livesView = new ImageView(HEARTS);
		livesView.setFitWidth(50);
		livesView.setPreserveRatio(true);
		livesView.setSmooth(true);

		healthCount.add(livesView);
		healthCount.trimToSize();
		
		livesView.setTranslateX(x);
		livesView.setTranslateY(y);
		
		HUDGroup.getChildren().add(livesView);
		x = x + 35;
		healthCountPos++;
	}
}