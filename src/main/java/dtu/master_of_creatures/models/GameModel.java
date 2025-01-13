package dtu.master_of_creatures.models;

// Project libraries
import dtu.master_of_creatures.controllers.GameController;
import dtu.master_of_creatures.utilities.enums.GameStates;

public class GameModel
{
    private static GameStates game_state;

    // App
    private GameController game_controller;

    public void initializeGame()
    {
    }

    /////////////////////////
    // setters and getters //
    /////////////////////////

    public void setGameState(GameStates game_state)
    {
        GameModel.game_state = game_state;
    }

    public void setGameController(GameController game_controller)
    {
        this.game_controller = game_controller;
    }

    public GameStates getGameState()
    {
        return game_state;
    }
}
