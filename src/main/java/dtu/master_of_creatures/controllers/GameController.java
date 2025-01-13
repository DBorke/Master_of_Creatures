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

public class GameController extends SceneController implements Initializable
{
    // Game data
    private final GameModel game_model;

    public GameController()
    {
        game_model = getGameModel();
        game_model.setGameController(this);
    }

    public void initialize(URL url, ResourceBundle resource_bundle)
    {
        game_model.initializeGame();
        game_model.setGameState(GameStates.GAME_ACTIVE);
    }

    public void quitGame() throws IOException
    {
        game_model.setGameState(GameStates.GAME_QUIT);

        goToMenuScene();
    }
}
