package dtu.master_of_creatures.controllers;

// Project libraries
import dtu.master_of_creatures.models.GameModel;
import dtu.master_of_creatures.models.PlayerModel;
import dtu.master_of_creatures.utilities.enums.GameStates;

// Java libraries
import java.net.URL;
import java.util.ResourceBundle;
import java.io.IOException;

// JavaFX libraries
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class GameController extends SceneController implements Initializable
{
    // JavaFX
    @FXML
    private Button end_turn;
    @FXML
    private Button concede;
    @FXML
    private Button sound_button;
    @FXML
    private Text turn_time;
    // Player 1
    @FXML
    private Text p1_name;
    @FXML
    private Text p1_health;
    @FXML
    private Text p1_blood_points;
    @FXML
    private Text p1_remain_deck;
    @FXML
    private Text p1_remain_hand;
    // Player 2
    @FXML
    private Text p2_name;
    @FXML
    private Text p2_health;
    @FXML
    private Text p2_blood_points;
    @FXML
    private Text p2_remain_deck;
    @FXML
    private Text p2_remain_hand;


    // Game data
    private final GameModel game_model;
    private PlayerModel current_player;

    /**
     * @author Danny (s224774)
     */
    public GameController()
    {
        game_model = getGameModel();
        game_model.setGameController(this);
    }

    /**
     * @author Danny (s224774)
     */
    public void initialize(URL url, ResourceBundle resource_bundle)
    {
        game_model.startNewRound();
        game_model.setGameState(GameStates.GAME_ACTIVE);
        turn_time.setText("Turn time left: " + game_model.getTurnTimeLimit());
    }

    public void handleTimeUI(int time)
    {
        turn_time.setText("Turn time left: " + time);
    }

    /**
     * @author Danny (s224774)
     */
    public void playerEndedTurn()
    {
        game_model.endTurn();
    }

    /**
     * @author Danny (s224774)
     */
    public void playerHasConceded()
    {
        game_model.playerConceded();
    }

    /**
     * @author Danny (s224774)
     */
    public void quitGame() throws IOException
    {
        game_model.setGameState(GameStates.GAME_QUIT);

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

    /////////////////////////
    //////// setters ////////
    /////////////////////////

    /**
     * @author Danny (s224774)
     */
    public void setCurrentPlayer(PlayerModel current_player)
    {
        this.current_player = current_player;
    }
}
