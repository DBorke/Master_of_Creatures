package dtu.master_of_creatures.controllers;

// Project libraries
import dtu.master_of_creatures.models.GameModel;
import dtu.master_of_creatures.utilities.enums.GameStates;

// Java libraries
import java.net.URL;
import java.util.ResourceBundle;
import java.io.IOException;

// JavaFX libraries
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

public class MenuController extends SceneController implements Initializable
{
    // JavaFX
    @FXML
    private Button sound;

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

        playThemeMusic();

        if(!getSoundUnmuted())
        {
            sound.setText("Sound Off");
        }
    }

    /**
     * @author Danny (s224774)
     */
    public void newGame() throws IOException // for host player
    {
        goToHostPregameScene();
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void joinGame() throws IOException // for joining player
    {
        goToJoinPregameScene();
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void quitApplication()
    {
        Platform.exit();
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public void muteUnmuteSound()
    {
        super.muteUnmuteSound();

        sound.setText(getSoundUnmuted() ? "Sound On" : "Sound Off");
    }
}
