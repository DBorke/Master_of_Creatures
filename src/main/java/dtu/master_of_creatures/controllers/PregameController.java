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

public class PregameController extends SceneController implements Initializable
{
    // Game data
    private final GameModel game_model;

    public PregameController()
    {
        game_model = getGameModel();
    }

    public void initialize(URL url, ResourceBundle resource_bundle)
    {
        game_model.setGameState(GameStates.GAME_SETUP);
    }

    public void startGame() throws IOException
    {
        goToGameScene();
    }

    public void quitSetup() throws IOException
    {
        goToMenuScene();
    }
}
