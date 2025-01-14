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
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class JoinPregameController extends SceneController implements Initializable
{
    // JavaFX
    @FXML
    private TextField player_2_name;
    @FXML
    private AnchorPane join_pregame_pane;
    @FXML
    private Button ready_up;
    @FXML
    private Button sound_button;

    // Game data
    private final GameModel game_model;

    /**
     * @author Danny (s224774)
     */
    public JoinPregameController()
    {
        game_model = getGameModel();
    }
    /**
     * @author Danny (s224774)
     */
    public void initialize(URL url, ResourceBundle resource_bundle)
    {
        game_model.setGameState(GameStates.GAME_SETUP);

        // Add GUI option elements
        defaultMatchSettings();
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void defaultMatchSettings()
    {
        player_2_name.setText("Player 2");
    }

    public void ready()
    {
        game_model.createPlayer(player_2_name.getText(), null, false);

        join_pregame_pane.requestFocus();
        ready_up.setDisable(true);
    }

    public void unready()
    {
        ready_up.setDisable(false);
    }

    /**
     * @author Danny (s224774)
     */
    public void quitSetup() throws IOException
    {
        goToMenuScene();
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
