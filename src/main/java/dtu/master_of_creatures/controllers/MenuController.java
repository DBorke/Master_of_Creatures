package dtu.master_of_creatures.controllers;

// Project libraries
import dtu.master_of_creatures.models.GameModel;
import dtu.master_of_creatures.utilities.enums.GameStates;

// Java libraries
import java.net.URL;
import java.util.ResourceBundle;
import java.io.IOException;

// JavaFX libraries
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MenuController extends SceneController implements Initializable
{
    // JavaFX
    @FXML
    private Button sound_button;

    // Game data
    private final GameModel game_model;

    /**
     * @author Danny (s224774)
     */
    public MenuController()
    {
        game_model = getGameModel();
    }

    /**
     * @author Danny (s224774)
     */
    public void initialize(URL url, ResourceBundle resource_bundle)
    {
        game_model.setGameState(GameStates.GAME_PENDING);
    }

    /**
     * @author Danny (s224774)
     */
    public void newGame() throws IOException
    {
        goToPregameScene();
    }

    /**
     * @author Danny (s224774)
     */
    public void muteSound()
    {
        super.muteSound();

        sound_button.setText(getSoundUnmuted() ? "Sound On" : "Sound Off");
    }
}
