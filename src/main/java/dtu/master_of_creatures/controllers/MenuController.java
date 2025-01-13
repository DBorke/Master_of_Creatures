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

public class MenuController extends SceneController implements Initializable
{
    // Game data
    private final GameModel game_model;

    public MenuController()
    {
        game_model = getGameModel();
    }

    public void initialize(URL url, ResourceBundle resource_bundle)
    {
        game_model.setGameState(GameStates.GAME_PENDING);
    }

    public void newGame() throws IOException
    {
        goToPregameScene();
    }
}
