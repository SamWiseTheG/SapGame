/*package application;
import javafx.animation.Animation;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SandboxFX extends Application {

	private static final Image IMAGE = new Image("spritesheet2.png");
	
	private static final int COLUMNS = 2;
	private static final int COUNT = 4;
	private static final int OFFSET_X = 22;
	private static final int OFFSET_Y = 10;
	private static final int WIDTH = 135;
	private static final int HEIGHT = 142;
	
	public static void main (String []args) {
		launch(args);
	}
	
	public void start (Stage primaryStage){
		primaryStage.setTitle("Robot in Motion");
		
		final ImageView imageView = new ImageView(IMAGE);
		imageView.setViewport(new Rectangle2D(OFFSET_X, OFFSET_Y, WIDTH,HEIGHT ));
		
		final Animation animation = new SpriteAnimation(
				imageView, Duration.millis(1000),
				COUNT, COLUMNS, OFFSET_X, OFFSET_Y,
				WIDTH, HEIGHT
		);
		
		animation.setCycleCount(Animation.INDEFINITE);
		animation.play();
		
		primaryStage.setScene(new Scene(new Group(imageView)));
		primaryStage.show();
	}
}
*/