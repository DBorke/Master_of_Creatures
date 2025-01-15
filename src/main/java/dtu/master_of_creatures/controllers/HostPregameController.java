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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class HostPregameController extends SceneController implements Initializable
{
    // JavaFX
    @FXML
    private TextField player_1_name;
    @FXML
    private TextField player_2_name; // temp
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
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void initialize(URL url, ResourceBundle resource_bundle)
    {
        game_model.setGameState(GameStates.GAME_SETUP);

        // Add and set up GUI option elements
        round_wins.getItems().addAll(1, 2, 3, 4, 5); // match setting options
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
        player_2_name.setText("Player 2");

        round_wins.getSelectionModel().select(2); // indices of the combo-boxes
        turn_time.getSelectionModel().select(1);
        health_points.getSelectionModel().select(4);
        blood_points.getSelectionModel().select(0);
        deck_size.getSelectionModel().select(2);
        hand_size.getSelectionModel().select(1);
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void startGame() throws IOException
    {
        // Set up game model
        String turn_time_string = turn_time.getSelectionModel().getSelectedItem();
        int turn_time = 0;

        if(!turn_time_string.equals("Unlimited"))
        {
            for(int char_index = 0; char_index < turn_time_string.length(); char_index++)
            {
                if(!Character.isDigit(turn_time_string.charAt(char_index)))
                {
                    turn_time = Integer.parseInt(turn_time_string.substring(0, char_index));

                    break;
                }
            }
        }
        else
        {
            turn_time = -1; // infinite
        }

        game_model.initializeGame(round_wins.getSelectionModel().getSelectedItem(), turn_time);

        // Set up player models
        List<CardTypes> temp_list = new ArrayList<>();
        temp_list.add(CardTypes.WOLF);

        game_model.initializePlayer(player_1_name.getText(), health_points.getSelectionModel().getSelectedItem(), blood_points.getSelectionModel().getSelectedItem(), deck_size.getSelectionModel().getSelectedItem(), hand_size.getSelectionModel().getSelectedItem(), temp_list, true);
        game_model.initializePlayer(player_2_name.getText(), health_points.getSelectionModel().getSelectedItem(), blood_points.getSelectionModel().getSelectedItem(), deck_size.getSelectionModel().getSelectedItem(), hand_size.getSelectionModel().getSelectedItem(), temp_list, false);

        // Models ready, go to playing scene
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
