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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class HostPregameController extends SceneController implements Initializable
{
    // JavaFX
    @FXML
    private TextField player_1_name;
    @FXML
    private ComboBox<Integer> round_wins;
    @FXML
    private ComboBox<String> turn_time;
    @FXML
    private ComboBox<Integer> health_points;
    @FXML
    private ComboBox<Integer> blood_points;
    @FXML
    private ComboBox<Integer> deck_size;
    @FXML
    private ComboBox<Integer> hand_size;
    @FXML
    private Button sound_button;

    // Game data
    private final GameModel game_model;

    /**
     * @author Danny (s224774)
     */
    public HostPregameController()
    {
        game_model = getGameModel();
    }
    /**
     * @author Danny (s224774)
     */
    public void initialize(URL url, ResourceBundle resource_bundle)
    {
        game_model.setGameState(GameStates.GAME_SETUP);

        // Add and set up GUI option elements
        round_wins.getItems().addAll(1, 2, 3, 4, 5);
        turn_time.getItems().addAll("30 seconds", "60 seconds", "90 seconds", "120 seconds", "Unlimited");
        health_points.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        blood_points.getItems().addAll(0, 1, 2, 3);
        deck_size.getItems().addAll(5, 10, 15, 20, 25);
        hand_size.getItems().addAll(3, 4, 5, 6, 7, 8, 9, 10);

        defaultMatchSettings();
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void defaultMatchSettings()
    {
        player_1_name.setText("Player 1");

        round_wins.getSelectionModel().select(2);
        turn_time.getSelectionModel().select(1);
        health_points.getSelectionModel().select(4);
        blood_points.getSelectionModel().select(0);
        deck_size.getSelectionModel().select(2);
        hand_size.getSelectionModel().select(1);
    }

    /**
     * @author Danny (s224774)
     */
    public void startGame() throws IOException
    {
        goToGameScene();
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
