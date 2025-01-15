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

public class GameController extends SceneController implements Initializable
{
    // JavaFX
    @FXML
    private Button sound_button;

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
    }

    /**
     * @author Danny (s224774)
     */
    public void playerSkippedTurn()
    {
        game_model.endTurn();
    }

    /**
     * @author Danny (s224774)
     */
    public void playerConceded()
    {
        game_model.setGameState(GameStates.GAME_HALFTIME);

        game_model.endTurn();
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
