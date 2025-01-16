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
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class GameController extends SceneController implements Initializable
{
    // JavaFX
    @FXML
    private Button sound_button;
    @FXML
    private Text turn_time;
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
     * @author Danny (s224774), Maria (s195685), Mathias (s224273), Romel (s215212)
     */
    public void initialize(URL url, ResourceBundle resource_bundle)
    {
        handleTurnTimeUI(game_model.getTurnTimeLimit());

        game_model.startNewRound();
        game_model.setGameState(GameStates.GAME_ACTIVE);
    }

    /**
     * @author Maria (s195685), Danny (s224774), Mathias (s224273), Romel (s215212)
     */
    public void handleTurnTimeUI(int turn_time_remaining)
    {
        turn_time.setText("Turn time left: " + turn_time_remaining);

        // Change text color depending on time left
        if(turn_time_remaining > 10)
        {
            if(turn_time.getFill().equals(Color.RED))
            {
                turn_time.setFill(Color.BLACK); // default color
            }
        }
        else // time is running out
        {
            turn_time.setFill(Color.RED);
        }
    }

    /**
     * @author Maria (s195685), Danny (s224774), Mathias (s224273), Romel (s215212)
     */
    public void handlePlayerInfoUIs(PlayerModel[] players)
    {
        PlayerModel player_1 = players[0];
        PlayerModel player_2 = players[1];

        // Update UI information for player 1
        p1_name.setText(current_player == player_1 ? player_1.getPlayerName() + " (current player)" : player_1.getPlayerName());
        p1_health.setText("Health: " + player_1.getHealthPoints());
        p1_blood_points.setText("Blood points: " + player_1.getBloodPoints());
        p1_remain_deck.setText("Remaining in deck: " + player_1.getCurrentDeck().size());
        p1_remain_hand.setText("Remaining in hand: " + player_1.getCardsInHand().size());

        // Update UI information for player 2 (temp)
        p2_name.setText(current_player == player_2 ? player_2.getPlayerName() + " (current player)" : player_2.getPlayerName());
        p2_health.setText("Health: " + player_2.getHealthPoints());
        p2_blood_points.setText("Blood points: " + player_2.getBloodPoints());
        p2_remain_deck.setText("Remaining in deck: " + player_2.getCurrentDeck().size());
        p2_remain_hand.setText("Remaining in hand: " + player_2.getCardsInHand().size());
    }

    /**
     * @author Maria (s195685), Danny (s224774), Mathias (s224273), Romel (s215212)
     */
    public void playerEndedTurn()
    {
        game_model.endTurn();
    }

    /**
     * @author Maria (s195685), Danny (s224774), Mathias (s224273), Romel (s215212)
     */
    public void playerHasConceded()
    {
        game_model.checkRoundMatchOver(true);
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
