package dtu.master_of_creatures;

// Project libraries
import dtu.master_of_creatures.controllers.MenuController;
import dtu.master_of_creatures.utilities.Constants;

// Java libraries
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

// JavaFX libraries
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MasterOfCreaturesApp extends Application
{
    /**
     * @author Danny (s224774)
     */
    public static void main(String[] args)
    {
        launch();
    }

    /**
     * @author Danny (s224774)
     */
    @Override
    public void start(Stage app_stage) throws IOException
    {
        // JavaFX setup
        FXMLLoader sceneLoader = new FXMLLoader(MasterOfCreaturesApp.class.getResource("scenes/MenuScene.fxml")); // load FXML (initial)
        sceneLoader.load(); // set FXML as root

        // Stage setup
        GraphicsDevice screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        app_stage.setTitle("Master of Creatures " + "(" + Constants.getGameVersion() + ")"); // window title
        app_stage.setFullScreen(true); // full screen on start
        app_stage.setMaximized(true); // maximize window (when not in full screen mode)
        app_stage.setResizable(true); // allow resizing window
        app_stage.setWidth(screen.getDisplayMode().getWidth()); // window width
        app_stage.setHeight(screen.getDisplayMode().getHeight()); // window height
        app_stage.setFullScreenExitHint(""); // hint to display when exiting full screen
        app_stage.getIcons().add(new Image(String.valueOf(MasterOfCreaturesApp.class.getResource("media/images/card_back_1.png")))); // app icon

        // Controller setup
        MenuController initial_controller = sceneLoader.getController(); // fetch initial controller
        initial_controller.setAppStage(app_stage); // set stage for initial controller
        initial_controller.gatherScreenInformation(screen); // gather screen information
        initial_controller.goToMenuScene(); // go to the first scene
    }
}
