package dtu.master_of_creatures.controllers;

// Project libraries
import dtu.master_of_creatures.models.GameModel;
import dtu.master_of_creatures.utilities.enums.GameStates;
import dtu.master_of_creatures.utilities.enums.CardTypes;

// Java libraries
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
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
    private AnchorPane join_pregame_pane;
    @FXML
    private TextField p2_name;
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

        defaultMatchSettings();
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void defaultMatchSettings()
    {
        p2_name.setText("Player 2");
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void ready()
    {
        List<CardTypes> temp_list = new ArrayList<>();
        temp_list.add(CardTypes.WOLF);

        game_model.initializePlayer(p2_name.getText(), temp_list, false); // needs information from other opponent's app

        join_pregame_pane.requestFocus();
        ready_up.setDisable(true);
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
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
